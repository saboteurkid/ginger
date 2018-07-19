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
 * Concrete language implementations defines plural rules.
 *
 * Used for resolving plural qualifier based on count.
 *
 * @author Andriy liemlhd
 */
public interface PluralRule {
    /**
     * Return plural form qualifier appropriate for given {@code count}.
     *
     * @param count number to get qualifier for
     * @return plural qualifier
     */
    String select(int count);
}
