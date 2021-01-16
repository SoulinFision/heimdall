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

package com.luter.heimdall.sample.common.encoder;

/**
 * 密码加密解密
 *
 * @author Luter
 */
public interface PasswordEncoder {
    /**
     * 加密
     *
     * @param rawPassword 明文密码
     * @return the string 密文密码
     */
    String encode(CharSequence rawPassword);

    /**
     * 是否匹配
     *
     * @param rawPassword     明文密码
     * @param encodedPassword 密文密码
     * @return the boolean
     */
    boolean matches(CharSequence rawPassword, String encodedPassword);


}
