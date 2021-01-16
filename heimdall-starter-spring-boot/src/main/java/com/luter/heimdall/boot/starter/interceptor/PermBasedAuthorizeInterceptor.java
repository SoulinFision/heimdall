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

package com.luter.heimdall.boot.starter.interceptor;

import com.luter.heimdall.core.manager.AuthorizationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 基于Url的授权拦截器
 * <p>
 * 根据系统配置的授权规则，对请求进行拦截和授权
 * <p>
 * 用法：
 * <p>
 * 在WebMvcConfigurer实现类中配置：
 * <p>
 * <p>
 * //@Override public void addInterceptors(InterceptorRegistry registry){
 * //registry.addInterceptor(new UrlBasedAuthorizationInterceptor(authorizationManager))
 * //.addPathPatterns("/**")
 * //.excludePathPatterns(
 * //"/login/**",
 * //"/logout/**",
 * //"/webjars/**",
 * //"/error/**");
 * <p>
 * }
 *
 * @author Luter
 */
@Slf4j
public class PermBasedAuthorizeInterceptor extends AbstractAuthorizeInteceptor implements HandlerInterceptor {

    /**
     * 授权管理器
     */
    private final AuthorizationManager authorizationManager;

    /**
     * 基于Url的授权拦截器
     * <p>
     * 根据系统配置的授权规则，对请求进行拦截和授权
     *
     * @param authorizationManager the authorization manager
     */
    public PermBasedAuthorizeInterceptor(AuthorizationManager authorizationManager) {
        this.authorizationManager = authorizationManager;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String res = request.getMethod().toUpperCase() + ":" + request.getRequestURI();
        log.info("权限拦截器，请求资源:{}", res);
        // 获取处理method
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        //放行OPTIONS请求，其实没必要，除非数据库里配置了 OPTIONS 类型的授权请求规则
        //就算配置了，也不授权...
        //非要授权，你把这个判断注释掉~~
        if (request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        final Class<?> declaringClass = method.getMethod().getDeclaringClass();
        final String anyAnnotationExist = isAnyAnnotationsExist(method);
        //如果方法上存在注解，则方法注解优先，直接放行交给注解授权验证
        if (null != anyAnnotationExist) {
            log.info("方法:{}#{}() 上存在权限注解 @{} ,优先使用注解授权", declaringClass.getName(), method.getMethod().getName(), anyAnnotationExist);
        } else {
            log.info("方法:{}#{}() 上不存在任何权限注解，执行拦截器授权", declaringClass.getName(), method.getMethod().getName());
            authorizationManager.authorize(request);
        }
        return true;

    }


}
