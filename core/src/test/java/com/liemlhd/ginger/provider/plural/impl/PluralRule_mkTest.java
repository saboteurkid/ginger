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
import com.liemlhd.ginger.provider.plural.impl.PluralRule_mk;

/**
 * @author Andriy liemlhd
 */
public class PluralRule_mkTest extends BasePluralRuleTest {
    {
        add(1, "one");
        for (int i = 21; i <= 1000; i += 10) {
            add(i, "one");
        }

        add(0, "other");
        for (int i = 2; i <= 20; i++) {
            add(i, "other");
        }
        for (int i = 20; i <= 1000; i += 10) {
            for (int j = 2; j <= 10; j++) {
                add(i + j, "other");
            }
        }
    }

    @Override
    protected PluralRule pluralRule() {
        return new PluralRule_mk();
    }
}
