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

package com.liemlhd.ginger.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.liemlhd.ginger.util.Preconditions.checkArgument;
import static com.liemlhd.ginger.util.Preconditions.checkNotNull;

public abstract class AbstractResourceLoader implements ResourceLoader {
    private static final String PATTERN_SUFFIX = ":(.+)";
    private final Pattern locationPattern;

    public AbstractResourceLoader(String schema) {
        this.locationPattern = createLocationPattern(schema);
    }

    protected abstract InputStream openResource(String path) throws IOException;

    @Override
    public final boolean isSupported(String location) {
        checkNotNull(location);
        return matcher(location).matches();
    }

    @Override
    public final InputStream openStream(String location) throws IOException {
        checkNotNull(location);
        Matcher matcher = matcher(location);
        checkArgument(matcher.matches(), "Unsupported location");
        String path = matcher.group(1);

        return openResource(path);
    }

    private Matcher matcher(String location) {
        return locationPattern.matcher(location);
    }

    private static Pattern createLocationPattern(String schema) {
        return Pattern.compile(schema + PATTERN_SUFFIX);
    }
}
