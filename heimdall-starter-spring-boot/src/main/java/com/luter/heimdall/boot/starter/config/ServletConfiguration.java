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

import com.luter.heimdall.boot.starter.servlet.ServletHolderImpl;
import com.luter.heimdall.boot.starter.util.JacksonUtils;
import com.luter.heimdall.core.config.Config;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.config.property.AuthorityProperty;
import com.luter.heimdall.core.config.property.CookieProperty;
import com.luter.heimdall.core.config.property.SchedulerProperty;
import com.luter.heimdall.core.config.property.SessionProperty;
import com.luter.heimdall.core.servlet.ServletHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 公共配置，完成servlet注入和配置文件读取
 *
 * @author Luter
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(HeimdallConfig.class)
public class ServletConfiguration {
    /**
     * 完成servlet注入和配置文件读取
     * <p>
     * 默认直接注入
     *
     * @return the servlet holder
     */
    @Bean
    @Primary
    public ServletHolder servletHolder(HeimdallConfig config) {
        log.warn("注册 ServletHolder 实现");
        Config c = new Config();
        SchedulerProperty schedulerProperty = new SchedulerProperty();
        BeanUtils.copyProperties(config.getScheduler(), schedulerProperty);
        c.setScheduler(schedulerProperty);
        SessionProperty sessionProperty = new SessionProperty();
        BeanUtils.copyProperties(config.getSession(), sessionProperty);
        c.setSession(sessionProperty);
        CookieProperty cookieProperty = new CookieProperty();
        BeanUtils.copyProperties(config.getCookie(), cookieProperty);
        c.setCookie(cookieProperty);
        ConfigManager.setConfig(c);
        AuthorityProperty authorityProperty = new AuthorityProperty();
        BeanUtils.copyProperties(config.getAuthority(), authorityProperty);
        c.setAuthority(authorityProperty);
        ConfigManager.setConfig(c);
        log.warn("配置参数初始化完毕:\n{}", JacksonUtils.toPrettyJson(c));
        return new ServletHolderImpl();
    }

}
