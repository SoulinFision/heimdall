/*
 *
 *  *    Copyright 2020-2021 Luter.me
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.luter.heimdall.core.cache;

import com.luter.heimdall.core.exception.CacheException;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 基于Map的缓存实现
 *
 * @param <K> the key parameter
 * @param <V> the value parameter
 * @author Luter
 */
@Slf4j
@Deprecated
public class MapCache<K, V> implements SimpleCache<K, V> {
    /**
     * Backing instance.
     */
    private final Map<K, V> map;

    /**
     * Instantiates a new Map cache.
     *
     * @param backingMap the backing map
     * @since 1.0.2，本地 Map 不支持过期时间设置。 废弃
     */
    @Deprecated
    public MapCache(Map<K, V> backingMap) {
        if (backingMap == null) {
            throw new IllegalArgumentException("Backing map cannot be null.");
        }
        log.debug("初始化 Map 缓存");
        this.map = backingMap;
    }

    @Override
    public V get(K key) throws CacheException {
        log.debug("获取 Map 缓存数据,key:{}", key);
        return map.get(key);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        log.debug("保存 Map 缓存数据,key:{},value:{}", key, value);
        return map.put(key, value);
    }

    @Override
    public V remove(K key) throws CacheException {
        log.debug("删除 Map 缓存数据,key:{}", key);
        return map.remove(key);
    }

    @Override
    public void clear() throws CacheException {
        log.debug("全部清除 Map 缓存数据");
        map.clear();
    }

    @Override
    public int size() {
        log.debug("获取 Map 缓存数据总数");
        return map.size();
    }

    @Override
    public Set<K> keys() {
        log.debug("获取 Map 缓存数据所有 Keys");
        Set<K> keys = map.keySet();
        if (!keys.isEmpty()) {
            return Collections.unmodifiableSet(keys);
        }
        return Collections.emptySet();
    }

    @Override
    public Collection<V> values() {
        log.debug("获取 Map 缓存数据所有 Values");
        Collection<V> values = map.values();
        if (!values.isEmpty()) {
            return Collections.unmodifiableCollection(values);
        }
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return "MapCache '" +
                map.size() +
                " entries)";
    }
}
