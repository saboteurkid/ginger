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

package com.liemlhd.ginger.cache;

import com.liemlhd.ginger.util.MiscUtils;
import com.liemlhd.ginger.util.Preconditions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class LocalCache {

    static class LocalLoadingCache<K, V> extends AbstractLoadingCache<K, V> {

        private final Map<K, ExpireableValue<V>> map = new HashMap<K, ExpireableValue<V>>();
        private final CacheLoader<? super K, V> cacheLoader;
        private final long expireAfterWriteNanos;

        public LocalLoadingCache(CacheBuilder<? super K, ? super V> cacheBuilder, CacheLoader<? super K, V>  cacheLoader) {
            this.expireAfterWriteNanos = cacheBuilder.expireAfterWriteNanos;
            this.cacheLoader = cacheLoader;
        }

        @Override
        public V get(K key) throws ExecutionException {
            synchronized (map) {
                ExpireableValue<V> v = map.get(key);
                if (v != null && v.isExpired()) {
                    map.remove(key);
                    v = null;
                }
                if (v == null) {
                    try {
                        V value = cacheLoader.load(key);
                        Preconditions.checkNotNull(value);
                        v = new ExpireableValue<V>(value, expireAfterWriteNanos);
                        map.put(key, v);
                    } catch (Throwable e) {
                        throw new ExecutionException(e);
                    }
                }
                return v.getValue();
            }
        }

        @Override
        public V invalidate(K key) {
            ExpireableValue<V> v = null;
            synchronized(map){
                v = map.remove(key);
            }
            return v == null ? null : v.getValue();
        }

        @Override
        public void refresh() {
            synchronized(map){
                map.clear();
            }
        }
    }
}
