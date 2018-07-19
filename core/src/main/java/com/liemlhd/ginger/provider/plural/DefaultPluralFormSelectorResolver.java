/*
 * Copyright 2013 Andriy liemlhd
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

package com.liemlhd.ginger.provider.plural;


import com.liemlhd.ginger.cache.CacheBuilder;
import com.liemlhd.ginger.cache.CacheLoader;
import com.liemlhd.ginger.cache.LoadingCache;

/**
 * Default {@link PluralFormSelectorResolver} implementation which looks up {@link PluralRule}
 * using {@code languageCode} and using it resolves selector for specified {@code count}.
 *
 * @author Andriy liemlhd
 */
public class DefaultPluralFormSelectorResolver implements PluralFormSelectorResolver {
    private final PluralRuleProvider pluralRuleProvider = new PluralRuleProvider();

    private final LoadingCache<String, PluralRule> pluralRuleCache;

    public DefaultPluralFormSelectorResolver() {
        this.pluralRuleCache = new CacheBuilder<String, PluralRule>()
                .build(new CacheLoader<String, PluralRule>() {
                    @Override
                    public PluralRule load(String languageCode) throws Exception {
                        return pluralRuleProvider.getPluralRule(languageCode);
                    }
                });
    }

    @Override
    public String resolve(String languageCode, int count) {
        return resolvePluralRule(languageCode).select(count);
    }

    private PluralRule resolvePluralRule(String languageCode) {
        return pluralRuleCache.getUnchecked(languageCode);
    }
}
