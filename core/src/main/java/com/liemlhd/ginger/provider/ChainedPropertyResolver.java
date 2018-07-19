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

package com.liemlhd.ginger.provider;

import com.liemlhd.ginger.PropertyResolver;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.liemlhd.ginger.util.Preconditions.checkNotNull;

/**
 * This implementation aggregates multiple underlying property resolvers.
 * <p/>
 * Each lookup methods delegates calls to underlying property resolver until property found.
 *
 * @author Andriy liemlhd
 */
class ChainedPropertyResolver implements PropertyResolver {
    private final Collection<PropertyResolver> propertyResolvers;

    public ChainedPropertyResolver(Collection<PropertyResolver> propertyResolvers) {
        this.propertyResolvers = checkNotNull(propertyResolvers);
    }

    @Override
    public String getString(String key) {
        for (PropertyResolver propertyResolver : propertyResolvers) {
            String value = propertyResolver.getString(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    @Override
    public Boolean getBoolean(String key) {
        for (PropertyResolver propertyResolver : propertyResolvers) {
            Boolean value = propertyResolver.getBoolean(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    @Override
    public Integer getInteger(String key) {
        for (PropertyResolver propertyResolver : propertyResolvers) {
            Integer value = propertyResolver.getInteger(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    @Override
    public Long getLong(String key) {
        for (PropertyResolver propertyResolver : propertyResolvers) {
            Long value = propertyResolver.getLong(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    @Override
    public Float getFloat(String key) {
        for (PropertyResolver propertyResolver : propertyResolvers) {
            Float value = propertyResolver.getFloat(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }


    @Override
    public Double getDouble(String key) {
        for (PropertyResolver propertyResolver : propertyResolvers) {
            Double value = propertyResolver.getDouble(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public List<String> getStringList(String key) {
        for (PropertyResolver propertyResolver : propertyResolvers) {
            List<String> value = propertyResolver.getStringList(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getStringMap(String key) {
        for (PropertyResolver propertyResolver : propertyResolvers) {
            Map<String, String> value = propertyResolver.getStringMap(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Set<String> getKeys() {
        Set<String> keys = new HashSet<String>();
        for (PropertyResolver propertyResolver : propertyResolvers) {
            keys.addAll(propertyResolver.getKeys());
        }
        return keys;
    }
}
