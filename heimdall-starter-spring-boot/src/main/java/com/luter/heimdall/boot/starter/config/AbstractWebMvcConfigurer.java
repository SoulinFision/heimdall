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

package com.luter.heimdall.boot.starter.config;

import com.luter.heimdall.boot.starter.interceptor.PermBasedAuthorizeInterceptor;
import com.luter.heimdall.core.manager.AuthorizationManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 默认权限 MVC 默认配置
 * <p>
 * 1、注入方法参数解析器，通过 test(@CurrentUser  SimpleSession user)方式注入当前登录用户信息
 * <p>
 * <p>
 * 2、添加基于 URL 的权限拦截器，Restful 形式授权需要开启此拦截器
 *
 * <p>
 * 如有其他配置需求，继承并且覆盖此类配置即可
 *
 * @author Luter
 */
@Slf4j
public abstract class AbstractWebMvcConfigurer implements WebMvcConfigurer {
    /**
     * 认证管理器
     */
    @Autowired
    private AuthorizationManager authorizationManager;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截处理操作的匹配路径
        //放开静态拦截
        log.warn("初始化 Url 权限拦截器");
        registry.addInterceptor(new PermBasedAuthorizeInterceptor(authorizationManager))
                //拦截所有路径
                .addPathPatterns("/**")
                //排除路径
                //排除静态资源拦截
                .excludePathPatterns(
                        "/login/**",
                        "/logout/**",
                        "/current/**",
                        "/static/**",
                        "/resources/**",
                        "/webjars/**",
                        "/error/**");

    }
}
