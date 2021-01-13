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
import com.luter.heimdall.boot.starter.util.ResponseUtils;
import com.luter.heimdall.sample.simple.service.AuthorizationDemoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/auth")
public class AuthorizationDemoController {
    @Autowired
    AuthorizationDemoService authorizationDemoService;


    @GetMapping("/is")
    public ResponseEntity<ResponseVO<Void>> is() {
        if (!authorizationDemoService.isAuthenticated()) {
            return ResponseUtils.unauthenticated();
        }
        return ResponseUtils.ok("你已经登录了");
    }


    @GetMapping("/role/{name}")
    public ResponseEntity<ResponseVO<Void>> role(@PathVariable String name) {
        if (!authorizationDemoService.hasRole(name)) {
            return ResponseUtils.fail(HttpStatus.FORBIDDEN, "你不具备角色:" + name);
        }
        return ResponseUtils.ok("你具备:" + name);
    }


    @GetMapping("/roles/any")
    public ResponseEntity<ResponseVO<Void>> hasAnyRoles() {
        if (!authorizationDemoService.hasAnyRoles("admin", "guest")) {
            return ResponseUtils.fail(HttpStatus.FORBIDDEN, "你不具备多个角色[admin , guest]之一");
        }
        return ResponseUtils.ok("你具备角色:[admin , guest]之一");
    }

    @GetMapping("/roles/all")
    public ResponseEntity<ResponseVO<Void>> hasAllRoles() {
        if (!authorizationDemoService.hasAllRoles("admin", "guest")) {
            return ResponseUtils.fail(HttpStatus.FORBIDDEN, "你不具备多个角色 [admin , guest]");
        }
        return ResponseUtils.ok("你具备角色:[admin , guest] 之一");
    }

    @GetMapping("/perm/{name}")
    public ResponseEntity<ResponseVO<Void>> hasPermission(@PathVariable String name) {
        if (!authorizationDemoService.hasPermission(name)) {
            return ResponseUtils.fail(HttpStatus.FORBIDDEN, "你不具备权限:" + name);
        }
        return ResponseUtils.ok("你具备权限:" + name);
    }

    @GetMapping("/perms/any")
    public ResponseEntity<ResponseVO<Void>> hasAnyPermissions() {
        if (!authorizationDemoService.hasAnyPermissions("catSave", "catUpdate")) {
            return ResponseUtils.fail(HttpStatus.FORBIDDEN, "你不具备多个权限[catSave,catUpdate]之一");
        }
        return ResponseUtils.ok("你具备权限:[catSave , catUpdate] 之一");
    }

    @GetMapping("/perms/all")
    public ResponseEntity<ResponseVO<Void>> hasAllPermissions() {
        if (!authorizationDemoService.hasAllPermissions("catSave", "catUpdate")) {
            return ResponseUtils.fail(HttpStatus.FORBIDDEN, "你不具备多个权限[catSave,catUpdate]");
        }
        return ResponseUtils.ok("你具备权限:[catSave,catUpdate]");
    }
}
