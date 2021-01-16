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


import com.luter.heimdall.core.config.Config;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * 监听token过期事件__keyevent@数据库__:expired"
 * <p>
 * 当Session过期后，同步从 ActiveUser  Hash 和 activeSessionId的ZSet  缓存中删除对应Session数据
 *
 * @author Luter
 */
@Slf4j
public class RedisSessionKeyDeletedListener extends RedisKeyDeletedEventMessageListener {
    /**
     * The User cache redis.
     */
    @Autowired
    private SessionDAO sessionDAO;


    /**
     * Instantiates a new Session redis key deleted listener.
     *
     * @param listenerContainer the listener container
     */
    public RedisSessionKeyDeletedListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }


    /**
     * 针对 redis 数据失效事件，进行数据处理
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.debug("Session 删除事件  redis key :  [{}]", message.toString());
        final Config config = ConfigManager.getConfig();
        String expiredKey = message.toString();
        if (StrUtils.isNotBlank(expiredKey) && expiredKey.startsWith(config.getSession().getSessionIdPrefix())) {
            String sessionId = expiredKey.replace(config.getSession().getSessionIdPrefix(), "");
            sessionDAO.clearOnlineUserCache(sessionId);

        } else {
            log.debug("Session 删除事件,key:[{}]", expiredKey);
        }

    }
}