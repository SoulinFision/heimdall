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

package com.luter.heimdall.core.servlet;

import com.luter.heimdall.core.exception.HeimdallException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * servlet 获取接口
 * <p>
 * 系统中如下两个地方用到了servlet
 * 1、SessionDao中，用以从request请求中获取访问者的IP地址
 * <p>
 * 2、CookieService中用作Cookies读写
 *
 * @author luter
 */
public interface ServletHolder {

    /**
     * 获取当前请求的 Request 对象
     *
     * @return 当前请求的Request对象 request
     */
    default HttpServletRequest getRequest() {
        throw new HeimdallException("请实现:ServletHolder接口");
    }

    /**
     * 获取当前请求的 Response 对象
     *
     * @return 当前请求的response对象 response
     */
    default HttpServletResponse getResponse() {
        throw new HeimdallException("请实现:ServletHolder接口");
    }

}
