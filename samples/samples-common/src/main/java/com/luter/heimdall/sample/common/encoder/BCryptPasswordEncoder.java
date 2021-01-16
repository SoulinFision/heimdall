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

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.luter.heimdall.core.exception.HeimdallException;
import com.luter.heimdall.core.utils.StrUtils;

/**
 * 密码加密解密   JbCrypt 实现
 *
 * @author Luter
 */
public class BCryptPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        if (StrUtils.isEmpty(rawPassword)) {
            throw new HeimdallException("密码不能为空");
        }
        return BCrypt.withDefaults().hashToString(12, rawPassword.toString().toCharArray());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (StrUtils.isEmpty(rawPassword) || StrUtils.isEmpty(encodedPassword)) {
            throw new HeimdallException("明文密码或者密文密码均不能为空");
        }
        return BCrypt.verifyer().verify(rawPassword.toString().toCharArray(), encodedPassword).verified;
    }


}
