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

package com.luter.heimdall.core.exception;


/**
 * 未授权
 *
 * @author :luter
 */
public class UnAuthorizedException extends AccountException {


    /**
     * Instantiates a new Un authorized exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public UnAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Luter exception.
     *
     * @param code    the code
     * @param message the message
     */
    public UnAuthorizedException(Integer code, String message) {
        super(code, message);
    }

    /**
     * 未授权异常
     * <p>
     * error: The current User is not authorized.  Access denied.
     */
    public UnAuthorizedException() {
        super(-403, "The current User is not authorized.  Access denied.");
    }

    /**
     * Instantiates a new Luter exception.
     *
     * @param message the message
     */
    public UnAuthorizedException(String message) {
        super(message);
    }

}
