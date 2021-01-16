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
 * The type heimdall exception.
 *
 * @author Luter
 */
public class HeimdallException extends RuntimeException {
    /**
     * 异常信息
     */
    public String message;
    /**
     * 状态码
     */
    public Integer code;

    /**
     * Instantiates a new heimdall exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public HeimdallException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new heimdall exception.
     *
     * @param code    the code
     * @param message the message
     */
    public HeimdallException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }


    /**
     * Instantiates a new heimdall exception.
     *
     * @param message the message
     */
    public HeimdallException(String message) {
        this.message = message;
    }


    /**
     * Instantiates a new heimdall exception.
     */
    public HeimdallException() {
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}