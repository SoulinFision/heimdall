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

package com.luter.heimdall.sample.simple.details;

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.authorization.authority.SimpleGrantedAuthority;
import com.luter.heimdall.core.details.DefaultSimpleUserDetails;
import com.luter.heimdall.core.details.UserDetails;
import com.luter.heimdall.sample.common.dto.SysUserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义用户详情实体
 *
 * @author Luter
 * @see UserDetails
 * @see DefaultSimpleUserDetails
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class PcUserDetails implements UserDetails {
    /**
     * The User.
     */
    private SysUserDTO user;
    /**
     * The Principal.
     */
    private String principal;
    /**
     * The Authorities.
     */
    private List<? extends GrantedAuthority> authorities;
    /**
     * The Roles.
     */
    private List<String> roles;

    /**
     * Instantiates a new Default user detail.
     *
     * @param user the user
     */
    public PcUserDetails(SysUserDTO user) {
        this.user = user;
    }

    @Override
    public String getPrincipal() {
        return "PC:" + user.getId();
    }

    @Override
    public boolean enabled() {
        return user.getEnabled();
    }


    @Override
    public List<String> getRoles() {
        return user.getRoles();
    }

    @Override
    public List<? extends GrantedAuthority> getAuthorities() {
        //构造精确url授权权限信息载体
        return user.getResources().stream().map(d -> new SimpleGrantedAuthority(d.getPerm())).collect(Collectors.toList());
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public SysUserDTO getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(SysUserDTO user) {
        this.user = user;
    }

    /**
     * Sets principal.
     *
     * @param principal the principal
     */
    public void setPrincipal(String principal) {
        this.principal = principal;
    }


    /**
     * Sets authorities.
     *
     * @param authorities the authorities
     */
    public void setAuthorities(List<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    /**
     * Sets roles.
     *
     * @param roles the roles
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
