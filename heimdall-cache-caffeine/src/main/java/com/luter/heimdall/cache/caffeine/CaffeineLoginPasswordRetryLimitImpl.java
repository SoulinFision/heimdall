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

package com.luter.heimdall.cache.caffeine;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.luter.heimdall.core.cache.SimpleCache;
import com.luter.heimdall.core.exception.ExcessiveAttemptsException;
import com.luter.heimdall.core.manager.limiter.LoginPasswordRetryLimit;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于Caffeine缓存的登录次数限制
 *
 * @author Luter
 * @since 1.0.2
 */
@Slf4j
public class CaffeineLoginPasswordRetryLimitImpl implements LoginPasswordRetryLimit {
    /**
     * 缓存
     */
    private SimpleCache<String, AtomicInteger> retryCache;

    /**
     * 允许重试的最大次数,attemptLimit+1请求会被拒绝
     */
    private int attemptLimit = 3;
    /**
     * 是否开启重试限制
     */
    private boolean limitEnabled = true;
    /**
     * 缓存 key 前缀
     */
    private String keyPrefix = "heimdall:retryLimit:";
    /**
     * 锁定时长,单位：秒
     */
    private long lockedDuration = 120;

    /**
     * Instantiates a new Cached authentication retry limit.
     */
    public CaffeineLoginPasswordRetryLimitImpl() {
        this.retryCache = new CaffeineCache<>(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(lockedDuration))
                .build());
    }


    @Override
    public void increase(String key) {
        String cacheKey = keyPrefix + key;
        log.info("开始登录次数逻辑，用户:{}", cacheKey);
        if (limitEnabled) {
            //先拿
            AtomicInteger retryCount = retryCache.get(cacheKey);
            if (null == retryCount) {
                log.info("用户: {} 首次登录，初始化登录次数到缓存", cacheKey);
                retryCount = new AtomicInteger(0);
                retryCache.put(cacheKey, retryCount);
            }
            log.info("用户 : {} ,重试次数: {}", cacheKey, retryCount.get());
            if (retryCount.incrementAndGet() > attemptLimit) {
                log.warn("用户: {} 重试次数:{} ,超过最大次数限制:{}，锁定账户", cacheKey, retryCount, attemptLimit);
                //这里需要进行具体账号锁定逻辑
                throw new ExcessiveAttemptsException("重试次数太多，您的账号被锁定 "
                        + Duration.ofSeconds(lockedDuration).toMinutes() + "分钟.请稍后重试");
            }
            retryCache.put(cacheKey, retryCount);
        } else {
            log.warn("未开启登录重试次数限制功能");
        }

    }

    @Override
    public void remove(String key) {
        retryCache.remove(key);
    }

    @Override
    public int count(String key) {
        AtomicInteger retryCount = retryCache.get(keyPrefix + key);
        if (null != retryCount) {
            return retryCount.get();
        }
        return 0;
    }

    @Override
    public int leftCount(String key) {
        return getAttemptLimit() - count(key);
    }

    /**
     * Gets retry cache.
     *
     * @return the retry cache
     */
    public SimpleCache<String, AtomicInteger> getRetryCache() {
        return retryCache;
    }

    /**
     * Sets retry cache.
     *
     * @param retryCache the retry cache
     */
    public void setRetryCache(SimpleCache<String, AtomicInteger> retryCache) {
        this.retryCache = retryCache;
    }

    /**
     * Gets attempt limit.
     *
     * @return the attempt limit
     */
    public int getAttemptLimit() {
        return attemptLimit;
    }

    /**
     * Sets attempt limit.
     *
     * @param attemptLimit the attempt limit
     */
    public void setAttemptLimit(int attemptLimit) {
        this.attemptLimit = attemptLimit;
    }

    /**
     * Is limit enabled boolean.
     *
     * @return the boolean
     */
    public boolean isLimitEnabled() {
        return limitEnabled;
    }

    /**
     * Sets limit enabled.
     *
     * @param limitEnabled the limit enabled
     */
    public void setLimitEnabled(boolean limitEnabled) {
        this.limitEnabled = limitEnabled;
    }

    /**
     * Gets locked duration.
     *
     * @return the locked duration
     */
    public long getLockedDuration() {
        return lockedDuration;
    }

    /**
     * Sets locked duration.
     *
     * @param lockedDuration the locked duration
     */
    public void setLockedDuration(long lockedDuration) {
        this.lockedDuration = lockedDuration;
    }

    /**
     * Gets key prefix.
     *
     * @return the key prefix
     */
    public String getKeyPrefix() {
        return keyPrefix;
    }

    /**
     * Sets key prefix.
     *
     * @param keyPrefix the key prefix
     */
    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }
}
