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

package com.luter.heimdall.core.authorization.authority;

import com.luter.heimdall.core.utils.StrUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Restful  风格路径的权限匹配标识定义
 *
 * @author luter
 */
@Data
@Accessors(chain = true)
public class MethodAndUrlGrantedAuthority implements GrantedAuthority {

    /**
     * 请求方法
     */
    private String method;

    /**
     * url路径
     */
    private String url;

    /**
     * Instantiates a new Method and url granted authority.
     */
    public MethodAndUrlGrantedAuthority() {
    }

    /**
     * Instantiates a new Method and url granted authority.
     *
     * @param method the method
     * @param url    the url
     */
    public MethodAndUrlGrantedAuthority(String method, String url) {
        this.method = method;
        this.url = url;
    }

    /**
     * method:url形式组合
     */
    @Override
    public String getAuthority() {
        return this.method + StrUtils.COLON + this.url;
    }
}