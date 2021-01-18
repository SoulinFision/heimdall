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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 认证监听器 抽象类，实现事件发布
 *
 * @author Luter
 */
@Slf4j
public abstract class AbstractAuthenticationEvent {

    /**
     * The Listeners.
     */
    private Collection<AuthenticationEventListener> listeners = new ArrayList<>();

    /**
     * 认证登录成功
     *
     * @param code    状态码
     *                <p>
     *                <p>
     *                0 : 重复登录，新来的被拒绝登录
     *                <p>
     *                1： 重复登录，上一个登录 Session 被踢出
     *                <p>
     *                2：登录成功，创建了新 Session
     * @param session the session
     */
    public void onLogin(int code, SimpleSession session) {
        for (AuthenticationEventListener listener : this.listeners) {
            try {
                listener.onLogin(code, session);
            } catch (Exception e) {
                log.error(" Authentication 事件监听 onLogin 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 注销认证成功
     *
     * @param session the session
     */
    public void onLogout(SimpleSession session) {
        for (AuthenticationEventListener listener : this.listeners) {
            try {
                listener.onLogout(session);
            } catch (Exception e) {
                log.error(" Authentication 事件监听 onLogout 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * Session 被成功踢出
     *
     * @param session the session
     */
    public void onSessionKickOut(SimpleSession session) {
        for (AuthenticationEventListener listener : this.listeners) {
            try {
                listener.onSessionKickOut(session);
            } catch (Exception e) {
                log.error(" Authentication 事件监听 onSessionKickOut 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * principal 被成功踢出
     *
     * @param principal the principal
     * @param session   the session
     */
    public void onPrincipalKickOut(String principal, SimpleSession session) {
        for (AuthenticationEventListener listener : this.listeners) {
            try {
                listener.onPrincipalKickOut(principal, session);
            } catch (Exception e) {
                log.error(" Authentication 事件监听 onPrincipalKickOut 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * Gets listeners.
     *
     * @return the listeners
     */
    public Collection<AuthenticationEventListener> getListeners() {
        return listeners;
    }

    /**
     * Sets listeners.
     *
     * @param listeners the listeners
     */
    public void setListeners(Collection<AuthenticationEventListener> listeners) {
        this.listeners = listeners;
    }
}
