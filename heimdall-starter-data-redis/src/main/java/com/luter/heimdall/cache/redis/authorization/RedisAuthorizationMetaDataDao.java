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

package com.luter.heimdall.cache.redis.authorization;

import com.luter.heimdall.core.authorization.dao.AuthorizationMetaDataCacheDao;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private final RedisTemplate<String, Collection<String>> redisTemplate;
    /**
     * The Cache key.
     */
    private final String cacheKey;
    /**
     * 多久过期
     */
    private long expire;

    /**
     * 授权信息 Redis 缓存实现
     *
     * @param redisTemplate 系统权限 redisTemplate
     */
    public RedisAuthorizationMetaDataDao(RedisTemplate<String, Collection<String>> redisTemplate) {
        this.redisTemplate = redisTemplate;
        String key = ConfigManager.getConfig().getAuthority().getSysCachedKey();
        cacheKey = StrUtils.isBlank(key) ? SYS_AUTHORITIES_CACHED_KEY : key;
        expire = ConfigManager.getConfig().getAuthority().getSysExpire();
    }

    @Override
    public void clearSysAuthorities() {
        redisTemplate.delete(getCacheKey());
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Collection<String>> getSysAuthorities() {
        Map<String, Collection<String>> authorityMap = new LinkedHashMap<>();
        final Map<Object, Object> entries = redisTemplate.opsForHash().entries(getCacheKey());
        if (!entries.isEmpty()) {
            entries.forEach((key, grantedAuthority) -> authorityMap.put(key.toString(), (Collection<String>) grantedAuthority));
        }
        log.debug("获取缓存系统权限:\n{}", authorityMap);
        return authorityMap;
    }

    @Override
    public void setSysAuthorities(Map<String, Collection<String>> authorities) {
        log.debug("设置缓存系统权限:\n{}", authorities);
        redisTemplate.opsForHash().putAll(getCacheKey(), authorities);
        redisTemplate.expire(getCacheKey(), getExpire(), TimeUnit.HOURS);
    }

    @Override
    public void resetCachedSysAuthorities(Map<String, Collection<String>> authorities) {
        clearSysAuthorities();
        setSysAuthorities(authorities);
    }


    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
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
