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

package com.luter.heimdall.sample.simple.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Maps;
import com.luter.heimdall.cache.caffeinel.CaffeineCache;
import com.luter.heimdall.core.authorization.aspect.AuthorizationAnnotationAspect;
import com.luter.heimdall.core.authorization.dao.AuthorizationMetaDataCacheDao;
import com.luter.heimdall.core.authorization.dao.impl.CachedAuthorizationMetaDataDao;
import com.luter.heimdall.core.authorization.handler.AuthorizationFilterHandler;
import com.luter.heimdall.core.authorization.handler.DefaultAuthorizationFilterHandler;
import com.luter.heimdall.core.authorization.service.AuthorizationMetaDataService;
import com.luter.heimdall.core.cache.MapCache;
import com.luter.heimdall.core.cache.SimpleCache;
import com.luter.heimdall.core.cookie.CookieService;
import com.luter.heimdall.core.cookie.SessionCookieServiceImpl;
import com.luter.heimdall.core.manager.AuthenticationManager;
import com.luter.heimdall.core.manager.AuthorizationManager;
import com.luter.heimdall.core.manager.listener.AuthenticationEventListener;
import com.luter.heimdall.core.servlet.ServletHolder;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.session.dao.impl.CachedSessionDaoImpl;
import com.luter.heimdall.core.session.listener.SessionEventListener;
import com.luter.heimdall.core.session.scheduler.DefaultInvalidSessionClearScheduler;
import com.luter.heimdall.sample.common.encoder.BCryptPasswordEncoder;
import com.luter.heimdall.sample.common.encoder.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 基于内存缓存和常规url形式授权的配置
 *
 * @author Luter
 */
@Configuration
@Slf4j
public class CachedSecurityConfig {
    /**
     * 密码加密解密实现
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cookie服务
     *
     * @return the cookie service
     */
    @Bean
    public CookieService cookieService(ServletHolder servletHolder) {
        return new SessionCookieServiceImpl(servletHolder);
    }

    /**
     * Session缓存Dao
     *
     * @param cookieService the cookie service
     * @return the session dao
     */
    @Bean
    public SessionDAO sessionDAO(CookieService cookieService, ServletHolder servletHolder) {
        log.warn("初始化 SessionDAO");
        SimpleCache<String, SimpleSession> mapSimpleCache = new MapCache<>(Maps.newConcurrentMap());
        final CachedSessionDaoImpl cachedSessionDao = new CachedSessionDaoImpl(mapSimpleCache, servletHolder, cookieService);
        //Session事件监听
        List<SessionEventListener> listeners = new ArrayList<>();
        listeners.add(new SessionEventListener() {
            @Override
            public void afterCreated(SimpleSession session) {
                log.warn("Session 事件 : Session 成功创建:{}", session.getId());
            }

            @Override
            public void afterRead(SimpleSession session) {
                log.warn("Session 事件 : afterRead :{}", session.getId());
            }

            @Override
            public void afterUpdated(SimpleSession session) {
                log.warn("Session 事件 : afterUpdated :{}", session.getId());
            }

            @Override
            public void afterDeleted(SimpleSession session) {
                log.warn("Session 事件 : afterDeleted :{}", session.getId());
            }

            @Override
            public void afterSessionValidScheduled() {
                log.warn("Session 事件 : afterSessionValidScheduled");
            }
        });
        cachedSessionDao.setListeners(listeners);
        return cachedSessionDao;
    }

    /**
     * 认证管理器，实现用户登录注销等功能
     *
     * @param sessionDAO Session缓存Dao
     * @return the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(SessionDAO sessionDAO) {
        log.warn("初始化 认证管理器");
        final AuthenticationManager authenticationManager = new AuthenticationManager(sessionDAO);
        List<AuthenticationEventListener> listeners = new ArrayList<>();
        listeners.add(new AuthenticationEventListener() {
            @Override
            public void onLogin(int code, SimpleSession session) {
                log.warn("认证 事件: 用户:[{}] {}"
                        , session.getDetails().getPrincipal()
                        , 1 == code ? "重复登录" : 2 == code ? "登录" : "");

            }

            @Override
            public void onLogout(SimpleSession session) {
                log.warn("认证 事件:用户:[{}] 注销啦", session.getDetails().getPrincipal());
            }

        });
        authenticationManager.setListeners(listeners);
        return authenticationManager;
    }


    /**
     * 系统授权缓存Dao
     * <p>
     * 用户首次访问保护资源的时候，系统会自动从缓存加载需要授权规则，
     * <p>
     * 如果缓存中没有，则会调用AuthorizationMetaDataService加载。
     *
     * @return the authorization meta data cache dao
     */
    @Bean
    public AuthorizationMetaDataCacheDao authorizationMetaDataCacheDao() {
        log.warn("初始化 系统授权数据 MetaDataDao");
        Cache<String, Map<String, Collection<String>>> caffeineCache = Caffeine.newBuilder()
                .expireAfterAccess(Duration.ofHours(1))
                .recordStats()
                .build();
//        Caffeine缓存
        SimpleCache<String, Map<String, Collection<String>>> caffeineSimpleCache = new CaffeineCache<>(caffeineCache);
//        Map 缓存
        SimpleCache<String, Map<String, Collection<String>>> mapSimpleCache = new MapCache<>(Maps.newConcurrentMap());
        return new CachedAuthorizationMetaDataDao(caffeineSimpleCache);
    }

    /**
     * 授权管理器
     *
     * @param authenticationManager 认证管理器
     * @return the authorization manager
     */
    @Bean
    public AuthorizationManager authorizationManager(AuthenticationManager authenticationManager,
                                                     AuthorizationMetaDataService authorizationMetaDataService,
                                                     AuthorizationMetaDataCacheDao authorizationMetaDataCacheDao) {
        log.warn("初始化 授权管理器");
        return new AuthorizationManager(authorizationMetaDataService, authorizationMetaDataCacheDao, authenticationManager);
    }

    /**
     * 授权校验
     */
    @Bean
    public AuthorizationFilterHandler securityFilterHandler(AuthenticationManager authenticationManager
            , AuthorizationManager authorizationManager) {
        log.warn("初始化 授权过滤器");
        return new DefaultAuthorizationFilterHandler(authenticationManager, authorizationManager);
    }

    /**
     * 开启注解授权验证
     *
     * @return the security annotation aspect handler
     */
    @Bean
    public AuthorizationAnnotationAspect securityAspect(AuthorizationFilterHandler authorizationFilterHandler) {
        log.warn("初始化 注解授权");
        return new AuthorizationAnnotationAspect(authorizationFilterHandler);
    }

    /**
     * 开启过期session清理任务
     *
     * @param sessionDAO Session缓存Dao
     * @return the default invalid session clear scheduler
     */
    @Bean
    public DefaultInvalidSessionClearScheduler defaultInvalidSessionClearScheduler(SessionDAO sessionDAO) {
        log.warn("初始化 Session 自动清理任务");
        return new DefaultInvalidSessionClearScheduler(sessionDAO);
    }


}
