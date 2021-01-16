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

package com.luter.heimdall.core.details;

/**
 * 默认基本用户详情类
 *
 * @author Luter
 */
public class DefaultSimpleUserDetails implements UserDetails {
    /**
     * 全局唯一标识，通过此字段唯一标识一个在认证系统中管理的信息实体。
     * <p>
     * 如何判断是否登录？
     * 1、如果请求中携带了SessionId,则会通过SessionID来进行判断。
     * 2、通过用户principal判断。
     * 如何判断用户是否重复登录?
     * 1、通过用户principal判断。
     * <p>
     * 系统根据principal参数在：缓存(SessionProperty.activeUserCacheKey)中遍历查找
     * key = principal的记录，从而获取到这个principal对应的SessionId，然后获取这个SessionId是否
     * 可用，如果可用，说明principal已经登录，否则未登录。
     *
     * <p>
     * 比如，可以使用终端类型+用户ID+手机号等参数组合成principal，
     * eg: PC:10001:18888888888
     * <p>
     * 系统根据此字段可以唯一确定某个用户是否登录
     */
    private String principal;
    /**
     * The Enabled.
     */
    private boolean enabled;

    @Override
    public String getPrincipal() {
        return principal;
    }

    @Override
    public boolean enabled() {
        return enabled;
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
     * Is enabled boolean.
     *
     * @return the boolean
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets enabled.
     *
     * @param enabled the enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
