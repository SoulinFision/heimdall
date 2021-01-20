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

package com.luter.heimdall.boot.starter.config.property;

import lombok.Data;

@Data
public class AuthorityProperty {
    /**
     * 是否开启系统权限缓存，默认：true，开启
     * <p>
     * 如果关闭，则每次权限校验都会从数据库直接获取
     */
    private boolean sysCachedEnabled = true;
    /**
     * 系统权限在缓存中的key
     */
    private String sysCachedKey = "heimdall:sysAuthorities";
    /**
     * <p>
     * 系统权限在缓存中保存的时长，单位:小时.
     * <p>
     * 超过将会被清理，默认 :24小时.
     * <p>
     * 过期时间尽量设置的长一些
     */
    private long sysExpire = 24;
    /**
     * 是否开启用户权限缓存，默认：true，开启
     * <p>
     * 如果关闭，则每次权限校验都会从数据库直接获取
     */
    private boolean userCachedEnabled = true;
    /**
     * 用户权限在缓存中的key
     */
    private String userCachedKey = "heimdall:userAuthorities";
    /**
     * <p>
     * 用户权限在缓存中保存的时长，单位:小时.
     * <p>
     * 超过将会被清理，默认 :24小时
     * <p>
     * 过期时间尽量设置的长一些
     */
    private long userExpire = 24;


}
