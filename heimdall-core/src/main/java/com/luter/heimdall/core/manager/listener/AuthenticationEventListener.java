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

package com.luter.heimdall.core.manager.listener;

import com.luter.heimdall.core.session.SimpleSession;

/**
 * 认证事件监听
 * <p>
 * 可以实现此接口,监听 登录、注销、踢出等事件,记录日志.
 *
 * @author Luter
 */
public interface AuthenticationEventListener {
    /**
     * 认证登录成功
     *
     * @param code    0: 重复登录，新来的被拒绝登录
     *                1： 重复登录，上一个登录 Session 被踢出
     *                2：登录成功，创建了新 Session
     * @param session the session
     */
    default void onLogin(int code, SimpleSession session) {
    }

    /**
     * 注销认证成功
     *
     * @param session the session
     */
    default void onLogout(SimpleSession session) {
    }

    /**
     * Session 被成功踢出
     *
     * @param session the session
     */
    default void onSessionKickOut(SimpleSession session) {
    }

    /**
     * principal 被成功踢出
     *
     * @param principal the principal
     * @param session   the session
     */
    default void onPrincipalKickOut(String principal, SimpleSession session) {
    }
}
