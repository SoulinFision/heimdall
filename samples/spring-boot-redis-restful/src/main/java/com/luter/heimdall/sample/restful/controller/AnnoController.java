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

package com.luter.heimdall.sample.restful.controller;

import com.luter.heimdall.core.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * The type Anno controller.
 *
 * @author Luter
 */
@RestController
@Slf4j
@RequestMapping("/anno")
public class AnnoController {


    /**
     * Requires user string.
     *
     * @return the string
     */
    @GetMapping("/user")
    @RequiresUser
    public String requiresUser() {
        return "RequiresUser";
    }

    /**
     * Require role string.
     *
     * @return the string
     */
    @GetMapping("/role")
    @RequiresRole("admin")
    public String requireRole() {
        return "RequiresRole(admin)";
    }

    /**
     * Require roles all string.
     *
     * @return the string
     */
    @GetMapping("/roles/all")
    @RequiresRoles(value = {"admin", "guest"}, mode = Mod.ALL)
    public String requireRolesAll() {
        return "RequiresRoles(admin,guest) All";
    }

    /**
     * Require roles any string.
     *
     * @return the string
     */
    @GetMapping("/roles/any")
    @RequiresRoles(value = {"admin", "guest"}, mode = Mod.ANY)
    public String requireRolesAny() {
        return "RequiresRoles(admin,guest) Any";
    }

    /**
     * Require permission string.
     *
     * @return the string
     */
    @GetMapping("/perm")
    @RequiresPermission("userSave")
    public String requirePermission() {
        return "RequiresPermission(userSave)";
    }


    /**
     * Require permissions all string.
     *
     * @return the string
     */
    @GetMapping("/perms/all")
    @RequiresPermissions(value = {"userSave", "userList"}, mode = Mod.ALL)
    public String requirePermissionsAll() {
        return "RequiresPermissions(userSave,userList) ALL";
    }

    /**
     * Require permissions any string.
     *
     * @return the string
     */
    @GetMapping("/perms/any")
    @RequiresPermissions(value = {"userSave", "userList"}, mode = Mod.ANY)
    public String requirePermissionsAny() {
        return "RequiresPermissions(userSave,userList) ANY";
    }

}
