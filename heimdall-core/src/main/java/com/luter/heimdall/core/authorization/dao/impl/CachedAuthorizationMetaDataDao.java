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

package com.luter.heimdall.core.authorization.dao.impl;

import com.luter.heimdall.core.authorization.dao.AuthorizationMetaDataCacheDao;
import com.luter.heimdall.core.cache.SimpleCache;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 基于内存缓存(如 ehcache、Map、caffeine等)的系统权限Dao实现
 *
 * @author luter
 */
@Slf4j
public class CachedAuthorizationMetaDataDao implements AuthorizationMetaDataCacheDao {

    /**
     * The Cache.
     */
    private final SimpleCache<String, Map<String, String>> cache;


    /**
     * The Cache key.
     */
    private final String cacheKey;

    /**
     * Instantiates a new Cached authorization dao.
     *
     * @param cache the cache
     */
    public CachedAuthorizationMetaDataDao(SimpleCache<String, Map<String, String>> cache) {
        this.cache = cache;
        String key = ConfigManager.getConfig().getAuthoritiesCachedKey();
        cacheKey = StrUtils.isBlank(key) ? AUTHORITIES_CACHED_KEY : key;
    }


    @Override
    public Map<String, String> getSysAuthorities() {
        return cache.get(getCacheKey());
    }

    @Override
    public void setSysAuthorities(Map<String, String> authorities) {
        cache.put(getCacheKey(), authorities);
    }

    @Override
    public void resetCachedSysAuthorities(Map<String, String> authorities) {
        cache.remove(getCacheKey());
        setSysAuthorities(authorities);
    }


    /**
     * Gets cache key.
     *
     * @return the cache key
     */
    public String getCacheKey() {
        return cacheKey;
    }


}
