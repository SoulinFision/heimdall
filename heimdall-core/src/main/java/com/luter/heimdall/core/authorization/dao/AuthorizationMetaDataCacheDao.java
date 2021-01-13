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

package com.luter.heimdall.core.authorization.dao;

import java.util.Map;

/**
 * 系统权限缓存DAO接口
 *
 * @author luter
 */
public interface AuthorizationMetaDataCacheDao {


    /**
     * 默认系统权限缓存 Key
     */
    String AUTHORITIES_CACHED_KEY = "heimdal:authorities";


    /**
     * 获取系统权限
     *
     * @return the authorities
     */
    Map<String, String> getSysAuthorities();

    /**
     * 设置系统权限
     *
     * @param authorities the authorities
     */
    void setSysAuthorities(Map<String, String> authorities);

    /**
     * 添加一个权限
     *
     * @param key   the key
     * @param value the value
     */
    default void addSysAuthority(String key, String value) {
        System.err.println("======AuthorizationMetaDataCacheDao.addSysAuthority======");
        System.err.println("======暂未实现======");
    }

    /**
     * 重新载入权限到缓存
     *
     * @param authorities the authorities
     */
    default void resetCachedSysAuthorities(Map<String, String> authorities) {
        System.err.println("======AuthorizationMetaDataCacheDao.resetCachedSysAuthorities======");
        System.err.println("======暂未实现======");
    }
}
