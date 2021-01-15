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

package com.luter.heimdall.sample.simple.service;

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.authorization.authority.SimpleGrantedAuthority;
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
        final List<SysResourceDTO> resources = DataUtil.getExactUrlResourceList();
        //需要保证顺序
        Map<String, Collection<String>> perms = new LinkedHashMap<>(resources.size());
        for (SysResourceDTO sysResourceDTO : resources) {
            //url +perm 构造拦截器链Map
            perms.put(sysResourceDTO.getUrl(), Collections.singletonList(sysResourceDTO.getPerm()));
        }
        return perms;

    }

    @Override
    public List<? extends GrantedAuthority> loadUserAuthorities(String principal) {
        final List<SysResourceDTO> resources = DataUtil.getExactUrlResourceList();
        return resources.stream().map(d -> new SimpleGrantedAuthority(d.getPerm())).collect(Collectors.toList());
    }
}
