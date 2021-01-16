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

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.exception.UnAuthticatedException;
import com.luter.heimdall.core.manager.AuthenticationManager;
import com.luter.heimdall.core.manager.AuthorizationManager;
import com.luter.heimdall.core.session.SimpleSession;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 默认注解权限过滤器
 *
 * @author Luter
 */
@Slf4j
public class DefaultAuthorizationFilterHandler extends AbstractAuthorizationFilterHandler implements AuthorizationFilterHandler {


    /**
     * Instantiates a new Abstract security filter handler.
     *
     * @param authenticationManager the authentication manager
     * @param authorizationManager  the authorization manager
     */
    public DefaultAuthorizationFilterHandler(AuthenticationManager authenticationManager,
                                             AuthorizationManager authorizationManager) {
        super(authenticationManager, authorizationManager);
    }

    @Override
    public boolean isAuthenticated() {
        return null != getCurrentUser();
    }

    @Override
    public boolean hasRole(String role) {
        return hasPermission(role);
    }

    @Override
    public boolean hasAnyRoles(String... roles) {
        return hasAnyPermissions(roles);
    }

    @Override
    public boolean hasAllRoles(String... roles) {
        return hasAllPermissions(roles);
    }

    @Override
    public boolean hasPermission(String perm) {
        final List<? extends GrantedAuthority> permStrList = getAuthorizationManager().getUserAuthorities();
        final List<String> collect = permStrList.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return hasIdentifier(collect, perm);
    }

    @Override
    public boolean hasAnyPermissions(String... perms) {
        final List<? extends GrantedAuthority> permStrList = getAuthorizationManager().getUserAuthorities();
        final List<String> collect = permStrList.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return hasAnyIdentifiers(collect, perms);
    }

    @Override
    public boolean hasAllPermissions(String... perms) {
        final List<? extends GrantedAuthority> permStrList = getAuthorizationManager().getUserAuthorities();
        final List<String> collect = permStrList.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        return hasAllIdentifiers(collect, perms);
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    private SimpleSession getUser() {
        final SimpleSession user = getCurrentUser();
        if (null == user) {
            throw new UnAuthticatedException();
        } else {
            return user;
        }
    }

}
