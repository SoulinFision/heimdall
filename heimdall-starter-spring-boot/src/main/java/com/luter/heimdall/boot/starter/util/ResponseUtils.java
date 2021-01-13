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

package com.luter.heimdall.boot.starter.util;


import com.luter.heimdall.boot.starter.model.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * 全局返回数据工具类
 *
 * @author Luter
 */
@Slf4j
public final class ResponseUtils {
    /**
     * 判断一个请求是否是json请求
     *
     * @param request the request
     * @return the boolean
     */
    public static Boolean isJson(HttpServletRequest request) {
        String header = request.getHeader("Accept");
        String headerA = request.getHeader("Content-Type");
        String headerB = request.getHeader("X-Requested-With");
        boolean result = (header != null && header.contains("json")) ||
                headerA != null && headerA.contains("json") ||
                "XMLHttpRequest".equalsIgnoreCase(headerB);
        log.debug("JSON请求类型判断=Accept:" + header + ",Content-Type:" + headerA + ",XMLHttpRequest:" + headerB + ",JSON:" + result);
        return result;
    }


    /**
     * 往客户端响应json格式消息
     *
     * @param response the response
     * @param status   HttpStatus状态
     * @param data     返回的数据结构体
     */
    public static void sendJsonResponse(HttpServletResponse response, int status, Object data) {
        String jsonStr = JacksonUtils.objectToJson(data);
        log.debug("send Json to client:\n" + jsonStr);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        try {
            response.getWriter().print(jsonStr);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            log.error("返回json处理错误");
        }
    }

    /**
     * Send json response.
     *
     * @param response the response
     * @param status   the status
     * @param data     the data
     */
    public static void sendJsonResponse(HttpServletResponse response, HttpStatus status, Object data) {
        sendJsonResponse(response, status.value(), data);
    }

    /**
     * 往客户端响应200 消息
     *
     * @param response the response
     * @param data     the data
     */
    public static void sendSuccessJsonResponse(HttpServletResponse response, Object data) {
        sendJsonResponse(response, HttpStatus.OK, data);
    }


    /**
     * 返回默认处理成功消息
     *
     * @return the response entity 返回默认成功消息
     */
    public static ResponseEntity<ResponseVO<Void>> ok() {
        return ResponseEntity.ok(ResponseVO.ok());
    }

    /**
     * 返回处理成功消息
     *
     * @param msg the msg
     * @return the response entity
     */
    public static ResponseEntity<ResponseVO<Void>> ok(String msg) {
        return ResponseEntity.ok(ResponseVO.ok(msg));
    }

    /**
     * 直接返回responseVo对象,不包含数据
     *
     * @param responseVo the response vo
     * @return the response entity
     */
    public static ResponseEntity<ResponseVO<Void>> response(ResponseVO<Void> responseVo) {
        HttpStatus status = HttpStatus.valueOf(responseVo.getStatus());
        return ResponseEntity.status(status).body(responseVo);
    }

    /**
     * 返回处理成功消息，携带数据
     *
     * @param <T>  the type parameter
     * @param data 需要携带的数据
     * @return the response entity
     */
    public static <T> ResponseEntity<ResponseVO<T>> ok(T data) {
        return ResponseEntity.ok(ResponseVO.ok(data));
    }

    /**
     * 返回处理成功消息，携带数据
     *
     * @param <T>  the type parameter
     * @param msg  the msg
     * @param data the data
     * @return the response entity
     */
    public static <T> ResponseEntity<ResponseVO<T>> ok(String msg, T data) {
        return ResponseEntity.ok(ResponseVO.ok(msg, data));
    }

    /**
     * Restful POST 数据创建成功 httpstatus:201
     *
     * @param msg the msg
     * @return the response entity
     */
    public static ResponseEntity<ResponseVO<Void>> created(String msg) {
        return response(ResponseVO.ok(HttpStatus.CREATED, msg));
    }

    /**
     * Restful PUT 数据修改成功 httpstatus:202
     *
     * @param msg the msg
     * @return the response entity
     */
    public static ResponseEntity<ResponseVO<Void>> accepted(String msg) {
        return response(ResponseVO.ok(HttpStatus.ACCEPTED, msg));
    }

    /**
     * Restful DELETE 数据删除成功 httpstatus:204
     *
     * @param msg the msg
     * @return the response entity
     */
    public static ResponseEntity<ResponseVO<Void>> deleted(String msg) {
        return response(ResponseVO.ok(HttpStatus.NO_CONTENT, msg));
    }

    /**
     * 返回失败消息
     *
     * @param msg the msg   错误消息
     * @return the response entity 默认返回 status:500错误
     */
    public static ResponseEntity<ResponseVO<Void>> fail(String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(HttpStatus.INTERNAL_SERVER_ERROR, msg));
    }

    /**
     * 返回失败消息,带数据
     *
     * @param <T>  the type parameter
     * @param msg  the msg   错误消息
     * @param data the data
     * @return the response entity 默认返回 status:500错误
     */
    public static <T> ResponseEntity<ResponseVO<T>> fail(String msg, T data) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(msg, data));
    }


    /**
     * 返回失败消息
     *
     * @param code the code  业务系统错误代码，可以给前端作为500失败后的进一步处理判断依据
     * @param msg  the msg   错误消息
     * @return the response entity 默认返回 status:500错误
     */
    public static ResponseEntity<ResponseVO<Void>> fail(int code, String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(HttpStatus.INTERNAL_SERVER_ERROR, code, msg));
    }

    /**
     * 返回失败消息,携带数据
     *
     * @param <T>  the type parameter
     * @param code the code  业务系统错误代码，可以给前端作为500失败后的进一步处理判断依据
     * @param msg  the msg   错误消息
     * @param data the data
     * @return the response entity 默认返回 status:500错误
     */
    public static <T> ResponseEntity<ResponseVO<T>> fail(int code, String msg, T data) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(HttpStatus.INTERNAL_SERVER_ERROR, code, msg, data));
    }

    /**
     * 返回失败消息
     *
     * @param status HttpStatus 状态
     * @param msg    the msg   错误消息
     * @return the response entity 默认返回 status:500错误
     */
    public static ResponseEntity<ResponseVO<Void>> fail(HttpStatus status, String msg) {
        return ResponseEntity.status(status)
                .body(ResponseVO.fail(status, msg));
    }

    /**
     * 返回失败消息
     *
     * @param status HttpStatus 状态
     * @param msg    the msg   错误消息
     * @param error  the error
     * @return the response entity 默认返回 status:500错误
     */
    public static ResponseEntity<ResponseVO<Void>> fail(HttpStatus status, String msg, String error) {
        return ResponseEntity.status(status).body(ResponseVO.fail(status, msg, error));
    }

    /**
     * 返回失败消息:BAD_REQUEST
     *
     * @param msg the msg   错误消息
     * @return the response entity 默认返回 status:500错误
     */
    public static ResponseEntity<ResponseVO<Void>> badRequest(String msg) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.fail(HttpStatus.BAD_REQUEST, msg));
    }

    /**
     * 返回未登录消息
     *
     * @return the response entity
     */
    public static ResponseEntity<ResponseVO<Void>> unauthenticated() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseVO.fail(HttpStatus.UNAUTHORIZED, "请登录后操作"));
    }

    /**
     * 返回无权操作消息
     *
     * @return the response entity
     */
    public static ResponseEntity<ResponseVO<Void>> unauthorized() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseVO.fail(HttpStatus.FORBIDDEN, "不具备此操作的权限"));
    }

    /**
     * 返回未登录消息
     *
     * @param response the response
     */
    public static void sendUnauthenticated(HttpServletResponse response) {
        sendJsonResponse(response, HttpStatus.UNAUTHORIZED, ResponseVO.fail(HttpStatus.UNAUTHORIZED, "请登录后操作"));
    }

    /**
     * 返回无权操作消息
     *
     * @param response the response
     */
    public static void sendUnauthorized(HttpServletResponse response) {
        sendJsonResponse(response, HttpStatus.FORBIDDEN, ResponseVO.fail(HttpStatus.UNAUTHORIZED, "不具备此操作的权限"));
    }
}
