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

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 权限标识符授权实体
 *
 * @author Luter
 */
@Data
@Accessors(chain = true)
public class SimpleGrantedAuthority implements GrantedAuthority {
    /**
     * 权限标志，或者角色标志。
     */
    private String attr;

    /**
     * Instantiates a new Simple granted authority.
     *
     * @param attr 权限标志 或者角色标志
     */
    public SimpleGrantedAuthority(String attr) {

        this.attr = attr;
    }

    /**
     * Instantiates a new Simple granted authority.
     */
    public SimpleGrantedAuthority() {
    }


    @Override
    public String getAuthority() {
        return this.attr;
    }

    /**
     * Sets authority.
     */
    public void setAuthority() {
    }


}
