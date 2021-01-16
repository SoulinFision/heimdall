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

package com.luter.heimdall.core.authorization.handler;

import com.luter.heimdall.core.manager.AuthenticationManager;
import com.luter.heimdall.core.manager.AuthorizationManager;
import com.luter.heimdall.core.session.SimpleSession;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * The type Abstract security filter handler.
 *
 * @author Luter
 */
@Slf4j
public abstract class AbstractAuthorizationFilterHandler {
    /**
     * The Authentication manager.
     */
    private final AuthenticationManager authenticationManager;
    /**
     * The Authorization manager.
     */
    private final AuthorizationManager authorizationManager;


    /**
     * Instantiates a new Abstract security filter handler.
     *
     * @param authenticationManager the authentication manager
     * @param authorizationManager  the authorization manager
     */
    public AbstractAuthorizationFilterHandler(AuthenticationManager authenticationManager, AuthorizationManager authorizationManager) {
        this.authenticationManager = authenticationManager;
        this.authorizationManager = authorizationManager;
    }

    /**
     * 获取当前登录用户
     *
     * @return the current user
     */
    public SimpleSession getCurrentUser() {
        return authenticationManager.getCurrentUser(false);
    }


    /**
     * needIdentifiers 是否包含单个  hasIdentifier
     *
     * @param needIdentifiers 需要的标识
     * @param hasIdentifier   具有的单个标识
     * @return the boolean
     */
    public boolean hasIdentifier(List<String> needIdentifiers, String hasIdentifier) {
        return null != needIdentifiers && !needIdentifiers.isEmpty() && needIdentifiers.contains(hasIdentifier);
    }

    /**
     * 比较 needIdentifiers 是否全部包含  hasIdentifiers
     *
     * @param needIdentifiers 需要的标识
     * @param hasIdentifiers  具有的多个标识
     * @return the boolean
     */
    public boolean hasAllIdentifiers(List<String> needIdentifiers, String[] hasIdentifiers) {
        if (null != hasIdentifiers && hasIdentifiers.length > 0 && null != needIdentifiers && !needIdentifiers.isEmpty()) {
            return needIdentifiers.containsAll(Arrays.asList(hasIdentifiers));
        }
        return false;
    }

    /**
     * 比较 needIdentifiers 是否全部包含  hasIdentifiers 其中之一
     *
     * @param needIdentifiers 需要的标识
     * @param hasIdentifiers  具有的多个标识
     * @return the boolean
     */
    public boolean hasAnyIdentifiers(List<String> needIdentifiers, String[] hasIdentifiers) {
        if (null != hasIdentifiers && hasIdentifiers.length > 0 && null != needIdentifiers && !needIdentifiers.isEmpty()) {
            for (String i : needIdentifiers) {
                if (Arrays.asList(hasIdentifiers).contains(i)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets authentication manager.
     *
     * @return the authentication manager
     */
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }

    public AuthorizationManager getAuthorizationManager() {
        return authorizationManager;
    }
}
