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

import java.util.Collection;
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
    String SYS_AUTHORITIES_CACHED_KEY = "heimdal:sysAuthorities";


    /**
     * 获取系统权限
     *
     * @return the authorities
     */
    Map<String, Collection<String>> getSysAuthorities();

    /**
     * 清理缓存
     */
    void clearSysAuthorities();

    /**
     * 设置系统权限
     *
     * @param authorities the authorities
     */
    void setSysAuthorities(Map<String, Collection<String>> authorities);


    /**
     * 重新载入权限到缓存
     *
     * @param authorities the authorities
     */
    default void resetCachedSysAuthorities(Map<String, Collection<String>> authorities) {
        System.err.println("======AuthorizationMetaDataCacheDao.resetCachedSysAuthorities======");
        System.err.println("======暂未实现======");
    }
}
