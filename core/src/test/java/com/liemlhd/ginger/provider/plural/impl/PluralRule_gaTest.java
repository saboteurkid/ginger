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
import com.liemlhd.ginger.provider.plural.impl.PluralRule_ga;

/**
 * @author Andriy liemlhd
 */
public class PluralRule_gaTest extends BasePluralRuleTest {
    {
        add(0, "other");
        add(1, "one");
        add(2, "two");
        add(3, "few");
        add(4, "few");
        add(5, "few");
        add(6, "few");
        add(7, "many");
        add(8, "many");
        add(9, "many");
        add(10, "many");
        for (int i = 11; i <= 1000; i++) {
            add(i, "other");
        }
    }

    @Override
    protected PluralRule pluralRule() {
        return new PluralRule_ga();
    }
}
