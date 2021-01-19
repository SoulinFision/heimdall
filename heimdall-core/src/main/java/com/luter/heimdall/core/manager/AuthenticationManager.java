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

package com.luter.heimdall.core.manager;

import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.details.UserDetails;
import com.luter.heimdall.core.exception.*;
import com.luter.heimdall.core.manager.listener.AbstractAuthenticationEvent;
import com.luter.heimdall.core.session.Page;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证管理器
 *
 * @author Luter
 */
@Slf4j
public class AuthenticationManager extends AbstractAuthenticationEvent {

    /**
     * The Session dao.
     */
    private final SessionDAO sessionDAO;

    /**
     * 是否允许重复登录
     * <p>
     * <p>
     * false: 不允许，踢掉前面的
     * <p>
     * true: 允许，拒绝后来的
     */
    private boolean concurrentLogin;

    /**
     * 认证管理器
     * <p>
     * 实现用户登录、注销、在线用户查看、踢出等认证相关操作
     *
     * @param sessionDAO SessionDAO 实现
     */
    public AuthenticationManager(SessionDAO sessionDAO) {
        this.sessionDAO = sessionDAO;
        concurrentLogin = ConfigManager.getConfig().getSession().isConcurrentLogin();
    }

    /**
     * 登录
     *
     * @param userDetails the user details
     * @return the simple session
     */
    public SimpleSession login(UserDetails userDetails) {
        log.debug("系统配置\n{}", ConfigManager.getConfig());
        if (null == userDetails) {
            throw new AccountException("UserDetails must not be null,login fail ");
        }
        if (StrUtils.isBlank(userDetails.getPrincipal())) {
            throw new AccountException("principal 为空，请检查UserDetails实现是否正确?");
        }
        //看看这个principal是不是登录了
        final SimpleSession session = sessionDAO.getByPrincipal(userDetails.getPrincipal());
        //登录了,并且没有过期
        if (null != session && !session.isExpired()) {
            //把新来的拒了
            if (concurrentLogin) {
                onLogin(0, session);
                throw new AccountException("您已经在别处登录,登录拒绝.请等待上次登录状态失效后再试");
            } else {
                log.warn("Principal:{},在别处登录，Session:{} 被自动踢出.", session.getDetails().getPrincipal(), session.getId());
                //把前面的踢了
                onLogin(1, session);
                sessionDAO.delete(session);
            }
            //没过期，直接把SessionId返回
//            if (!session.isTimedOut()) {
//                log.info(" Principal :[{}] 已经登录,SessionId:[{}],续签返回.", userDetails.getPrincipal(), session.getId());
//                //此处默认进行Session更新，也可以踢出上一个登录，然后重新登录
//                final SimpleSession updateSession = sessionDAO.update(session);
//                //发布事件
//                onLogin(1, updateSession);
//                return updateSession;
//            } else {
//                //过期了，删除过期Session，继续登录流程
//                log.info(" Principal :[{}] with SessionId:[{}],TimedOut. delete!", userDetails.getPrincipal(), session.getId());
//                sessionDAO.delete(session);
//            }
        }
        log.debug("开始进行登录，userDetails:{}", userDetails);
        final SimpleSession simpleSession = sessionDAO.create(userDetails);
        //发布事件
        onLogin(2, simpleSession);
        return simpleSession;
    }

    /**
     * 通过 principal 获取用户Session
     *
     * @param principal the principal对应 UserDetails.getPrincipal()
     * @return SimpleSession user by principal
     */
    public SimpleSession getUserByPrincipal(String principal) {
        if (StrUtils.isBlank(principal)) {
            throw new HeimdallException("principal参数不能为空");
        }

        final SimpleSession session = sessionDAO.getByPrincipal(principal);
        if (null != session) {
            if (session.isExpired()) {
                throw new ExpiredSessionException();
            }
            log.debug("这个Session已经登录了:{}", session.getId());
            //只要是登录用户,就更新续签
            return sessionDAO.update(session);
        }
        throw new UnAuthticatedException();
    }

    /**
     * 注销认证状态
     *
     * @return the simple session
     */
    public SimpleSession logout() {
        final SimpleSession session = getCurrentUser();
        if (null != session) {
            sessionDAO.delete(session);
            //发布事件
            onLogout(session);
            return session;
        } else {
            throw new UnAuthticatedException("请登录后操作");
        }

    }

    /**
     * 获取用户，如果没登录或者超期了，抛出异常
     *
     * @return the current user
     */
    public SimpleSession getCurrentUser() {
        return getCurrentUser(true);

    }

    /**
     * 获取当前用户
     *
     * @param isThrewEx 如果session过期了是否抛出异常
     * @return the current user
     */
    public SimpleSession getCurrentUser(boolean isThrewEx) {
        String sessionId = resolveSessionId(sessionDAO.getServletHolder().getRequest());
        if (StrUtils.isBlank(sessionId)) {
            if (isThrewEx) {
                throw new UnAuthticatedException();
            }
            return null;
        }
        final SimpleSession session = sessionDAO.readSession(sessionId);
        if (null != session) {
            //Session没过期
            if (!session.isExpired()) {
                log.debug("User with Principal :[{}] and SesionId :[{}] has logged in",
                        session.getDetails().getPrincipal(), session.getId());
                //只要是登录用户,就更新续签
                return sessionDAO.update(session);
            } else {
                //过期了，而且需要抛出异常
                if (isThrewEx) {
                    throw new ExpiredSessionException();
                }
                //否则返回Null
                return null;
            }

        }
        //session为空，而且需要抛出异常
        if (isThrewEx) {
            throw new UnAuthticatedException();
        }
        return null;
    }

    /**
     * 解析请求中的SessionId
     * <p>
     * <p>
     * 1、如果Cookie开启，首先从Cookie解析，有，则返回
     * <p>
     * 2、如果没有，从Header中解析，有则返回
     * <p>
     * 3、header中没有，从request parameters参数中解析，返回
     *
     * @param request the request
     * @return the string
     */
    public String resolveSessionId(HttpServletRequest request) {
        final String sessionName = ConfigManager.getConfig().getSession().getSessionName();
        if (ConfigManager.getConfig().getCookie().getEnabled()) {
            final Cookie cookie = sessionDAO.getCookieService().getCookie();
            if (null != cookie) {
                log.debug("解析请求 SessionId | Cookie 中携带了SessionId: [{} = {}]", sessionName, cookie.getValue());
                return cookie.getValue();
            }
        }
        String token = request.getHeader(sessionName);
        if (StrUtils.isBlank(token)) {
            String sessionId = request.getParameter(sessionName);
            log.debug("解析请求 SessionId | Parameters 中携带了SessionId: [{} = {}]", sessionName, sessionId);
            return sessionId;
        }
        log.debug("解析请求 SessionId | Headers 中携带了SessionId: [{} = {}]", sessionName, token);
        return token;
    }

    /**
     * 获取所有在线用户
     *
     * @return the active sessions
     */
    public Collection<SimpleSession> getActiveSessions() {
        return sessionDAO.getActiveSessions();
    }

    /**
     * 分页获取在线用户(仅redis缓存支持)
     *
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return the active sessions
     */
    public Page<SimpleSession> getActiveSessions(int pageNo, int pageSize) {
        final Page<SimpleSession> activeSessions = sessionDAO.getActiveSessions(pageNo, pageSize);
        //因为ZSet和实际Session之间可能存在数据偏差，导致通过批量拿到的数据有null
        //此处处理一下，将null转换成空SimpleSession对象，便于调用方处理
        //当然也可直接返给调用方，调用方遍历判断是否为Null，进行处理
        if (null != activeSessions && null != activeSessions.getRecords() && !activeSessions.getRecords().isEmpty()) {
            final List<SimpleSession> collect = activeSessions.getRecords().stream().map(d -> {
                if (null == d) {
                    return new SimpleSession();
                }
                return d;
            }).collect(Collectors.toList());
            activeSessions.setRecords(collect);
        }
        return activeSessions;
    }

    /**
     * 通过SessionId踢出用户
     *
     * @param sessionId the session id
     * @return the boolean
     */
    public boolean kickOutSession(String sessionId) {
        if (StrUtils.isNotBlank(sessionId)) {
            final SimpleSession simpleSession = sessionDAO.readSession(sessionId);
            if (null != simpleSession) {
                sessionDAO.delete(simpleSession);
                //发布事件
                onSessionKickOut(simpleSession);
                return true;
            } else {
                throw new InvalidSessionException("ID 错误，用户未登录，踢出失败");
            }

        } else {
            throw new HeimdallException("SessionId 不能为空");
        }
    }

    /**
     * 通过principal踢出用户
     *
     * @param principal the principal，对应于UserDetails.getPrincipal()
     * @return the boolean
     */
    public boolean kickOutPrincipal(String principal) {
        final SimpleSession simpleSession = getUserByPrincipal(principal);
        sessionDAO.delete(simpleSession);
        //发布事件
        onPrincipalKickOut(principal, simpleSession);
        return true;
    }


    /**
     * Gets session dao.
     *
     * @return the session dao
     */
    public SessionDAO getSessionDAO() {
        return sessionDAO;
    }

    public boolean isConcurrentLogin() {
        return concurrentLogin;
    }

    public void setConcurrentLogin(boolean concurrentLogin) {
        this.concurrentLogin = concurrentLogin;
    }
}
