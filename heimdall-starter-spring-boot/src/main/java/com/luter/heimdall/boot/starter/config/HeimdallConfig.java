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

package com.luter.heimdall.boot.starter.config;


import com.luter.heimdall.boot.starter.config.property.AuthorityProperty;
import com.luter.heimdall.boot.starter.config.property.CookieProperty;
import com.luter.heimdall.boot.starter.config.property.SchedulerProperty;
import com.luter.heimdall.boot.starter.config.property.SessionProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * spring 读取配置
 * <p>
 * 其实也可以直接扩展Config,但是就没有配置提醒了
 *
 * @author Luter
 */
@ConfigurationProperties(prefix = "heimdall.security")
@Slf4j
@Data
public class HeimdallConfig {
    /**
     * 系统权限在缓存中保存的时长，单位:小时.
     * <p>
     * 超过这个时长将会被清理，默认 :24小时
     */
    private long authoritiesExpire = 24;
    /**
     * Session配置
     */
    @NestedConfigurationProperty
    private SessionProperty session = new SessionProperty();
    /**
     * Cookie 配置
     */
    @NestedConfigurationProperty
    private CookieProperty cookie = new CookieProperty();
    /**
     * 定时清理任务配置
     */
    @NestedConfigurationProperty
    private SchedulerProperty scheduler = new SchedulerProperty();
    /**
     * 权限缓存配置
     */
    @NestedConfigurationProperty
    private AuthorityProperty authority = new AuthorityProperty();
}
