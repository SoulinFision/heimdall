
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

package com.luter.heimdall.boot.starter.exception;


import com.luter.heimdall.boot.starter.model.ResponseVO;
import com.luter.heimdall.boot.starter.util.ResponseUtils;
import com.luter.heimdall.core.exception.ExpiredSessionException;
import com.luter.heimdall.core.exception.UnAuthorizedException;
import com.luter.heimdall.core.exception.UnAuthticatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * heimdall 基础异常拦截
 * <p>
 * //@ConditionalOnClass({Servlet.class, DispatcherServlet.class})
 * //@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
 * //@ControllerAdvice
 *
 * @author Luter
 */
@Slf4j
public abstract class AbstractHeimdalExceptionAdvice {
    /**
     * 日志显示的格式:
     * <p>
     * <p>
     * ===发生错误:
     * <p>
     * Exception
     * <p>
     * Message
     * <p>
     * Error
     * <p>
     * Status
     */
    public static final String LOG_FORMAT = "\n===发生错误:\nException:{}\nMessage:{}\nError:{}\nStatus:{}\n";
    /**
     * 根据配置参数确定错误页面
     */
    @Value("${server.error.path:${error.path:error}}")
    public String errorView;


    /**
     * No request handler found exception handler object.
     *
     * @param request  the request
     * @param e        the e
     * @return the object
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public Object requestHandlerFoundExceptionHandler(HttpServletRequest request, NoHandlerFoundException e) {
        String msg = "没找到您要访问的资源:" + request.getMethod() + ":" + request.getRequestURI(),
                error = e.getMessage();
        log.error(LOG_FORMAT, e.getClass().getName(), msg, error, HttpStatus.NOT_FOUND);
        return dealError(request, ResponseVO.fail(HttpStatus.NOT_FOUND, msg, error));
    }

    /**
     * Http request method not supported exception object.
     *
     * @param request the request
     * @param e       the e
     * @return the object
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
    public Object httpRequestMethodNotSupportedException(HttpServletRequest request,
                                                         HttpRequestMethodNotSupportedException e) {
        String error = e.getMessage(), msg = "访问方法不被支持:" + request.getMethod() + ":" + request.getRequestURI();
        log.error(LOG_FORMAT, e.getClass().getName(), msg, error, HttpStatus.METHOD_NOT_ALLOWED);
        return dealError(request, ResponseVO.fail(HttpStatus.METHOD_NOT_ALLOWED, msg, error));
    }

    /**
     * Exception object.
     *
     * @param request  the request
     * @param e        the e
     * @return the object
     */
    @ExceptionHandler(value = {Exception.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object exception(HttpServletRequest request, Exception e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        int code = -1;
        Throwable resultCause = getRootExceptionThrowable(e);
        String message = "系统异常", error = resultCause.getMessage();
        if (e instanceof ExpiredSessionException) {
            message = "登录会话超时,请重新登录";
            code = ((ExpiredSessionException) e).getCode();
        }
        log.error(LOG_FORMAT, e.getClass().getName(), message, error, status);
        return dealError(request, ResponseVO.fail(status.value(), code, message, error, null));
    }


    /**
     * Exception object.
     *
     * @param request the request
     * @param e       the e
     * @return the object
     */
    @ExceptionHandler(value = {UnAuthticatedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Object exception(HttpServletRequest request, UnAuthticatedException e) {
        String message = "请登录", error = e.getMessage();
        log.error(LOG_FORMAT, e.getClass().getName(), message, error, HttpStatus.UNAUTHORIZED);
        return dealError(request, ResponseVO.fail(HttpStatus.UNAUTHORIZED, message, error));
    }

    /**
     * Exception object.
     *
     * @param request the request
     * @param e       the e
     * @return the object
     */
    @ExceptionHandler(value = {UnAuthorizedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Object exception(HttpServletRequest request, UnAuthorizedException e) {
        String message = "不具备操作权限", error = e.getMessage();
        log.error(LOG_FORMAT, e.getClass().getName(), message, error, HttpStatus.FORBIDDEN);
        return dealError(request, ResponseVO.fail(HttpStatus.FORBIDDEN, message, error));
    }

    /**
     * 根据请求类型确定返回json还是错误页面
     *
     * @param request the request
     * @param fail    the fail
     * @return the object
     */
    protected Object dealError(HttpServletRequest request, ResponseVO<?> fail) {
        return dealError(request, fail, errorView);
    }

    /**
     * 根据请求类型确定返回json还是错误页面
     *
     * @param request  the request
     * @param fail     the fail
     * @param viewName the view name
     * @return the object
     */
    protected Object dealError(HttpServletRequest request, ResponseVO<?> fail, String viewName) {
        //如果是json请求
        if (ResponseUtils.isJson(request)) {
            return ResponseEntity.status(fail.getStatus()).body(fail);
        }
        //否则返回错误视图
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(viewName);
        modelAndView.setStatus(HttpStatus.valueOf(fail.getStatus()));
        modelAndView.addAllObjects(convertResponseVoToMap(fail));
        return modelAndView;
    }

    /**
     * 递归找到最终cause
     *
     * @param e the e
     * @return the root exception throwable
     */
    protected Throwable getRootExceptionThrowable(Exception e) {
        if (null == e) {
            return null;
        }
        Throwable rootCause = e;
        while (null != rootCause.getCause()) {
            rootCause = rootCause.getCause();
        }
        return rootCause;

    }

    /**
     * Convert response vo to map map.
     *
     * @param responseVO the response vo
     * @return the map
     */
    public static Map<String, Object> convertResponseVoToMap(ResponseVO<?> responseVO) {
        Map<String, Object> data = new HashMap<>(6);
        data.put("msg", responseVO.getMsg());
        data.put("status", responseVO.getStatus());
        data.put("error", responseVO.getError());
        data.put("timestamp", responseVO.getTimestamp());
        data.put("code", responseVO.getCode());
        data.put("data", responseVO.getData());
        return data;
    }
}