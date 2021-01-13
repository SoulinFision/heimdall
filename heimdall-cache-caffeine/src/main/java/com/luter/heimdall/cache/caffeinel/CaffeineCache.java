/*
 *    Copyright 2020-2021 Luter.me
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.luter.heimdall.cache.caffeinel;


import com.luter.heimdall.core.cache.SimpleCache;
import com.luter.heimdall.core.exception.CacheException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Set;

/**
 * The type Caffeine cache.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author Luter
 */
@Slf4j
public class CaffeineCache<K, V> implements SimpleCache<K, V> {

    /**
     * The Cache.
     */
    private final com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    /**
     * Instantiates a new Caffeine cache.
     *
     * @param cache the cache
     */
    public CaffeineCache(com.github.benmanes.caffeine.cache.Cache<K, V> cache) {
        this.cache = cache;
        log.warn("初始化 Caffeine Cache 完毕");
    }


    @Override
    public V get(K key) throws CacheException {
        log.debug("获取 Caffeine 缓存数据,key:{}", key);
        return this.cache.getIfPresent(key);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        log.debug("保存 Caffeine 缓存数据,key:{},value:{}", key, value);
        this.cache.put(key, value);
        return value;
    }

    @Override
    public V remove(K key) throws CacheException {
        log.debug("删除 Caffeine 缓存数据,key:{}", key);
        final V previous = get(key);
        this.cache.invalidate(key);
        return previous;
    }

    @Override
    public void clear() throws CacheException {
        log.debug("全部清除 Caffeine 缓存数据");
        this.cache.invalidateAll();
    }

    @Override
    public int size() {
        log.debug("获取 Caffeine 缓存数据总数");
        return this.cache.asMap().size();
    }

    @Override
    public Set<K> keys() {
        log.debug("获取 Caffeine 缓存数据所有 key");
        return this.cache.asMap().keySet();
    }

    @Override
    public Collection<V> values() {
        log.debug("获取缓存数据所有 value");
        return this.cache.asMap().values();
    }
}
