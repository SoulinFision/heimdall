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

package com.luter.heimdall.sample.restful.service;

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.authorization.authority.MethodAndUrlGrantedAuthority;
import com.luter.heimdall.core.authorization.service.AuthorizationMetaDataService;
import com.luter.heimdall.sample.common.dto.SysResourceDTO;
import com.luter.heimdall.sample.common.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 系统权限数据提供服务
 *
 * @author Luter
 */
@Service
@Slf4j
public class AuthorizationMetaDataServiceImpl implements AuthorizationMetaDataService {
    @Override
    public Map<String, Collection<String>> loadSysAuthorities() {
        final List<SysResourceDTO> resources = DataUtil.getRestfulResourceList();
        Map<String, Collection<String>> perms = new LinkedHashMap<>(resources.size());
        for (SysResourceDTO sysResourceDTO : resources) {
            //这个 url 需要哪些权限或者角色，匹配其一就可以
            perms.put(sysResourceDTO.getUrl(), Collections.singletonList(sysResourceDTO.getPerm()));
        }
        return perms;
    }

    @Override
    public List<? extends GrantedAuthority> loadUserAuthorities() {
        final List<SysResourceDTO> resources = DataUtil.getRestfulResourceList();
        List<SysResourceDTO> adminRes = new ArrayList<>();
        //用户权限
        //        admin
        adminRes.add(resources.get(0));
        adminRes.add(resources.get(1));
        return adminRes.stream().map(d -> new MethodAndUrlGrantedAuthority(d.getMethod(), d.getUrl()))
                .collect(Collectors.toList());
    }
}
