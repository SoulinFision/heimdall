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

package com.luter.heimdall.core.manager.limiter;

import com.luter.heimdall.core.exception.ExcessiveAttemptsException;

/**
 * 登录密码重试次数限制
 * <p>
 * <p>
 * 1、在登录方法中校验密码出现错误，执行increase记录错误次数
 * <p>
 * 2、如果increase操作中发现已经超过最大重试次数，会返回 ExcessiveAttemptsException 异常
 * <p>
 * 3、执行完毕AuthenticationManager.login(UserDetails userDetails)登录方法后，执行remove清除锁定缓存。
 *
 * @author Luter
 * @see ExcessiveAttemptsException
 * @since 1.0.2
 */
public interface LoginPasswordRetryLimit {

    /**
     * 缓存重试次数
     *
     * @param key 根据业务提供一个唯一的 Key 用来标识限制谁的登录次数
     */
    void increase(String key);

    /**
     * 清除缓存数据，规定次数内正确后，清除已经缓存的数据
     *
     * @param key the key
     */
    void remove(String key);

    /**
     * 已经重试的次数
     *
     * @param key the key
     * @return the int
     */
    int count(String key);

    /**
     * 剩余有效次数，也就是说还可以重试几次
     *
     * @param key the key
     * @return the int
     */
    int leftCount(String key);


}
