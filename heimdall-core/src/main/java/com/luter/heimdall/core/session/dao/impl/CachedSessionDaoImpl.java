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

package com.luter.heimdall.core.session.dao.impl;

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.cache.SimpleCache;
import com.luter.heimdall.core.config.Config;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.cookie.CookieService;
import com.luter.heimdall.core.details.UserDetails;
import com.luter.heimdall.core.exception.*;
import com.luter.heimdall.core.servlet.ServletHolder;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.session.generator.SessionIdGenerator;
import com.luter.heimdall.core.session.generator.UUIDSessionIdGeneratorImpl;
import com.luter.heimdall.core.session.listener.AbstractSessionEvent;
import com.luter.heimdall.core.utils.StrUtils;
import com.luter.heimdall.core.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 基于内存缓存(如：map、ehcache 、caffeine)的SessionDao
 *
 * @author Luter
 */
@Slf4j
public class CachedSessionDaoImpl extends AbstractSessionEvent implements SessionDAO {

    /**
     * Session 缓存
     */
    private final SimpleCache<String, SimpleSession> sessionCache;
    /**
     * 用户权限缓存
     */
    private final SimpleCache<String, List<? extends GrantedAuthority>> userAuthCache;

    /**
     * The Session id generator.
     */
    private SessionIdGenerator sessionIdGenerator;
    /**
     * 获取请求，主要是为了拿远端IP
     */
    private ServletHolder servletHolder;
    /**
     * Cookie管理器
     */
    private CookieService cookieService;


    /**
     * Instantiates a new Cached session dao.
     *
     * @param sessionCache  the session cache
     * @param userAuthCache the user auth cache
     * @param servletHolder the servlet holder
     * @param cookieService the cookie service
     */
    public CachedSessionDaoImpl(SimpleCache<String, SimpleSession> sessionCache,
                                SimpleCache<String, List<? extends GrantedAuthority>> userAuthCache,
                                ServletHolder servletHolder, CookieService cookieService) {
        final Config config = ConfigManager.getConfig();
        if (null == sessionCache) {
            throw new CacheException("sessionCache 实现不能为空");
        }
        if (null == userAuthCache) {
            throw new CacheException("userAuthCache 实现不能为空");
        }
        if (null != servletHolder && config.getCookie().getEnabled()) {
            if (null == cookieService) {
                throw new HeimdallException("请实现或者Set ServletHolder、cookieService实现类,或者关闭Cookie功能");
            }
        } else {
            log.warn("ServletHolder 未实现,Cookie功能关闭");
        }
        this.sessionCache = sessionCache;
        this.userAuthCache = userAuthCache;
        this.servletHolder = servletHolder;
        this.cookieService = cookieService;
        this.sessionIdGenerator = null == sessionIdGenerator ? new UUIDSessionIdGeneratorImpl() : sessionIdGenerator;
    }


    @Override
    public SimpleSession create(UserDetails userDetails) {
        final Config config = ConfigManager.getConfig();
        if (null == sessionCache) {
            throw new CacheException("cache must not be null");
        }
        if (null == sessionIdGenerator) {
            throw new HeimdallException("sessionIdGenerator  must not be null");
        }
        SimpleSession session = new SimpleSession();
        if (userDetails.enabled()) {
            final String sessionId = sessionIdGenerator.generate();
            if (StrUtils.isBlank(sessionId)) {
                throw new SessionException("generated sessionId must not be null ");
            }
            session.setId(sessionId);
            session.setDetails(userDetails);
            //在认证服务判断，此处注释
//            final SimpleSession userByUniqueId = getByPrincipal(session.getDetails().getPrincipal());
//            if (null != userByUniqueId) {
//                log.debug("已经登录过了:{}", session.getDetails().getPrincipal());
//                update(userByUniqueId);
//                return userByUniqueId;
//            }
            //拿远端IP
            if (null != servletHolder) {
                session.setHost(WebUtils.getRemoteIp(servletHolder.getRequest()));
            }
            sessionCache.put(getSessionIdPrefix() + sessionId, session);
            //写入cookie
            if (config.getCookie().getEnabled()) {
                if (null != cookieService) {
                    cookieService.addCookie(sessionId);
                } else {
                    throw new CookieException("Cookie Provider must not be null");
                }
            } else {
                log.debug("Cookie功能未开启");
            }
            //发布事件
            afterCreated(session);
            return session;

        }
        throw new NonEnabledAccountException();
    }


    @Override
    public SimpleSession readSession(String sessionId) throws InvalidSessionException {
        final SimpleSession session = sessionCache.get(getSessionIdPrefix() + sessionId);
        afterRead(session);
        return session;
    }

    @Override
    public SimpleSession update(SimpleSession session) throws InvalidSessionException {
        log.debug("更新Session 信息,session:{}", session);
        session.setLastAccessTime(new Date());
        //发布事件
        afterUpdated(session);
        return session;
    }

    @Override
    public void delete(SimpleSession session) {
        final Config config = ConfigManager.getConfig();
        //清理用户权限缓存
        clearUserAuthorities(session.getId());
        //清理 Session 缓存
        sessionCache.remove(getSessionIdPrefix() + session.getId());
        log.debug("remove session from cache ,key ;{}", getSessionIdPrefix() + session.getId());
        //删除cookie
        if (config.getCookie().getEnabled()) {
            if (null != cookieService) {
                cookieService.delCookie();
            } else {
                throw new CookieException("Cookie Provider must not be null");
            }
        } else {
            log.debug("Cookie功能未开启");
        }

        //删除用户权限缓存
        clearUserAuthorities(session.getId());
        //发布事件
        afterDeleted(session);
    }

    @Override
    public Collection<SimpleSession> getActiveSessions() {
        return sessionCache.values();
    }

    @Override
    public SimpleSession getByPrincipal(String principal) {
        final Collection<SimpleSession> activeSessions = getActiveSessions();
        for (SimpleSession activeSession : activeSessions) {
            if (principal.equals(activeSession.getDetails().getPrincipal())) {
                return activeSession;
            }
        }
        return null;
    }

    @Override
    public void validateExpiredSessions() {
        final Collection<SimpleSession> activeSessions = getActiveSessions();
        log.info("过期Session清理任务开始,活动Session总数:{} ", activeSessions.size());
        if (activeSessions.isEmpty()) {
            log.info("暂无活动Session");
        } else {
            for (SimpleSession activeSession : activeSessions) {
                if (activeSession.isTimedOut()) {
                    //过期了，直接删除用户权限缓存
                    clearUserAuthorities(activeSession.getId());
                    //直接删除Session 缓存
//                    delete(activeSession);
                    //Session 缓存留着等定时任务删除，这种情况，可以获取到 Session 过期的错误
                    //对前端相对友好一些
                    activeSession.setExpired(true);
                    log.warn(" SessionId:[{}] 过期, 被移除 ", activeSession.getId());
                } else {
                    log.info("SessionId: [{}] 未过期不作处理 ", activeSession.getId());
                }
            }
        }
        //发布事件
        afterSessionValidScheduled();
        log.info("过期Session清理任务结束");
    }

    //////用户权限部分

    @Override
    public void setUserAuthorities(String sessionId, List<? extends GrantedAuthority> authorities) {
        if (null != authorities && !authorities.isEmpty()) {
            log.debug("缓存 用户权限，SessionId : [{}], 权限总数:{}", sessionId, authorities.size());
            userAuthCache.put(sessionId, authorities);
        } else {
            log.warn("缓存用户权限失败，用户权限为空");
        }
    }

    @Override
    public List<? extends GrantedAuthority> getUserAuthorities(String sessionId) {
        log.debug(" 获取缓存 用户权限,SessionId :[{}]", sessionId);
        return userAuthCache.get(sessionId);
    }

    @Override
    public void clearUserAuthorities(String sessionId) {
        log.debug("清除缓存 用户权限，SessionId : [{}]", sessionId);
        userAuthCache.remove(sessionId);
    }

    @Override
    public void clearAllUserAuthorities() {
        log.debug("清除所有缓存的用户权限");
        userAuthCache.clear();
    }

    //////用户权限部分
    @Override
    public ServletHolder getServletHolder() {
        return servletHolder;
    }

    @Override
    public CookieService getCookieService() {
        return cookieService;
    }

    /**
     * Gets cache.
     *
     * @return the cache
     */
    public SimpleCache<String, SimpleSession> getSessionCache() {
        return sessionCache;
    }

    /**
     * Gets session id generator.
     *
     * @return the session id generator
     */
    public SessionIdGenerator getSessionIdGenerator() {
        return sessionIdGenerator;
    }

    /**
     * Sets session id generator.
     *
     * @param sessionIdGenerator the session id generator
     */
    public void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
    }


    /**
     * Sets servlet holder.
     *
     * @param servletHolder the servlet holder
     */
    public void setServletHolder(ServletHolder servletHolder) {
        this.servletHolder = servletHolder;
    }


    /**
     * Sets cookie provider.
     *
     * @param cookieService the cookie provider
     */
    public void setCookieService(CookieService cookieService) {
        this.cookieService = cookieService;
    }

    /**
     * 获取 SessionID 前缀
     *
     * @return the session id prefix
     */
    public String getSessionIdPrefix() {
        String sessionIdPrefix = ConfigManager.getConfig().getSession().getSessionIdPrefix();
        return StrUtils.isBlank(sessionIdPrefix) ? DEFAULT_SESSION_ID_PREFIX : sessionIdPrefix;
    }

    public SimpleCache<String, List<? extends GrantedAuthority>> getUserAuthCache() {
        return userAuthCache;
    }
}
