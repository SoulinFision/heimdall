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

package com.luter.heimdall.core.authorization.authority;

import java.io.Serializable;

/**
 * 授权信息接口
 *
 * @author luter
 */
public interface GrantedAuthority extends Serializable {
    /**
     * 获取权限标志
     * <p>
     * 通过注解授权的的时候会用到
     * 对于 restful 资源，也就是MethodAndUrlGrantedAuthority类型权限，
     * 权限标志位:METHOD:URL 格式，如：POST:/pet/cat,method 为大写；
     * <p>
     * 对于 精确路由 url 资源和角色资源（SimpleGrantedAuthority），
     * 这种模式下的 url 不重复
     * 标志为权限标志或者角色标识集合,不区分 method。
     * <p>
     * 角色标识如： {"admin","user"}
     * 权限标识如：{"catSave"}
     *
     * @return the authority
     */
    String getAuthority();
}
