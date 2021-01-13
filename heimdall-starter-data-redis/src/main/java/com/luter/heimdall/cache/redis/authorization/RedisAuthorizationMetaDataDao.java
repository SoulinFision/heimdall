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

package com.luter.heimdall.cache.redis.authorization;

import com.luter.heimdall.core.authorization.dao.AuthorizationMetaDataCacheDao;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 redis 的系统权限缓存Dao实现
 *
 * @author luter
 */
@Slf4j
public class RedisAuthorizationMetaDataDao implements AuthorizationMetaDataCacheDao {

    /**
     * The Redis template.
     */
    private final StringRedisTemplate redisTemplate;
    /**
     * The Cache key.
     */
    private final String cacheKey;

    /**
     * Instantiates a new Redis authorization meta data dao.
     *
     * @param redisTemplate the redis template
     */
    public RedisAuthorizationMetaDataDao(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        String key = ConfigManager.getConfig().getAuthoritiesCachedKey();
        cacheKey = StrUtils.isBlank(key) ? AUTHORITIES_CACHED_KEY : key;
    }

    @Override
    public Map<String, String> getSysAuthorities() {
        Map<String, String> authorityMap = new LinkedHashMap<>();
        final Map<Object, Object> entries = redisTemplate.opsForHash().entries(getCacheKey());
        if (!entries.isEmpty()) {
            entries.forEach((key, grantedAuthority) -> authorityMap.put(key.toString(), grantedAuthority.toString()));
        }
        log.debug("获取缓存系统权限:\n{}", authorityMap);
        return authorityMap;
    }

    @Override
    public void setSysAuthorities(Map<String, String> authorities) {
        log.debug("设置缓存系统权限:\n{}", authorities);
        redisTemplate.opsForHash().putAll(getCacheKey(), authorities);
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
