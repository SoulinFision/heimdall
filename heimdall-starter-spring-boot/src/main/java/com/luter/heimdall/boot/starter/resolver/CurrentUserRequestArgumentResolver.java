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

package com.luter.heimdall.boot.starter.resolver;


import com.luter.heimdall.core.manager.AuthenticationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * 通过注解参数在Controller中获取当前登录用户信息
 * <p>
 * <p>
 * 配置
 * <p>
 * <p>
 * 在Springboot的WebMvcConfigurer中开启：
 * <p>
 * //@Autowired
 * <p>
 * //private CurrentUserRequestArgumentResolver currentUserRequestArgumentResolver;
 * <p>
 * //@Override
 * <p>
 * //public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
 * <p>
 * //argumentResolvers.add(currentUserRequestArgumentResolver);
 * <p>
 * //}
 *
 * <p>
 * 用法
 * <p>
 * <p>
 * 在Controller层的方法中:
 * <p>
 * //@RequestMapping("/current")
 * //public ResponseEntity<ResponseVO<SimpleSession>> current(@CurrentUser SimpleSession user) {
 * //   return ResponseUtils.ok(user);
 * //}
 *
 * @author luter
 * @see AuthenticationManager#getCurrentUser() AuthenticationManager#getCurrentUser()
 */
@Slf4j
@Service
public class CurrentUserRequestArgumentResolver implements HandlerMethodArgumentResolver {
    /**
     * The Authentication manager.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 入参筛选
     *
     * @param methodParameter 参数集合
     * @return 格式化后的参数
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(CurrentUser.class);
    }

    /**
     * @param methodParameter       入参集合
     * @param modelAndViewContainer model 和 view
     * @param nativeWebRequest      web相关
     * @param webDataBinderFactory  入参解析
     * @return 包装对象
     */
    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) {
        CurrentUser currentUser = methodParameter.getParameterAnnotation(CurrentUser.class);
        if (null != currentUser) {
            HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
            if (null == request) {
                log.warn("resolve Argument error, it is not  a HttpServletRequest method");
                return null;
            }
            if (null != authenticationManager) {
                log.debug("注解参数获取当前登录用户");
                return authenticationManager.getCurrentUser();
            }
        }
        return null;
    }
}
