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

package com.luter.heimdall.sample.simple.controller;

import com.luter.heimdall.boot.starter.model.ResponseVO;
import com.luter.heimdall.boot.starter.resolver.CurrentUser;
import com.luter.heimdall.boot.starter.util.ResponseUtils;
import com.luter.heimdall.core.details.UserDetails;
import com.luter.heimdall.core.manager.AuthenticationManager;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.sample.common.dto.SysUserDTO;
import com.luter.heimdall.sample.simple.details.PcUserDetails;
import com.luter.heimdall.sample.simple.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * The type Sys controller.
 *
 * @author Luter
 */
@RestController
@Slf4j
public class SysController {


    /**
     * The Sys user service.
     */
    @Autowired
    private SysUserService sysUserService;

    /**
     * The Authentication manager.
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * Test string.
     *
     * @return the string
     */
    @RequestMapping("/test")
    public String test() {
        return "";
    }

    /**
     * Login response entity.
     *
     * @param username the username
     * @param password the password
     * @return the response entity
     */
    @RequestMapping("/login")
    public ResponseEntity<ResponseVO<String>> login(String username, String password) {
        //实际情况应该从数据库拿用户信息中的密文密码
//        String passFromDb = "从数据库里拿到的密文密码";
//        if (!passwordEncoder.matches(password, passFromDb)) {
//            return ResponseUtils.fail("账号或者密码错误", null);
//        }
        //演示
        if (!"aaaaaa".equals(password)) {
            return ResponseUtils.fail("账号或者密码错误", null);
        }
        final UserDetails userDetailsByUsername = sysUserService.getUserDetailsByUsername(username);
        final SimpleSession simpleSession = authenticationManager.login(userDetailsByUsername);
        return ResponseUtils.ok("success", simpleSession.getId());
    }

    /**
     * Logout response entity.
     *
     * @return the response entity
     */
    @RequestMapping("/logout")
    public ResponseEntity<ResponseVO<Void>> logout() {
        authenticationManager.logout();
        return ResponseUtils.ok();
    }

    /**
     * Current response entity.
     *
     * @return the response entity
     */
    @RequestMapping("/current")
    public ResponseEntity<ResponseVO<SimpleSession>> current() {
        return ResponseUtils.ok(authenticationManager.getCurrentUser());
    }

    /**
     * Current response entity.
     *
     * @param user the user
     * @return the response entity
     */
    @RequestMapping("/current/anno")
    public ResponseEntity<ResponseVO<SysUserDTO>> currentAnno(@CurrentUser SimpleSession user) {
        final PcUserDetails details = (PcUserDetails) user.getDetails();
        return ResponseUtils.ok(details.getUser());
    }

    /**
     * Current response entity.
     *
     * @return the response entity
     */
    @RequestMapping("/online")
    public ResponseEntity<ResponseVO<Collection<SimpleSession>>> online() {
        final Collection<SimpleSession> activeSessions = authenticationManager.getActiveSessions();
        return ResponseUtils.ok(activeSessions);
    }

    /**
     * Current response entity.
     *
     * @param sessionId the session id
     * @return the response entity
     */
    @RequestMapping("/kickout/session/{sessionId}")
    public ResponseEntity<ResponseVO<Void>> kickOutSession(@PathVariable String sessionId) {
        authenticationManager.kickOutSession(sessionId);
        return ResponseUtils.ok();
    }

    /**
     * Current response entity.
     *
     * @param principal the principal
     * @return the response entity
     */
    @RequestMapping("/kickout/principal/{principal}")
    public ResponseEntity<ResponseVO<Void>> kickOutPrincipal(@PathVariable String principal) {
        authenticationManager.kickOutPrincipal(principal);
        return ResponseUtils.ok();
    }
}
