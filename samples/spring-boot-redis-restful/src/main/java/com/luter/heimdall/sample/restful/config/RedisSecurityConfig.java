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

package com.luter.heimdall.sample.restful.config;


import com.luter.heimdall.cache.redis.authorization.RedisAuthorizationMetaDataDao;
import com.luter.heimdall.cache.redis.session.RedisSessionDaoImpl;
import com.luter.heimdall.core.authorization.aspect.AuthorizationAnnotationAspect;
import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.authorization.dao.AuthorizationMetaDataCacheDao;
import com.luter.heimdall.core.authorization.handler.AuthorizationFilterHandler;
import com.luter.heimdall.core.authorization.handler.DefaultAuthorizationFilterHandler;
import com.luter.heimdall.core.authorization.service.AuthorizationMetaDataService;
import com.luter.heimdall.core.cookie.CookieService;
import com.luter.heimdall.core.cookie.SessionCookieServiceImpl;
import com.luter.heimdall.core.manager.AuthenticationManager;
import com.luter.heimdall.core.manager.AuthorizationManager;
import com.luter.heimdall.core.servlet.ServletHolder;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.session.scheduler.DefaultInvalidSessionClearScheduler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.List;

/**
 * 系统安全模块配置
 *
 * @author Luter
 */
@Configuration
@Slf4j
public class RedisSecurityConfig {

    /**
     * Session 缓存
     */
    @Autowired
    private RedisTemplate<String, SimpleSession> sessionRedisTemplate;

    /**
     * 在线用户关系缓存
     */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    /**
     * 系统权限缓存
     */
    @Autowired
    private RedisTemplate<String, Collection<String>> stringCollectionRedisTemplate;
    /**
     * Session(用户) 权限缓存
     */
    @Autowired
    private RedisTemplate<String, List<? extends GrantedAuthority>> userAuthRedisTemplate;

    /**
     * Cookie service cookie service.
     *
     * @param servletHolder the servlet holder
     * @return the cookie service
     */
    @Bean
    public CookieService cookieService(ServletHolder servletHolder) {
        log.warn("初始化 Cookie服务");
        return new SessionCookieServiceImpl(servletHolder);
    }

    /**
     * Session dao session dao.
     *
     * @param cookieService the cookie service
     * @param servletHolder the servlet holder
     * @return the session dao
     */
    @Bean
    public SessionDAO sessionDAO(CookieService cookieService, ServletHolder servletHolder) {
        log.warn("初始化 SessionDAO");
        return new RedisSessionDaoImpl(sessionRedisTemplate, stringRedisTemplate, userAuthRedisTemplate, servletHolder, cookieService);
    }

    /**
     * Authentication manager authentication manager.
     *
     * @param sessionDAO the session dao
     * @return the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(SessionDAO sessionDAO) {
        log.warn("初始化 认证管理器");
        return new AuthenticationManager(sessionDAO);
    }

    /**
     * Authorization meta data dao authorization meta data cache dao.
     *
     * @return the authorization meta data cache dao
     */
    @Bean
    public AuthorizationMetaDataCacheDao authorizationMetaDataCacheDao() {
        log.warn("初始化 系统权限数据 MetaDataDao");
        return new RedisAuthorizationMetaDataDao(stringCollectionRedisTemplate);
    }


    /**
     * Authorization manager authorization manager.
     *
     * @param authenticationManager         the authentication manager
     * @param authorizationMetaDataService  the authorization meta data service
     * @param authorizationMetaDataCacheDao the authorization meta data cache dao
     * @return the authorization manager
     */
    @Bean
    public AuthorizationManager authorizationManager(AuthenticationManager authenticationManager
            , AuthorizationMetaDataService authorizationMetaDataService
            , AuthorizationMetaDataCacheDao authorizationMetaDataCacheDao) {
        log.warn("初始化 授权管理器");
        return new AuthorizationManager(authorizationMetaDataService, authorizationMetaDataCacheDao, authenticationManager);
    }

    /**
     * Authorization filter handler authorization filter handler.
     *
     * @param authenticationManager the authentication manager
     * @param authorizationManager  the authorization manager
     * @return the authorization filter handler
     */
    @Bean
    public AuthorizationFilterHandler authorizationFilterHandler(AuthenticationManager authenticationManager,
                                                                 AuthorizationManager authorizationManager) {
        log.warn("初始化 授权过滤器");
        return new DefaultAuthorizationFilterHandler(authenticationManager, authorizationManager);
    }

    /**
     * Security aspect security annotation aspect handler.
     *
     * @param authorizationFilterHandler the authorization filter handler
     * @return the security annotation aspect handler
     */
    @Bean
    public AuthorizationAnnotationAspect securityAspect(AuthorizationFilterHandler authorizationFilterHandler) {
        log.warn("初始化 注解权限");
        return new AuthorizationAnnotationAspect(authorizationFilterHandler);
    }

    /**
     * Default invalid session clear scheduler default invalid session clear scheduler.
     *
     * @param sessionDAO the session dao
     * @return the default invalid session clear scheduler
     */
    @Bean
    public DefaultInvalidSessionClearScheduler defaultInvalidSessionClearScheduler(SessionDAO sessionDAO) {
        log.warn("初始化 Session 自动清理任务");
        return new DefaultInvalidSessionClearScheduler(sessionDAO);
    }
}
