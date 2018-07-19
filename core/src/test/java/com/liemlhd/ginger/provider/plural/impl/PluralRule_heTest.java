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

package com.liemlhd.ginger.provider.plural.impl;

import com.liemlhd.ginger.provider.plural.PluralRule;
import com.liemlhd.ginger.provider.plural.impl.PluralRule_he;

/**
 * @author Andriy liemlhd
 */
public class PluralRule_heTest extends BasePluralRuleTest {
    {
        add(1, "one");

        add(2, "two");

        for (int i = 10; i <= 1000; i += 10) {
            add(i, "many");
        }

        add(0, "other");
        for (int i = 3; i<=9; i++) {
            add(i, "other");
        }
        for (int i = 10; i<=1000; i+=10) {
            for (int j = 1; j <= 9; j++) {
                add(i + j, "other");
            }
        }
    }

    @Override
    protected PluralRule pluralRule() {
        return new PluralRule_he();
    }
}
