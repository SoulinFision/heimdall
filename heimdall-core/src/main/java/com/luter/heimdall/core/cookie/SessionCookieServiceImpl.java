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

package com.luter.heimdall.core.cookie;

import com.luter.heimdall.core.config.Config;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.exception.CookieException;
import com.luter.heimdall.core.exception.HeimdallException;
import com.luter.heimdall.core.servlet.ServletHolder;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;

/**
 * 默认Cookie实现，如果有特殊需求，可自己重新实现
 *
 * @author Luter
 */
@Slf4j
public class SessionCookieServiceImpl implements CookieService {

    /**
     * The Servlet holder.
     */
    private final ServletHolder servletHolder;

    /**
     * Cookie 生成服务
     *
     * @param servletHolder servlet 请求持有实现
     */
    public SessionCookieServiceImpl(ServletHolder servletHolder) {
        if (null == servletHolder) {
            throw new HeimdallException(" SessionCookieServiceImpl Need ServletHolder ");
        }
        this.servletHolder = servletHolder;
    }

    @Override
    public void addCookie(String value) {
        final Config config = ConfigManager.getConfig();
        if (StrUtils.isBlank(config.getCookie().getName())) {
            throw new CookieException("Wrong Config  of Cookie Name ");
        }
        if (StrUtils.isBlank(value)) {
            throw new CookieException("Cookie Value must not be null");
        }
        Cookie cookie = new Cookie(config.getCookie().getName(), value);
        String path = config.getCookie().getPath();
        if (StrUtils.isBlank(path)) {
            path = "/";
        }
        cookie.setPath(path);
        cookie.setMaxAge(config.getCookie().getMaxAge());
        cookie.setComment(config.getCookie().getComment());
        cookie.setHttpOnly(config.getCookie().getHttpOnly());
        cookie.setSecure(config.getCookie().getSecure());
        cookie.setVersion(config.getCookie().getVersion());
        if (StrUtils.isNotBlank(config.getCookie().getDomain())) {
            cookie.setDomain(config.getCookie().getDomain());
        }
        servletHolder.getResponse().addCookie(cookie);
    }

    @Override
    public Cookie getCookie() {
        String name = ConfigManager.getConfig().getCookie().getName();
        if (StrUtils.isBlank(name)) {
            throw new CookieException("Wrong Config of Cookie Name ");
        }
        Cookie[] cookies = servletHolder.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && name.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }


    @Override
    public void delCookie() {
        String name = ConfigManager.getConfig().getCookie().getName();
        if (StrUtils.isBlank(name)) {
            throw new CookieException("Wrong Config of Cookie Name ");
        }
        Cookie[] cookies = servletHolder.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && (name).equals(cookie.getName())) {
                    Cookie deleted = new Cookie(name, null);
                    deleted.setPath(null);
                    deleted.setMaxAge(0);
                    servletHolder.getResponse().addCookie(deleted);
                    return;
                }
            }
        }
    }

    @Override
    public void updateCookie(String value) {
        String name = ConfigManager.getConfig().getCookie().getName();
        if (StrUtils.isBlank(name)) {
            throw new CookieException("Wrong Config of Cookie Name ");
        }
        Cookie[] cookies = servletHolder.getRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie != null && (name).equals(cookie.getName())) {
                    addCookie(value);
                    return;
                }
            }
        }
    }


}
