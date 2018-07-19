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

package com.liemlhd.ginger.locale;

import com.liemlhd.ginger.LocaleResolver;

import java.util.Locale;

/**
 * The basic implementation of {@code LocaleResolver} which uses {@link java.util.Locale#getDefault()} to resolve
 * current locale. It can be used by desktop applications.
 *
 * @author Andriy liemlhd
 */
public class DefaultLocaleResolver implements LocaleResolver {
    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }
}
