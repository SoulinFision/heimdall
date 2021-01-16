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

package com.luter.heimdall.core.config;

import com.luter.heimdall.core.config.property.AuthorityProperty;
import com.luter.heimdall.core.config.property.CookieProperty;
import com.luter.heimdall.core.config.property.SchedulerProperty;
import com.luter.heimdall.core.config.property.SessionProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 系统参数配置
 *
 * @author Luter
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Config {
    /**
     * Session参数配置
     */
    private SessionProperty session = new SessionProperty();
    /**
     * Cookie参数配置
     */
    private CookieProperty cookie = new CookieProperty();
    /**
     * Session定时清理任务配置
     */
    private SchedulerProperty scheduler = new SchedulerProperty();

    /**
     * 权限缓存配置
     */
    private AuthorityProperty authority = new AuthorityProperty();
}
