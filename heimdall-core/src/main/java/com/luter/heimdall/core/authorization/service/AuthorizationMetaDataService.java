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

package com.luter.heimdall.core.authorization.service;


import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.manager.AuthorizationManager;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * 系统权限资源服务
 *
 * @author luter
 */
public interface AuthorizationMetaDataService {
    /**
     * 加载系统资源
     * <p>
     * 这个Map的数据约定了哪些系统资源(url)将被拦截和授权
     * <p>
     * 对于普通精确url资源授权，Map.key = 请求url, Map.value = 权限标识字符串
     * <p>
     * <p>
     * <p>
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
     * @see AuthorizationManager#isAuthorized(HttpServletRequest, boolean) AuthorizationManager#isAuthorized(HttpServletRequest, boolean)
     */
    Map<String, Collection<String>> loadSysAuthorities();

    /**
     * 加载用户权限
     */
    List<? extends GrantedAuthority> loadUserAuthorities(String principal);
}
