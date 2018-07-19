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

/**
 * Implementations responsible for resolving plural form selectors based on {@code languageCode} and {@code count}.
 *
 * @author Andriy liemlhd
 */
public interface PluralFormSelectorResolver {
    /**
     * Resolve plural form selector for specified {@code languageCode} and {@code count}.
     *
     * @param languageCode, must be not {@code null}
     * @param count,        must be {@code >= 0}
     * @return not {@code null} plural form selector
     */
    String resolve(String languageCode, int count);
}
