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

/**
 * 权限验证过滤器处理器接口
 *
 * @author luter
 */
public interface AuthorizationFilterHandler {
    /**
     * 用户是否登录
     *
     * @return the boolean 登录或者未登录，不会抛出未登录异常
     */
    boolean isAuthenticated();

    /**
     * 用户是否具有某个角色
     *
     * @param role the role
     * @return the boolean
     */
    boolean hasRole(String role);

    /**
     * 用户是否具有多个角色之一
     *
     * @param roles the roles
     * @return the boolean
     */
    boolean hasAnyRoles(String... roles);

    /**
     * 用户是否具有多个角色全部
     *
     * @param roles the roles
     * @return the boolean
     */
    boolean hasAllRoles(String... roles);


    /**
     * 用户是否具有某个权限标识
     *
     * @param perm the perm
     * @return the boolean
     */
    boolean hasPermission(String perm);

    /**
     * 用户是否具有多个权限标识之一
     *
     * @param perms the perms
     * @return the boolean
     */
    boolean hasAnyPermissions(String... perms);

    /**
     * 用户是否具有多个权限标识全部
     *
     * @param perms the perms
     * @return the boolean
     */
    boolean hasAllPermissions(String... perms);
}
