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
import com.luter.heimdall.boot.starter.util.JacksonUtils;
import com.luter.heimdall.boot.starter.util.ResponseUtils;
import com.luter.heimdall.core.exception.UnAuthorizedException;
import com.luter.heimdall.core.exception.UnAuthticatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * whiteLabel 页面消息自定义处理
 *
 * @author Luter
 */
@Slf4j
public class BaseServletErrorController extends BasicErrorController {
    /**
     * Instantiates a new Base servlet error controller.
     *
     * @param errorAttributes    the error attributes
     * @param serverProperties   the server properties
     * @param errorViewResolvers the error view resolvers
     */
    @Autowired
    public BaseServletErrorController(ErrorAttributes errorAttributes,
                                      ServerProperties serverProperties,
                                      List<ErrorViewResolver> errorViewResolvers) {
        super(errorAttributes, serverProperties.getError(), errorViewResolvers);
    }

    /**
     * 根据配置参数确定错误页面
     */
    @Value("${server.error.name:${error.name:error}}")
    public String errorView;

    /**
     * 覆盖默认的Json响应
     */
    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        final ResponseVO<Void> error = getError(request);
        final Map<String, Object> stringObjectMap = AbstractHeimdalExceptionAdvice.convertResponseVoToMap(error);
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        return ResponseEntity.status(error.getStatus()).body(stringObjectMap);

    }

    /**
     * 覆盖默认的HTML响应
     */
    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        final ResponseVO<Void> error = getError(request);
        final Map<String, Object> model = AbstractHeimdalExceptionAdvice.convertResponseVoToMap(error);
        response.setStatus(status.value());
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        return (modelAndView != null) ? modelAndView : new ModelAndView(errorView, model);

    }

    /**
     * 重新定义异常信息
     *
     * @param request the request
     * @return the error
     */
    private ResponseVO<Void> getError(HttpServletRequest request) {
        final ErrorAttributeOptions of = ErrorAttributeOptions.of(
                ErrorAttributeOptions.Include.BINDING_ERRORS,
                ErrorAttributeOptions.Include.EXCEPTION,
                ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.STACK_TRACE);
        Map<String, Object> body = getErrorAttributes(request, of);
        HttpStatus status = super.getStatus(request);
        final Object exceptionObject = body.get("exception");
        if (null != exceptionObject) {
            String exception = body.get("exception").toString();
            if (UnAuthticatedException.class.getName().equals(exception)) {
                status = HttpStatus.UNAUTHORIZED;
            }
            if (UnAuthorizedException.class.getName().equals(exception)) {
                status = HttpStatus.FORBIDDEN;
            }
        }
        String msg = body.get("error").toString();
        switch (status.value()) {
            case 401:
                msg = "请登录后操作";
                break;
            case 403:
                msg = "您不具备访问此资源的权限";
                break;
            case 404:
                msg = "您要访问的资源不存在";
                break;
            case 405:
                msg = "请求方法错误";
                break;
            default:
                break;
        }
        String res = body.get("path").toString();
        boolean isJson = ResponseUtils.isJson(request);
        //输出自定义的Json格式
        final ResponseVO<Void> error = ResponseVO.fail(status, msg, "Basic Request Error :  " + body.get("message").toString());
        log.error("===系统错误====: 资源: {}, JSON请求 : {}, Error :{} ", res, isJson, JacksonUtils.toJson(error));
        return error;
    }
}
