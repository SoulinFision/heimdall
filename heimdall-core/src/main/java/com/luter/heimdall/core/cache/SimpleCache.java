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

package com.luter.heimdall.core.cache;


import com.luter.heimdall.core.exception.CacheException;

import java.util.Collection;
import java.util.Set;

/**
 * 通用内存缓存接口定义
 *
 * @param <K> the key parameter
 * @param <V> the value parameter
 * @author Luter
 */
public interface SimpleCache<K, V> {
    /**
     * 根据Key获取缓存
     *
     * @param key the key
     * @return the v
     * @throws CacheException the cache exception
     */
    V get(K key) throws CacheException;

    /**
     * 存入缓存
     *
     * @param key   the key
     * @param value the value
     * @return the v
     * @throws CacheException the cache exception
     */
    V put(K key, V value) throws CacheException;

    /**
     * 删除缓存数据
     *
     * @param key the key
     * @return the v
     * @throws CacheException the cache exception
     */
    V remove(K key) throws CacheException;

    /**
     * 清空缓存
     *
     * @throws CacheException the cache exception
     */
    void clear() throws CacheException;

    /**
     * 获取 缓存当前数量
     *
     * @return the int
     */
    int size();

    /**
     * 获取缓存中所有的keys
     *
     * @return the set
     */
    Set<K> keys();

    /**
     * 获取缓存中所有的值
     *
     * @return the collection
     */
    Collection<V> values();
}
