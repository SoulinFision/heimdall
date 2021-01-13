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
 * 未登录
 *
 * @author :luter
 */
public class UnAuthticatedException extends AccountException {

    /**
     * Instantiates a new Un authticated exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public UnAuthticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Luter exception.
     *
     * @param code    the code
     * @param message the message
     */
    public UnAuthticatedException(Integer code, String message) {
        super(code, message);
    }

    /**
     * Instantiates a new Luter exception.
     *
     * @param message the message
     */
    public UnAuthticatedException(String message) {
        super(message);
    }

    /**
     * 未登录认证异常
     * <p>
     * error:The current User is not authenticated.  Access denied.
     */
    public UnAuthticatedException() {
        super("The current User is not authenticated.  Access denied.");
    }

}
