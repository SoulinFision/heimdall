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

package com.luter.heimdall.cache.redis.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.data.redis.listener.KeyspaceEventMessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.lang.Nullable;

/**
 * The type Redis key deleted event message listener.
 *
 * @author Luter
 */
@Slf4j
public class RedisKeyDeletedEventMessageListener extends KeyspaceEventMessageListener implements ApplicationEventPublisherAware {
    /**
     * The Publisher.
     */
    @Nullable
    private ApplicationEventPublisher publisher;
    /**
     * The Redis properties.
     */
    @Autowired
    private RedisProperties redisProperties;

    /**
     * Instantiates a new Redis key deleted event message listener.
     *
     * @param listenerContainer the listener container
     */
    public RedisKeyDeletedEventMessageListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    protected void doRegister(RedisMessageListenerContainer listenerContainer) {
        //从系统配置参数中获取到redis的配置信息，组合事件匹配pattern
        final String tPattern = "__keyevent@" + redisProperties.getDatabase() + "__:del";
        final Topic topic = new PatternTopic(tPattern);
        log.warn("注册 redis key Del 事件监听， topic  pattern:{}", tPattern);
        listenerContainer.addMessageListener(this, topic);
    }

    @Override
    protected void doHandleMessage(Message message) {
        this.publishEvent(new RedisKeyExpiredEvent(message.getBody()));
    }

    /**
     * Publish event.
     *
     * @param event the event
     */
    protected void publishEvent(RedisKeyExpiredEvent event) {
        if (this.publisher != null) {
            this.publisher.publishEvent(event);
        }

    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

}
