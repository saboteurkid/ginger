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
import com.liemlhd.ginger.provider.plural.impl.PluralRule_sl;

/**
 * @author Andriy liemlhd
 */
public class PluralRule_slTest extends BasePluralRuleTest {
    {
        for (int i = 1; i <= 1000; i += 100) {
            add(i, "one");
        }
        for (int i = 2; i <= 1000; i += 100) {
            add(i, "two");
        }

        for (int i = 3; i <= 1000; i += 100) {
            add(i, "few");
            add(i + 1, "few");
        }

        add(0, "other");

        for (int i = 0; i <= 1000; i += 100) {
            for (int j = 5; j <= 100; j++) {
                add(i + j, "other");
            }
        }
    }

    @Override
    protected PluralRule pluralRule() {
        return new PluralRule_sl();
    }
}
