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

package com.luter.heimdall.cache.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.luter.heimdall.core.authorization.dao.impl.CachedAuthorizationMetaDataDao;
import com.luter.heimdall.core.cache.SimpleCache;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.config.property.AuthorityProperty;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Collection;
import java.util.Map;

/**
 * 基于 Caffeine 的系统权限缓存 Dao
 *
 * @author Luter
 * @since 1.0.2
 */
@Slf4j
public class CaffeineAuthorizationMetaDataDao extends CachedAuthorizationMetaDataDao {

    /**
     * 基于 Caffeine 的系统权限缓存 Dao
     *
     * @since 1.0.2
     */
    public CaffeineAuthorizationMetaDataDao() {
        super();
        final AuthorityProperty config = ConfigManager.getConfig().getAuthority();
        Cache<String, Map<String, Collection<String>>> caffeineCache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofHours(config.getSysExpire())).build();
        SimpleCache<String, Map<String, Collection<String>>> caffeineSimpleCache = new CaffeineCache<>(caffeineCache);
        this.setSysCache(caffeineSimpleCache);
        String sysKey = config.getSysCachedKey();
        this.setSysCacheKey(StrUtils.isBlank(sysKey) ? SYS_AUTHORITIES_CACHED_KEY : sysKey);
    }
}
