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

import com.luter.heimdall.core.config.property.CookieProperty;
import com.luter.heimdall.core.config.property.SchedulerProperty;
import com.luter.heimdall.core.config.property.SessionProperty;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置管理器,静态单例
 * <p>
 * 如果需要第三方注入的配置参数，
 * 需要注意配置初始化顺序
 * <p>
 * 仅在方法中静态调用，
 * 不能在类中或者构造函数里进行调用
 * <p>
 * <p>
 * 如果此类首先被初始化，则默认加载本类配置文件
 *
 * @author luter
 * @see ConfigParser
 */
@Slf4j
public final class ConfigManager {
    /**
     * 配置文件 Bean
     */
    private static Config config;

    /**
     * Gets config.
     *
     * @return the config
     */
    public static Config getConfig() {
        if (null == config) {
            initConfig();
        }
        return config;
    }

    /**
     * Sets config.
     *
     * @param config the config
     */
    public static void setConfig(Config config) {
        //set 之前校验一下配置参数
        validConfig(config);
        ConfigManager.config = config;
    }

    /**
     * Init config.
     */
    public synchronized static void initConfig() {
        if (null == config) {
            setConfig(ConfigParser.parseConfig());
        }
    }


    private static void validConfig(Config config) {
        if (null == config) {
            throw new IllegalArgumentException("获取系统配置参数失败.配置为空,请检查系统配置文件是否名称或者格式是否正确？");
        }
        validSessionConfig(config.getSession());
        validCookieConfig(config.getCookie());
        validSchedulerConfig(config.getScheduler());
    }

    /**
     * Valid session config.
     *
     * @param config the config
     */
    private static void validSessionConfig(SessionProperty config) {
        if (config.getGlobalSessionTimeout() < 30) {
            throw new IllegalArgumentException("Session配置参数: globalSessionTimeout不能小于30秒");
        }
        if (StrUtils.isEmpty(config.getSessionName())) {
            throw new IllegalArgumentException("Session配置参数: sessionName 不能为空 ");
        }
        if (StrUtils.isEmpty(config.getSessionIdPrefix())) {
            log.warn("请注意 Session配置参数 sessionIdPrefix 为空，将采用系统默认配置 ");
        }
        if (StrUtils.isEmpty(config.getActiveSessionCacheKey())) {
            log.warn("请注意 Session配置参数 activeSessionCacheKey 为空，将采用系统默认配置 ");
        }
    }

    /**
     * Valid cookie config.
     *
     * @param config the config
     */
    private static void validCookieConfig(CookieProperty config) {
        if (StrUtils.isEmpty(config.getName())) {
            throw new IllegalArgumentException("Cookie配置参数: name 不能为空 ");
        }
    }

    /**
     * Valid scheduler config.
     *
     * @param config the config
     */
    private static void validSchedulerConfig(SchedulerProperty config) {
        if (config.getInitialDelay() < 1) {
            throw new IllegalArgumentException("Scheduler配置参数: initialDelay 不能低于 1 秒");
        }
        if (config.getPeriod() < 1) {
            throw new IllegalArgumentException("Scheduler配置参数: period 不能低于 1 秒");
        }
    }


}
