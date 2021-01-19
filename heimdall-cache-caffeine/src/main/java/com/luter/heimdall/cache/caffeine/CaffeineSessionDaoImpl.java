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
import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.cache.SimpleCache;
import com.luter.heimdall.core.config.Config;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.cookie.CookieService;
import com.luter.heimdall.core.servlet.ServletHolder;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.core.session.dao.impl.CachedSessionDaoImpl;
import com.luter.heimdall.core.session.generator.UUIDSessionIdGeneratorImpl;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;

/**
 * 基于 Caffeine 的 Session 缓存Dao
 *
 * @author Luter
 * @since 1.0.2
 */
@Slf4j
public class CaffeineSessionDaoImpl extends CachedSessionDaoImpl {
    /**
     * 基于 Caffeine 的 Session 缓存Dao
     *
     * @param servletHolder the servlet holder
     * @param cookieService the cookie service
     * @since 1.0.2
     */
    public CaffeineSessionDaoImpl(ServletHolder servletHolder, CookieService cookieService) {
        super();
        final Config config = ConfigManager.getConfig();
        Cache<String, SimpleSession> sessionCache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofSeconds(config.getSession().getGlobalSessionTimeout()))
                .build();
        Cache<String, List<? extends GrantedAuthority>> userAuthCache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofHours(config.getAuthority().getUserExpire()))
                .build();
        //Session 缓存
        SimpleCache<String, SimpleSession> simSessionCache = new CaffeineCache<>(sessionCache);
        //Session(用户) 权限缓存
        SimpleCache<String, List<? extends GrantedAuthority>> simpleUserAuthCache = new CaffeineCache<>(userAuthCache);
        this.setSessionCache(simSessionCache);
        this.setUserAuthCache(simpleUserAuthCache);
        this.setServletHolder(servletHolder);
        this.setCookieService(cookieService);
        this.setSessionIdGenerator(new UUIDSessionIdGeneratorImpl());
    }
}
