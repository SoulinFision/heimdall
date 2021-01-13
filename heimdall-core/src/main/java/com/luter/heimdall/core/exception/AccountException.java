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

package com.luter.heimdall.core.exception;


/**
 * 账户相关异常
 *
 * @author :luter
 */
public class AccountException extends HeimdallException {

    /**
     * Instantiates a new Account exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public AccountException(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * Instantiates a new Account exception.
     *
     * @param code    the code
     * @param message the message
     */
    public AccountException(Integer code, String message) {
        super(code, message);
    }


    /**
     * Instantiates a new Account exception.
     *
     * @param message the message
     */
    public AccountException(String message) {
        super(message);
    }

}
