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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public final class FileSystemResourceLoader extends AbstractResourceLoader {
    private static final String SCHEMA = "file";

    public FileSystemResourceLoader() {
        super(SCHEMA);
    }

    @Override
    protected InputStream openResource(String path) {
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            // return null according to interface contract
            return null;
        }
    }
}
