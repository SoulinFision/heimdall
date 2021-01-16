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

package com.luter.heimdall.cache.redis.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 注册redis事件容器
 * <p>
 * 主要实现功能：
 * <p>
 * 认证系统维护了当前在线principal的Hash以及在线sessionId的ZSet
 * <p>
 * 当一个Session过期或者由于注销操作等被删除后，需要事件通知Hash和ZSet，对对应数据进行同步删除
 *
 * @author Luter
 */
@Slf4j
public class RedisMessageListenerAutoConfiguration {
    /**
     * 开启redis 事件监听
     *
     * @param connectionFactory the connection factory
     * @return the redis message listener container
     */
    @Bean
    @ConditionalOnMissingBean
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        log.warn("RedisMessageListenerContainer 初始化 ");
        return container;
    }
}
