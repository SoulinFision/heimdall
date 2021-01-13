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

package com.luter.heimdall.core.annotation;

import java.lang.annotation.*;

/**
 * 需要角色才能访问
 *
 * @author luter
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface RequiresRoles {
    /**
     * 需要验证的权限码
     *
     * @return the string [ ]
     */
    String[] value();

    /**
     * 指定验证模式是AND还是OR，默认AND
     *
     * @return 验证模式 mod
     */
    Mod mode() default Mod.ANY;

}
