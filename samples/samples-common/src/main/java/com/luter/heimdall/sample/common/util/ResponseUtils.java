package com.luter.heimdall.sample.common.util;


import com.luter.heimdall.sample.common.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public final class ResponseUtils {
    public static Boolean isAjax(HttpServletRequest request) {
        String headerB = request.getHeader("X-Requested-With");
        boolean result = ("XMLHttpRequest".equalsIgnoreCase(headerB));
        log.debug("请求类型判断:XMLHttpRequest:" + headerB + ",result:" + result);
        return result;
    }

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


    public static void sendJsonResponse(HttpServletResponse response, int status, Object data) {
        String jsonStr = JacksonUtils.toJson(data);
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

    public static void sendJsonResponse(HttpServletResponse response, HttpStatus status, Object data) {
        sendJsonResponse(response, status.value(), data);
    }

    public static void sendSuccessJsonResponse(HttpServletResponse response, Object data) {
        sendJsonResponse(response, HttpStatus.OK, data);
    }


    public static ResponseEntity<ResponseVO<Void>> ok() {
        return ResponseEntity.ok(ResponseVO.ok());
    }

    public static ResponseEntity<ResponseVO<Void>> ok(String msg) {
        return ResponseEntity.ok(ResponseVO.ok(msg));
    }

    public static ResponseEntity<ResponseVO<Void>> response(ResponseVO<Void> responseVo) {
        HttpStatus status = HttpStatus.valueOf(responseVo.getStatus());
        return ResponseEntity.status(status).body(responseVo);
    }

    public static <T> ResponseEntity<ResponseVO<T>> ok(T data) {
        return ResponseEntity.ok(ResponseVO.ok(data));
    }

    public static <T> ResponseEntity<ResponseVO<T>> ok(String msg, T data) {
        return ResponseEntity.ok(ResponseVO.ok(msg, data));
    }

    public static ResponseEntity<ResponseVO<Void>> created(String msg) {
        return response(ResponseVO.ok(HttpStatus.CREATED, msg));
    }

    public static ResponseEntity<ResponseVO<Void>> accepted(String msg) {
        return response(ResponseVO.ok(HttpStatus.ACCEPTED, msg));
    }

    public static ResponseEntity<ResponseVO<Void>> deleted(String msg) {
        return response(ResponseVO.ok(HttpStatus.NO_CONTENT, msg));
    }

    public static ResponseEntity<ResponseVO<Void>> fail(String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(HttpStatus.INTERNAL_SERVER_ERROR, msg));
    }

    public static <T> ResponseEntity<ResponseVO<T>> fail(String msg, T data) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(msg, data));
    }


    public static ResponseEntity<ResponseVO<Void>> fail(int code, String msg) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(HttpStatus.INTERNAL_SERVER_ERROR, code, msg));
    }

    public static <T> ResponseEntity<ResponseVO<T>> fail(int code, String msg, T data) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseVO.fail(HttpStatus.INTERNAL_SERVER_ERROR, code, msg, data));
    }

    public static ResponseEntity<ResponseVO<Void>> fail(HttpStatus status, String msg) {
        return ResponseEntity.status(status)
                .body(ResponseVO.fail(status, msg));
    }

    public static ResponseEntity<ResponseVO<Void>> fail(HttpStatus status, String msg, String error) {
        return ResponseEntity.status(status).body(ResponseVO.fail(status, msg, error));
    }

    public static ResponseEntity<ResponseVO<Void>> badRequest(String msg) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseVO.fail(HttpStatus.BAD_REQUEST, msg));
    }

    public static ResponseEntity<ResponseVO<Void>> unauthenticated() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseVO.fail(HttpStatus.UNAUTHORIZED, "请登录后操作"));
    }

    public static ResponseEntity<ResponseVO<Void>> unauthorized() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseVO.fail(HttpStatus.FORBIDDEN, "不具备此操作的权限"));
    }

    public static void sendUnauthenticated(HttpServletResponse response) {
        sendJsonResponse(response, HttpStatus.UNAUTHORIZED, ResponseVO.fail(HttpStatus.UNAUTHORIZED, "请登录后操作"));
    }

    public static void sendUnauthorized(HttpServletResponse response) {
        sendJsonResponse(response, HttpStatus.FORBIDDEN, ResponseVO.fail(HttpStatus.UNAUTHORIZED, "不具备此操作的权限"));
    }
}
