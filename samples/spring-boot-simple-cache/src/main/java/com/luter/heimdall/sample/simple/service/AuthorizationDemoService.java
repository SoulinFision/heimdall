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

package com.luter.heimdall.sample.simple.service;

import com.luter.heimdall.core.authorization.handler.AuthorizationFilterHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 演示 Service 层权限判断
 *
 * @author Luter
 */
@Slf4j
@Service
public class AuthorizationDemoService {
    /**
     * 权限判断
     */
    @Autowired
    AuthorizationFilterHandler authorizationFilterHandler;

    /**
     * 是否登录
     *
     * @return the boolean
     */
    public boolean isAuthenticated() {
        return authorizationFilterHandler.isAuthenticated();
    }

    /**
     * 具备角色
     *
     * @param role the role
     * @return the boolean
     */
    public boolean hasRole(String role) {
        return authorizationFilterHandler.hasRole(role);
    }

    /**
     * 具备多个角色之一
     *
     * @param role the role
     * @return the boolean
     */
    public boolean hasAnyRoles(String... role) {
        return authorizationFilterHandler.hasAnyRoles(role);
    }

    /**
     * 具备多个角色所有
     *
     * @param role the role
     * @return the boolean
     */
    public boolean hasAllRoles(String... role) {
        return authorizationFilterHandler.hasAllRoles(role);
    }

    /**
     * 具备权限
     *
     * @param permission the permission
     * @return the boolean
     */
    public boolean hasPermission(String permission) {
        return authorizationFilterHandler.hasPermission(permission);
    }

    /**
     * 具备多个权限之一
     *
     * @param permissions the permissions
     * @return the boolean
     */
    public boolean hasAnyPermissions(String... permissions) {
        return authorizationFilterHandler.hasAnyPermissions(permissions);
    }

    /**
     * 具备多个权限所有
     *
     * @param permissions the permissions
     * @return the boolean
     */
    public boolean hasAllPermissions(String... permissions) {
        return authorizationFilterHandler.hasAllPermissions(permissions);
    }

}
