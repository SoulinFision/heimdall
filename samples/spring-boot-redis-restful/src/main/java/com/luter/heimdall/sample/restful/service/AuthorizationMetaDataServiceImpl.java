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

package com.luter.heimdall.sample.restful.service;

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.authorization.service.AuthorizationMetaDataService;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.sample.common.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 系统权限数据提供服务
 * <p>
 * <p>
 * 在 restful 模式下
 *
 * @author Luter
 */
@Service
@Slf4j
public class AuthorizationMetaDataServiceImpl implements AuthorizationMetaDataService {
    @Override
    public Map<String, Collection<String>> loadSysAuthorities() {
        ////////////////////////////这里的授权方式要与 UserDetails 中的匹配
        //restful url 授权
        return DataUtil.getRestfulPerm();
        //精确路由 url 授权
//        return DataUtil.getExactUrlPerms();
        //角色授权
//        return DataUtil.getRolePerms();
        ////////////////////////////这里的授权方式要与 UserDetails 中的匹配
    }

    @Override
    public List<? extends GrantedAuthority> loadUserAuthorities(SimpleSession session) {
        log.warn("加载用户 pricipal = [{}] 的权限 ", session.getDetails().getPrincipal());
        ////////////////////////////这里的授权方式要与 UserDetails 中的匹配
        ///restful url 授权
        return DataUtil.getUserRestfulPerm(session.getDetails().getPrincipal());
        //角色授权
//        return DataUtil.getUserRolePerms(principal);

        //精确匹配路由 url 授权
//        return DataUtil.getExactUserPerms(principal);
        ////////////////////////////这里的授权方式要与 UserDetails 中的匹配
    }
}
