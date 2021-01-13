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

package com.luter.heimdall.core.details;

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.authorization.authority.MethodAndUrlGrantedAuthority;
import com.luter.heimdall.core.authorization.authority.SimpleGrantedAuthority;

import java.io.Serializable;
import java.util.List;

/**
 * The interface User details.
 *
 * @author luter
 * @see DefaultSimpleUserDetails
 */
public interface UserDetails extends Serializable {

    /**
     * 用这个数据判断用户是否登录,可以是用户id,手机号、用户名等等，只要能全局唯一标识这个用户即可
     * <p>
     * eg:
     * 1、 租户ID+用户名称: 这样，同一个用户名称不同租户的用户可以各自登录不受影响
     * <p>
     * 2、设备类型+用户ID: 同一个用户，可从不同终端登录，而不至于被踢下线
     *
     * @return the principal
     */
    String getPrincipal();

    /**
     * 用这个判断用户是否启用
     *
     * @return the boolean
     */
    boolean enabled();

    /**
     * 此用户拥有的角色
     *
     * @return the roles
     */
    List<String> getRoles();

    /**
     * 此用户具备的权限标识符
     *
     * @return the permission
     * @see MethodAndUrlGrantedAuthority
     * @see SimpleGrantedAuthority
     */
    List<? extends GrantedAuthority> getAuthorities();


}
