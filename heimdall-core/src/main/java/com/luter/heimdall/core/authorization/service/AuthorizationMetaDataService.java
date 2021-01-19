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

package com.luter.heimdall.core.authorization.service;


import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.manager.AuthorizationManager;
import com.luter.heimdall.core.session.SimpleSession;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 系统权限服务
 * <p>
 * <p>
 * 1、加载系统权限
 * <p>
 * 2、加载登录用户的权限
 *
 * @author luter
 */
public interface AuthorizationMetaDataService {
    /**
     * 加载 系统权限
     * <p>
     * 系统权限数据约定了哪些系统资源(url)将被拦截和需要具备什么权限
     * <p>
     * 对于普通精确路由url资源授权，Map.key = 请求url, Map.value = 权限标识或者角色标识集合
     * <p>
     * 对于Restful 形式的资源授权，Map.key = 请求url, Map.value = 不限，无业务作用
     * <p>
     * restful权限首先会根据请求url，从这个map中判断是否需要进行拦截
     * <p>
     * 拦截到之后，再获取当前用户信息，从信息中解析出用户具有的权限：UserDetails.getAuthorities()
     * <p>
     * 进入授权逻辑，首先判断 权限是否是MethodAndUrlGrantedAuthority类型的，如果是则认为是restful资源
     * <p>
     * 开始遍历用户所有的MethodAndUrlGrantedAuthority资源，查找里面是否有url 和method与当前请求相匹配的数据
     * <p>
     * 如果有，则认为授权通过
     *
     * @return the map
     * @see AuthorizationManager#isAuthorized(HttpServletRequest, boolean) AuthorizationManager#isAuthorized(HttpServletRequest, boolean)AuthorizationManager#isAuthorized(HttpServletRequest, boolean)
     * @since 1.0.2
     */
    Map<String, Collection<String>> loadSysAuthorities();

    /**
     * 加载 用户权限
     * <p>
     * <p>
     * 用户访问受保护资源之前，会首先通过此方法获取当前用户具备的权限信息
     * <p>
     * 如果缓存开启，则首先从缓存获取，缓存中不存在，调用此方法获取，获取到后进行缓存。
     *
     * @param session the session
     * @return the list
     * @since 1.0.2
     */
    List<? extends GrantedAuthority> loadUserAuthorities(SimpleSession session);
}
