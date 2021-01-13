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

package com.luter.heimdall.boot.starter.servlet;

import com.luter.heimdall.core.exception.HeimdallException;
import com.luter.heimdall.core.servlet.ServletHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet 持有实现
 *
 * @author Luter
 */
public class ServletHolderImpl implements ServletHolder {
    /**
     * 获取当前会话的 request
     *
     * @return request
     */
    @Override
    public HttpServletRequest getRequest() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            throw new HeimdallException(" 获取 ServletRequestAttributes 失败,请确保当前环境支持Java Web");
        }
        return servletRequestAttributes.getRequest();
    }

    /**
     * 获取当前会话的 response
     *
     * @return response
     */
    @Override
    public HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes == null) {
            throw new HeimdallException(" 获取 ServletRequestAttributes 失败,请确保当前环境支持Java Web");
        }
        return servletRequestAttributes.getResponse();
    }
}
