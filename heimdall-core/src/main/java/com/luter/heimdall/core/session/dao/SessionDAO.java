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

package com.luter.heimdall.core.session.dao;

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.cookie.CookieService;
import com.luter.heimdall.core.details.UserDetails;
import com.luter.heimdall.core.exception.InvalidSessionException;
import com.luter.heimdall.core.servlet.ServletHolder;
import com.luter.heimdall.core.session.Page;
import com.luter.heimdall.core.session.SimpleSession;

import java.util.Collection;
import java.util.List;

/**
 * The interface Session dao.
 *
 * @author luter
 */
public interface SessionDAO {
    /**
     * 默认全局Session过期时间 3600秒
     */
    long GLOBAL_SESSION_TIMEOUT = 3600;
    /**
     * 默认 Session 缓存前缀
     */
    String DEFAULT_SESSION_ID_PREFIX = "heimdall:sessions:";
    /**
     * 默认活动 Session 缓存 Key (for redis)
     */
    String DEFAULT_ACTIVE_SESSION_CACHE_KEY = "heimdall:active-sessions:";
    /**
     * 默认活动 用户 缓存 Key (for redis)
     */
    String DEFAULT_ACTIVE_USER_CACHE_KEY = "heimdall:active-users:";

    /**
     * 创建session，也可以理解为登录认证
     * <p>
     * 会自动对Principal是否登录进行判断
     *
     * @param userDetails the user details
     * @return the simple session
     */
    SimpleSession create(UserDetails userDetails);


    /**
     * 通过principal获取Session
     *
     * @param principal the principal
     * @return the by principal
     */
    SimpleSession getByPrincipal(String principal);

    /**
     * 读取Session
     *
     * @param sessionId the session id
     * @return the simple session
     * @throws InvalidSessionException the invalid session exception
     */
    SimpleSession readSession(String sessionId) throws InvalidSessionException;

    /**
     * 更新Session信息
     * <p>
     * 当发生系统访问行为后，应该更新Session的lastAccessTime时间为当前时间
     * <p>
     * 当用户权限发生变化后，可通过此方法动态更新Session内UserDetails内容
     * <p>
     * session续签功能也在此方法完成
     *
     * @param session the session
     * @return the simple session
     * @throws InvalidSessionException the invalid session exception
     */
    SimpleSession update(SimpleSession session) throws InvalidSessionException;

    /**
     * 删除Session
     *
     * @param session the session
     */
    void delete(SimpleSession session);

    /**
     * 获取所有活动Session,也即是在线用户
     *
     * @return the active sessions
     */
    Collection<SimpleSession> getActiveSessions();

    /**
     * 分页获取所有活动Session,也即是在线用户
     * <p>
     * 注意:仅Redis缓存可用
     * <p>
     * 由于ZSet清理的不及时或者有错误,导致已经失效的Session没有及时清理
     * <p>
     * 这样在mGet的时候，就会导致返回的列表list中可能存在空数据null
     * <p>
     * 调用的时候，需要对空值进行判断处理，免得前端处理出现错误
     *
     * @param pageNo   页码
     * @param pageSize 每页数量
     * @return the active sessions
     */
    default Page<SimpleSession> getActiveSessions(int pageNo, int pageSize) {
        System.out.println("======default getActiveSessions with page and size");
        return null;
    }

    /**
     * 清理无效Session缓存
     */
    default void validateExpiredSessions() {
        System.out.println("======validateExpiredSessions default");
    }

    /////用户权限信息缓存

    /**
     * 用户登录成功后，缓存用户具有的权限
     */
    default void setUserAuthorities(String sessionId,List<? extends GrantedAuthority> authorities) {
        System.out.println("======setAuthorities default");
    }

    /**
     * 从缓存中读取当前登录用户的权限
     */
    default List<? extends GrantedAuthority> getUserAuthorities(String sessionId) {
        System.out.println("======setAuthorities default");
        return null;
    }

    default void clearUserAuthorities(String sessionId) {
        System.out.println("======clearUserAuthorities default");
    }

    default void clearAllUserAuthorities() {
        System.out.println("======clearAllUserAuthorities default");
    }

    /**
     * Gets servlet holder.
     *
     * @return the servlet holder
     */
    ServletHolder getServletHolder();

    /**
     * Gets cookie service.
     *
     * @return the cookie service
     */
    CookieService getCookieService();
}
