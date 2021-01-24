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

/**
 * The type Cookie config.
 *
 * @author Luter
 */
@Data
public class CookieProperty {
    /**
     * 是否开启cookie
     */
    private Boolean enabled = false;
    /**
     * cookie名称,尽量与SessionName保持一致，也可以不同
     */
    private String name = "HSessionId";
    /**
     * 版本,默认0
     */
    private Integer version = 0;
    /**
     * 注释.默认空
     */
    private String comment = "";
    /**
     * 域 默认 空
     */
    private String domain;
    /**
     * cookie 生命周期 默认-1 无限期
     */
    private Integer maxAge = -1;
    /**
     * 路径，默认:/
     */
    private String path = "/";
    /**
     * 是否开启SSL,默认false
     */
    private Boolean secure = false;
    /**
     * 开启 Http only.默认true
     */
    private Boolean httpOnly = true;
}
