
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

package com.luter.heimdall.boot.starter.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 全局通用返回数据模型
 *
 * @param <T> the type parameter
 * @author Luter
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseVO<T> {
    /**
     * 默认的code
     */
    public static final Integer DEFAULT_CODE = -1;

    /**
     * 返回数据，可以是一个，也可以是多个
     */
    private T data;
    /**
     * 返回消息
     */
    private String msg;
    /**
     * 异常错误,生产环境可不返回给前端
     */
    private String error;

    /**
     * The Timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS", timezone = "GMT+8")
    private LocalDateTime timestamp = LocalDateTime.now();
    /**
     * httpstatus 错误码
     *
     * @see HttpStatus
     */
    private Integer status;
    /**
     * 附加属性，可以为特定httpstatus下返回数据表示业务层面的错误码
     */
    private Integer code;

    /**
     * Ok response vo.
     *
     * @param <T> the type parameter
     * @return the response vo
     */
    public static <T> ResponseVO<T> ok() {
        return build(HttpStatus.OK.value(), DEFAULT_CODE, "success", "", null);
    }

    /**
     * Ok response vo.
     *
     * @param <T>  the type parameter
     * @param data the data
     * @return the response vo
     */
    public static <T> ResponseVO<T> ok(T data) {
        return build(HttpStatus.OK.value(), DEFAULT_CODE, "success", "success", data);
    }

    /**
     * Ok response vo.
     *
     * @param <T> the type parameter
     * @param msg the msg
     * @return the response vo
     */
    public static <T> ResponseVO<T> ok(String msg) {
        return build(HttpStatus.OK.value(), DEFAULT_CODE, msg, "", null);
    }

    /**
     * Ok response vo.
     *
     * @param <T>  the type parameter
     * @param msg  the msg
     * @param data the data
     * @return the response vo
     */
    public static <T> ResponseVO<T> ok(String msg, T data) {
        return build(HttpStatus.OK.value(), DEFAULT_CODE, msg, "", data);
    }

    /**
     * Ok response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param msg    the msg
     * @return the response vo
     */
    public static <T> ResponseVO<T> ok(HttpStatus status, String msg) {
        return build(HttpStatus.OK.value(), DEFAULT_CODE, msg, "", null);
    }

    /**
     * Fail response vo.
     *
     * @param <T> the type parameter
     * @param msg the msg
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(String msg) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), DEFAULT_CODE, msg, "", null);
    }


    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param msg    the msg
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(Integer status, String msg) {
        return build(status, DEFAULT_CODE, msg, "", null);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param msg    the msg
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(HttpStatus status, String msg) {
        return build(status.value(), DEFAULT_CODE, msg, "", null);
    }


    /**
     * 错误的请求
     *
     * @param <T> the type parameter
     * @param msg the msg
     * @return the response vo
     */
    public static <T> ResponseVO<T> badRequest(String msg) {
        return build(HttpStatus.FORBIDDEN.value(), DEFAULT_CODE, msg, "bad request", null);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param msg    the msg
     * @param error  the error
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(HttpStatus status, String msg, String error) {
        return build(status.value(), DEFAULT_CODE, msg, error, null);
    }

    /**
     * Fail response vo.
     *
     * @param <T>  the type parameter
     * @param msg  the msg
     * @param data the data
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(String msg, T data) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR.value(), 0, msg, "", data);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param msg    the msg
     * @param data   the data
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(HttpStatus status, String msg, T data) {
        return build(status.value(), DEFAULT_CODE, msg, "", data);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param code   the code
     * @param msg    the msg
     * @param data   the data
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(HttpStatus status, int code, String msg, T data) {
        return build(status.value(), code, msg, "", data);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param msg    the msg
     * @param error  the error
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(Integer status, String msg, String error) {
        return build(status, DEFAULT_CODE, msg, error, null);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param code   the code
     * @param msg    the msg
     * @param error  the error
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(Integer status, Integer code, String msg, String error) {
        return build(status, code, msg, error, null);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param code   the code
     * @param msg    the msg
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(Integer status, Integer code, String msg) {
        return build(status, code, msg, "", null);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param code   the code
     * @param msg    the msg
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(HttpStatus status, Integer code, String msg) {
        return build(status.value(), code, msg, "", null);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param msg    the msg
     * @param error  the error
     * @param data   the data
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(Integer status, String msg, String error, T data) {
        return build(status, DEFAULT_CODE, msg, error, data);
    }

    /**
     * Fail response vo.
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param code   the code
     * @param msg    the msg
     * @param error  the error
     * @param data   the data
     * @return the response vo
     */
    public static <T> ResponseVO<T> fail(Integer status, Integer code, String msg, String error, T data) {
        return build(status, code, msg, error, data);
    }

    /**
     * build
     *
     * @param <T>    the type parameter
     * @param status the status
     * @param code   the code
     * @param msg    the msg
     * @param error  the error
     * @param data   the data
     * @return the response vo
     */
    private static <T> ResponseVO<T> build(Integer status, Integer code, String msg, String error, T data) {
        ResponseVO<T> apiResult = new ResponseVO<>();
        apiResult.setStatus(status);
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        apiResult.setData(data);
        apiResult.setError(error);
        return apiResult;
    }


    /**
     * Is 1 xx informational boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    protected boolean is1xxInformational() {
        return HttpStatus.valueOf(this.status).is1xxInformational();
    }

    /**
     * Is 2 xx successful boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    protected boolean is2xxSuccessful() {
        return HttpStatus.valueOf(this.status).is2xxSuccessful();
    }

    /**
     * Is 3 xx redirection boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    protected boolean is3xxRedirection() {
        return HttpStatus.valueOf(this.status).is3xxRedirection();
    }

    /**
     * Is 4 xx client error boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    protected boolean is4xxClientError() {
        return HttpStatus.valueOf(this.status).is4xxClientError();
    }

    /**
     * Is 5 xx server error boolean.
     *
     * @return the boolean
     */
    @JsonIgnore
    protected boolean is5xxServerError() {
        return HttpStatus.valueOf(this.status).is5xxServerError();
    }

    /**
     * 4xx或者5xx错误
     *
     * @return the boolean
     */
    @JsonIgnore
    protected boolean isClientAndServerError() {
        return this.is4xxClientError() || this.is5xxServerError();
    }

}
