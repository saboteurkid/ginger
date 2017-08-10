/*
 * Copyright 2013 Andriy Vityuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vityuk.ginger.provider;

import com.vityuk.ginger.LocaleResolver;
import com.vityuk.ginger.PropertyResolver;
import com.vityuk.ginger.cache.CacheBuilder;
import com.vityuk.ginger.cache.CacheLoader;
import com.vityuk.ginger.cache.LoadingCache;
import com.vityuk.ginger.loader.LocalizationLoader;
import com.vityuk.ginger.loader.ResourceLoader;
import com.vityuk.ginger.provider.format.MessageFormatFactory;
import com.vityuk.ginger.provider.plural.PluralFormSelectorResolver;
import com.vityuk.ginger.util.MiscUtils;
import com.vityuk.ginger.cache.ThreadLocalLoadingCache;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.vityuk.ginger.util.Preconditions.checkNotNull;

/**
 * @author Andriy Vityuk
 */
public class DefaultLocalizationProvider implements LocalizationProvider {

    public static final char LOCALE_SEPARATOR = '_';
    public static final char FILE_EXTENSION_SEPARATOR = '.';

    private static final String EMPTY_SELECTOR = "";
    private static final MessageFormat NULL_MESSAGE_FORMAT = new MessageFormat("");

    private final LocaleResolver localeResolver;
    private final ResourceLoader resourceLoader;
    private final LocalizationLoader localizationLoader;
    private final List<String> locations;
    private final MessageFormatFactory messageFormatFactory;
    private final PluralFormSelectorResolver pluralFormSelectorResolver;

    private final LoadingCache<Locale, PropertyResolver> propertyResolverCache;
    private final LoadingCache<MessageKey, MessageFormat> messageFormatCache;

    private DefaultLocalizationProvider(Builder builder) {
        localeResolver = checkNotNull(builder.localeResolver);
        resourceLoader = checkNotNull(builder.resourceLoader);
        localizationLoader = checkNotNull(builder.localizationLoader);
        locations = checkNotNull(builder.locations);
        messageFormatFactory = checkNotNull(builder.messageFormatFactory);
        pluralFormSelectorResolver = checkNotNull(builder.pluralFormSelectorResolver);

        propertyResolverCache = createPropertyResolverCache(builder, new CacheLoader<Locale, PropertyResolver>() {
            @Override
            public PropertyResolver load(Locale locale) throws Exception {
                return createPropertyResolver(locale);
            }
        });

        messageFormatCache = createMessageFormatCache(builder, new CacheLoader<MessageKey, MessageFormat>() {
            @Override
            public MessageFormat load(MessageKey key) throws Exception {
                return createMessageFormat(key.getLocale(), key.getKey(), key.getSelector());
            }
        });
    }

    @Override
    public String getString(String key) {
        return getPropertyResolver().getString(checkNotNull(key));
    }

    @Override
    public Boolean getBoolean(String key) {
        return getPropertyResolver().getBoolean(checkNotNull(key));
    }

    @Override
    public Integer getInteger(String key) {
        return getPropertyResolver().getInteger(checkNotNull(key));
    }

    @Override
    public Long getLong(String key) {
        return getPropertyResolver().getLong(checkNotNull(key));
    }

    @Override
    public Float getFloat(String key) {
        return getPropertyResolver().getFloat(checkNotNull(key));
    }

    @Override
    public Double getDouble(String key) {
        return getPropertyResolver().getDouble(checkNotNull(key));
    }

    @Override
    public List<String> getStringList(String key) {
        return getPropertyResolver().getStringList(checkNotNull(key));
    }

    @Override
    public Map<String, String> getStringMap(String key) {
        return getPropertyResolver().getStringMap(checkNotNull(key));
    }

    @Override
    public String getMessage(String key, Object... parameters) {
        return formatMessage(getMessageFormat(key), parameters);
    }

    @Override
    public MessageFormat getMessageFormat(String key) {
        return getSelectedMessageFormat(key, EMPTY_SELECTOR);
    }

    @Override
    public String getSelectedMessage(String key, String selector, Object... parameters) {
        return formatMessage(getSelectedMessageFormat(key, selector), parameters);
    }

    @Override
    public MessageFormat getSelectedMessageFormat(String key, String selector) {
        checkNotNull(key);
        checkNotNull(selector);
        Locale locale = getCurrentLocale();

        MessageFormat messageFormat = getMessageFormat(locale, key, selector);
        if (messageFormat == null && !isEmptySelector(selector)) {
            // Fallback to message without selector
            messageFormat = getMessageFormat(locale, key, EMPTY_SELECTOR);
        }
        // Should not use recursive because it can cause infinite loop.
        if (messageFormat == null && !locale.equals(localeResolver.getFallbackLocale())) {
            locale = localeResolver.getFallbackLocale();
            messageFormat = getMessageFormat(locale, key, selector);
            if (messageFormat == null && !isEmptySelector(selector)) {
                // Fallback to message without selector
                messageFormat = getMessageFormat(locale, key, EMPTY_SELECTOR);
            }
        }
        return messageFormat;
    }

    @Override
    public String getPluralMessage(String key, Number count, Object... parameters) {
        MessageFormat messageFormat = getPluralMessageFormat(checkNotNull(key), count);
        return formatMessage(messageFormat, mergeParameters(count, parameters));
    }

    private PropertyResolver getPropertyResolver() {
        Locale locale = getCurrentLocale();
        return getPropertyResolver(locale);
    }

    private Locale getCurrentLocale() {
        return checkNotNull(localeResolver.getLocale(), "LocaleResolver must return not null Locale");
    }

    private String getPluralFormSelector(Locale locale, Number count) {
        return pluralFormSelectorResolver.resolve(locale.getLanguage(), count.intValue());
    }

    private PropertyResolver getPropertyResolver(Locale locale) {
        try {
            return propertyResolverCache.getUnchecked(locale);
        } catch (RuntimeException e) {
            throw MiscUtils.propagate(e.getCause());
        }
    }

    private MessageFormat getMessageFormat(Locale locale, String key, String selector) {
        try {
            MessageKey messageKey = new MessageKey(locale, key, selector);
            MessageFormat messageFormatOptional = messageFormatCache.getUnchecked(messageKey);
            if (messageFormatOptional == NULL_MESSAGE_FORMAT) {
                return null;
            }
            return messageFormatOptional;
        } catch (RuntimeException e) {
            throw MiscUtils.propagate(e.getCause());
        }
    }

    private PropertyResolver createPropertyResolver(Locale locale) {
        List<PropertyResolver> propertyResolvers = new ArrayList<PropertyResolver>(locations.size());
        for (String location : locations) {
            propertyResolvers.add(createPropertyResolver(location, locale));
        }
        return createMultiPropertyResolver(propertyResolvers);
    }

    public MessageFormat getPluralMessageFormat(String key, Number count) {
        Locale locale = getCurrentLocale();

        MessageFormat messageFormat = null;
        if (count.intValue() == 0 || count.intValue() == 1) {
            // Special cases, allows to define specific message for 0 and 1 count
            String selector = String.valueOf(count);
            messageFormat = getMessageFormat(locale, key, selector);
        }

        if (messageFormat == null) {
            String selector = getPluralFormSelector(locale, count);
            messageFormat = getMessageFormat(locale, key, selector);
        }

        if (messageFormat == null) {
            // Fallback to message without selector
            messageFormat = getMessageFormat(locale, key, EMPTY_SELECTOR);
        }

        return messageFormat;
    }

    private MessageFormat createMessageFormat(Locale locale, String key, String selector) {
        final String format = getMessageFormatString(locale, key, selector);
        if (format == null) {
            return NULL_MESSAGE_FORMAT;
        }
        return messageFormatFactory.create(locale, format);
    }

    private String getMessageFormatString(Locale locale, String key, String selector) {
        PropertyResolver propertyResolver = getPropertyResolver(locale);
        if (isEmptySelector(selector)) {
            return propertyResolver.getString(key);
        }

        Map<String, String> propertyMap = propertyResolver.getStringMap(key);
        return propertyMap == null ? null : propertyMap.get(selector);
    }

    private PropertyResolver createPropertyResolver(String location, Locale locale) {
        InputStream inputStream = openLocation(location, locale);
        try {
            return loadLocalization(location, inputStream);
        } finally {
            MiscUtils.closeQuietly(inputStream);
        }
    }

    private InputStream openLocation(String location, Locale locale) {
        if (!resourceLoader.isSupported(location)) {
            throw new UnsupportedLocationException(location);
        }
        return findResourceForLocale(location, locale);
    }

    private InputStream findResourceForLocale(String location, Locale locale) {
        int idx = location.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        if (idx == -1) {
            throw new RuntimeException("Localization resource should have extension in location: '" + location + "'");
        }
        String prefix = location.substring(0, idx);
        String suffix = location.substring(idx);

        for (Locale candidateLocale : createCandidateLocales(locale)) {
            String localizedLocation = createLocalizedLocation(prefix, suffix, candidateLocale);
            InputStream inputStream = openStream(localizedLocation);
            if (inputStream != null) {
                return inputStream;
            }
        }

        throw new ResourceNotFoundException(location, locale);
    }

    private InputStream openStream(String location) {
        try {
            return resourceLoader.openStream(location);
        } catch (IOException e) {
            throw new RuntimeException("Unable to open location: '" + location + "'", e);
        }
    }

    private PropertyResolver loadLocalization(String location, InputStream inputStream) {
        try {
            return localizationLoader.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load localization resource: '" + location + "'", e);
        }
    }

    private String formatMessage(MessageFormat messageFormat, Object[] obj) {
        return messageFormat == null ? null : messageFormat.format(obj);
    }

    public static Builder builder() {
        return new Builder();
    }

    private static LoadingCache<MessageKey, MessageFormat> createMessageFormatCache(Builder builder,
            CacheLoader<MessageKey, MessageFormat> cacheLoader) {
        if (builder.maxCacheTimeInSec == -1) {
            return ThreadLocalLoadingCache.create(cacheLoader);
        } else {
            return ThreadLocalLoadingCache.create(cacheLoader, builder.maxCacheTimeInSec, TimeUnit.SECONDS);
        }
    }

    private static LoadingCache<Locale, PropertyResolver> createPropertyResolverCache(Builder builder,
            CacheLoader<Locale, PropertyResolver> cacheLoader) {
        CacheBuilder<Object, Object> cacheBuilder = new CacheBuilder<Object, Object>();
        if (builder.maxCacheTimeInSec >= 0) {
            cacheBuilder.expireAfterWrite(builder.maxCacheTimeInSec, TimeUnit.SECONDS);
        }
        return cacheBuilder.build(cacheLoader);
    }

    private static List<Locale> createCandidateLocales(Locale locale) {
        List<Locale> locales = new ArrayList<Locale>(2);

        if (!locale.getCountry().isEmpty()) {
            locales.add(locale);
        }
        if (!locale.getLanguage().isEmpty()) {
            locales.add(locales.isEmpty() ? locale : new Locale(locale.getLanguage(), "", ""));
        }
        locales.add(Locale.ROOT);

        return locales;
    }

    private static String createLocalizedLocation(String locationPrefix, String locationSuffix, Locale locale) {
        StringBuilder builder = new StringBuilder(locationPrefix.length() + locationSuffix.length() + 6);

        builder.append(locationPrefix);
        if (!locale.getLanguage().isEmpty()) {
            builder.append(LOCALE_SEPARATOR);
            builder.append(locale.getLanguage());
        }
        if (!locale.getCountry().isEmpty()) {
            builder.append(LOCALE_SEPARATOR);
            builder.append(locale.getCountry());
        }
        builder.append(locationSuffix);

        return builder.toString();
    }

    private static PropertyResolver createMultiPropertyResolver(List<PropertyResolver> propertyResolvers) {
        if (propertyResolvers.size() == 1) {
            return propertyResolvers.get(0);
        }
        return new ChainedPropertyResolver(propertyResolvers);
    }

    private static Object[] mergeParameters(Number count, Object[] parameters) {
        Object[] mergedParameters = new Object[parameters.length + 1];
        mergedParameters[0] = count;
        System.arraycopy(parameters, 0, mergedParameters, 1, parameters.length);
        return mergedParameters;
    }

    private static boolean isEmptySelector(String selector) {
        return selector.isEmpty();
    }

    public static class Builder {

        private LocaleResolver localeResolver;
        private ResourceLoader resourceLoader;
        private LocalizationLoader localizationLoader;
        private List<String> locations;
        private MessageFormatFactory messageFormatFactory;
        private PluralFormSelectorResolver pluralFormSelectorResolver;
        private int maxCacheTimeInSec = -1;

        public Builder withLocaleResolver(LocaleResolver localeResolver) {
            this.localeResolver = localeResolver;
            return this;
        }

        public Builder withResourceLoader(ResourceLoader resourceLoader) {
            this.resourceLoader = resourceLoader;
            return this;
        }

        public Builder withLocalizationLoader(LocalizationLoader localizationLoader) {
            this.localizationLoader = localizationLoader;
            return this;
        }

        public Builder withLocations(List<String> locations) {
            this.locations = locations;
            return this;
        }

        public Builder withMessageFormatFactory(MessageFormatFactory messageFormatFactory) {
            this.messageFormatFactory = messageFormatFactory;
            return this;
        }

        public Builder withPluralFormSelectorResolver(PluralFormSelectorResolver pluralFormSelectorResolver) {
            this.pluralFormSelectorResolver = pluralFormSelectorResolver;
            return this;
        }

        public Builder withMaxCacheTimeInSec(int maxCacheTimeInSec) {
            this.maxCacheTimeInSec = maxCacheTimeInSec;
            return this;
        }

        public DefaultLocalizationProvider build() {
            return new DefaultLocalizationProvider(this);
        }
    }

    private static final class MessageKey {

        private final Locale locale;
        private final String key;
        private final String selector;

        public MessageKey(Locale locale, String key, String selector) {
            this.locale = locale;
            this.key = key;
            this.selector = selector;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(new Object[]{key, selector, locale});
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MessageKey that = (MessageKey) o;
            return MiscUtils.equal(key, that.key) && MiscUtils.equal(selector, selector)
                    && MiscUtils.equal(locale, that.locale);
        }

        public Locale getLocale() {
            return locale;
        }

        public String getKey() {
            return key;
        }

        public String getSelector() {
            return selector;
        }
    }
}
