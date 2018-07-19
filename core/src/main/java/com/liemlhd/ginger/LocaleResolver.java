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
package com.liemlhd.ginger;

import java.util.Locale;
import java.util.function.Supplier;

/**
 * Implementations responsible for resolving current {@link Locale}.
 *
 * For example, desktop application might get it just from
 * {@link java.util.Locale#getDefault()}. In contrast web application might need
 * to get it from current request context.
 */
public interface LocaleResolver {

    /**
     * Resolve current {@code Locale}.
     *
     * @return resolved locale or default value, must never return {@code null}
     */
    Locale getLocale();

    default Locale getFallbackLocale() {
        return getLocale();
    }

    static LocaleResolver of(Supplier<Locale> localeSupplier, Supplier<Locale> fallbackLocaleSupplier) {
        return new LocaleResolver() {
            @Override
            public Locale getLocale() {
                return localeSupplier.get();
            }

            @Override
            public Locale getFallbackLocale() {
                return fallbackLocaleSupplier.get();
            }

        };
    }
}
