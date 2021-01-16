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

import com.luter.heimdall.core.details.UserDetails;
import com.luter.heimdall.core.exception.AccountException;
import com.luter.heimdall.sample.common.dto.SysUserDTO;
import com.luter.heimdall.sample.common.util.DataUtil;
import com.luter.heimdall.sample.restful.details.AppUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The type Sys user service.
 *
 * @author Luter
 */
@Slf4j
@Service
public class SysUserService {

    /**
     * 加载认证用户详情
     * <p>
     * <p>
     * 用户详情由经过合法校验后的用户信息构成，应该是一个经过校验后合法的用户信息载体
     * <p>
     * <p>
     * 图形验证码、密码校验等工作，应该在构造UserDetails之前完成。
     * <p>
     * UserDetails被提交给认证授权管理器后，即进行Session创建和管理
     *
     * @param username the username
     * @return the user details
     */
    public UserDetails getUserDetailsByUsername(String username) {
        List<SysUserDTO> findUserList = DataUtil.getSimpleUserList().stream().filter(item -> item.getUsername().equals(username)).collect(Collectors.toList());
        if (findUserList.isEmpty()) {
            throw new AccountException("用户名密码错误");
        }
        return new AppUserDetails(findUserList.get(0));
    }
}
