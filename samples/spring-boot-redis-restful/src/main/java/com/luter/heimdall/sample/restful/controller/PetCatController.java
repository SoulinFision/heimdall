///*
// *    Copyright 2020-2021 Luter.me
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.luter.heimdall.sample.restful.controller;
//
//import com.luter.heimdall.core.annotation.Mod;
//import com.luter.heimdall.core.annotation.RequiresRoles;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.web.bind.annotation.*;
//
///**
// * The type Pet cat controller.
// *
// * @author Luter
// */
//@RestController
//@Slf4j
//@RequestMapping("/pet/cat")
//public class PetCatController {
//    /**
//     * Save string.
//     *
//     * @return the string
//     */
//    @PostMapping
//    public String save() {
//        return "save";
//    }
//
//    /**
//     * Delete string.
//     *
//     * @param id the id
//     * @return the string
//     */
//    @DeleteMapping("/{id}")
//    public String delete(@PathVariable Long id) {
//        return "delete:" + id;
//    }
//
//    /**
//     * Update string.
//     *
//     * @return the string
//     */
//    @PutMapping
//    public String update() {
//        return "update:";
//    }
//
//    /**
//     * Detail string.
//     *
//     * @param id the id
//     * @return the string
//     */
//    @GetMapping("/{id}")
//    public String detail(@PathVariable Long id) {
//        return "detail:" + id;
//    }
//
//    /**
//     * List string.
//     *
//     * @return the string
//     */
//    @GetMapping
//    @RequiresRoles(value = {"GET:/pet/cat", "POST:/pet/save"}, mode = Mod.ALL)
//    public String list() {
//        return "list";
//    }
//
//}
