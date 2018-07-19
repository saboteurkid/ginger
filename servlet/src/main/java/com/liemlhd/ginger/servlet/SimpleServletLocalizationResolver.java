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

package com.liemlhd.ginger.servlet;

import com.liemlhd.ginger.Localization;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;

/**
 * @author Andriy liemlhd
 */
public class SimpleServletLocalizationResolver implements ServletLocalizationResolver {
    @Override
    public Localization resolve(ServletRequest servletRequest, ServletContext servletContext) {
        Localization localization = LocalizationContextUtils.getLocalization(servletRequest);
        if (localization == null) {
            localization = LocalizationContextUtils.getLocalization(servletContext);
        }
        if (localization == null) {
            String message = "Unable to find " + Localization.class.getName() + " in servlet request or servlet " +
                    "context. " + LocalizationContextUtils.class.getName() + " should be used to populate instance of" +
                    Localization.class.getName();
            throw new IllegalStateException(message);
        }

        return localization;
    }
}
