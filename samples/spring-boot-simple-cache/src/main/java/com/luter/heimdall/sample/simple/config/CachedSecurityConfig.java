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

package com.luter.heimdall.sample.simple.config;

import com.luter.heimdall.boot.starter.resolver.CurrentUserRequestArgumentResolver;
import com.luter.heimdall.cache.caffeine.CaffeineAuthorizationMetaDataDao;
import com.luter.heimdall.cache.caffeine.CaffeineLoginPasswordRetryLimitImpl;
import com.luter.heimdall.cache.caffeine.CaffeineSessionDaoImpl;
import com.luter.heimdall.core.authorization.aspect.AuthorizationAnnotationAspect;
import com.luter.heimdall.core.authorization.dao.AuthorizationMetaDataCacheDao;
import com.luter.heimdall.core.authorization.handler.AuthorizationFilterHandler;
import com.luter.heimdall.core.authorization.handler.DefaultAuthorizationFilterHandler;
import com.luter.heimdall.core.authorization.service.AuthorizationMetaDataService;
import com.luter.heimdall.core.cookie.CookieService;
import com.luter.heimdall.core.cookie.SessionCookieServiceImpl;
import com.luter.heimdall.core.manager.AuthenticationManager;
import com.luter.heimdall.core.manager.AuthorizationManager;
import com.luter.heimdall.core.manager.limiter.LoginPasswordRetryLimit;
import com.luter.heimdall.core.servlet.ServletHolder;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.session.scheduler.DefaultInvalidSessionClearScheduler;
import com.luter.heimdall.sample.common.encoder.BCryptPasswordEncoder;
import com.luter.heimdall.sample.common.encoder.PasswordEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cookie服务
     *
     * @param servletHolder the servlet holder
     * @return the cookie service
     */
    @Bean
    public CookieService cookieService(ServletHolder servletHolder) {
        return new SessionCookieServiceImpl(servletHolder);
    }

    /**
     * 当前登录用户参数注解解析
     *
     * @param authenticationManager the authentication manager
     * @return the current user request argument resolver
     */
    @Bean
    public CurrentUserRequestArgumentResolver currentUserRequestArgumentResolver(AuthenticationManager authenticationManager) {
        return new CurrentUserRequestArgumentResolver(authenticationManager);
    }

    /**
     * Session缓存Dao
     *
     * @param servletHolder the servlet holder
     * @return the session dao
     */
    @Bean
    public SessionDAO sessionDAO(ServletHolder servletHolder) {
        return new CaffeineSessionDaoImpl(servletHolder);
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
        return new AuthenticationManager(sessionDAO);
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
        return new CaffeineAuthorizationMetaDataDao();
    }

    /**
     * 授权管理器
     *
     * @param authenticationManager         认证管理器
     * @param authorizationMetaDataService  the authorization meta data service
     * @param authorizationMetaDataCacheDao the authorization meta data cache dao
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
     *
     * @param authenticationManager the authentication manager
     * @param authorizationManager  the authorization manager
     * @return the authorization filter handler
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
     * @param authorizationFilterHandler the authorization filter handler
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

    /**
     * 登录密码重试次数限制
     *
     * @return the login password retry limit
     */
    @Bean
    public LoginPasswordRetryLimit loginPasswordRetryLimit() {
        return new CaffeineLoginPasswordRetryLimitImpl();
    }

}
