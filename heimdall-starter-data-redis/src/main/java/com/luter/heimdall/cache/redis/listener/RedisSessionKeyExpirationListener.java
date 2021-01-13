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

import com.luter.heimdall.core.config.Config;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 监听token过期事件__keyevent@数据库__:expired"
 * <p>
 * 当Session被删除后，同步从 ActiveUser  Hash 和 activeSessionId的ZSet  缓存中删除对应Session数据
 * <p>
 * <p>
 * 此处，也可以把过期后的 SessionId 单独存储到别处(过期 Session 表)，当用户继续以此 SessionId访问系统的时候，
 * <p>
 * 如果存在，就返回给用户 SessionId 过期错误，这样会比较友好一些。
 * <p>
 * 当然，你也得考虑过期 Session 存储的删除机制，要不然你系统哪天访问量暴增，你就彻底火了。。。
 *
 * @author Luter
 */
@Slf4j
public class RedisSessionKeyExpirationListener extends RedisKeyExpiredEventMessageListener {
    /**
     * The User cache redis.
     */
    @Autowired
    private StringRedisTemplate userCacheRedis;

    /**
     * Instantiates a new Session redis key expiration listener.
     *
     * @param listenerContainer the listener container
     */
    public RedisSessionKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 针对 redis 数据失效事件，进行数据处理
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Session 过期事件  redis key :  [{}]", message.toString());
        final Config config = ConfigManager.getConfig();
        String expiredKey = message.toString();
        if (StrUtils.isNotBlank(expiredKey) && expiredKey.startsWith(config.getSession().getSessionIdPrefix())) {
            //去掉前缀
            String sessionId = expiredKey.replace(config.getSession().getSessionIdPrefix(), "");
            log.info("Session 过期事件 ,key:[{}],从zSet删除", sessionId);
            //删除ZSet中对应Key (SessionId)
            final Long session = userCacheRedis.opsForZSet().remove(config.getSession().getActiveSessionCacheKey(), sessionId);
            log.info("Session 过期事件 ,key:[{}],从 Session zSet 删除，结果:{}", sessionId, session);
            //拿到 Hash 中所有数据
            final Map<Object, Object> entries = userCacheRedis.opsForHash().entries(config.getSession().getActiveUserCacheKey());
            List<String> toBeDeleted = new ArrayList<>();
            if (!entries.isEmpty()) {
                //遍历，如果value(sessionId)  与传入的SessionId相同,把key加入待删除List
                for (Map.Entry<Object, Object> data : entries.entrySet()) {
                    if (data.getValue().equals(sessionId)) {
                        toBeDeleted.add(data.getKey().toString());
                    }
                }
            }
            // Hash 中有数据要删除
            if (!toBeDeleted.isEmpty()) {
                final Long user = userCacheRedis.opsForHash().delete(config.getSession().getActiveUserCacheKey(), toBeDeleted.toArray());
                log.info("Session 过期事件 ,key:[{}],从 User Hash 删除，结果:{}", sessionId, user);
            }

        } else {
            log.info("Session 过期事件,key:[{}]", expiredKey);
        }

    }
}