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

package com.luter.heimdall.boot.starter.interceptor;

import com.luter.heimdall.core.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;

/**
 * 权限拦截器抽象类
 *
 * @author Luter
 */
@Slf4j
public abstract class AbstractAuthorizeInteceptor {
    /**
     * 判断拦截到从控制器方法上是否存在任何授权注解，如果存在，注解优先
     *
     * @param method the method
     * @return the string
     */
    public String isAnyAnnotationsExist(HandlerMethod method) {
        if (method.hasMethodAnnotation(RequiresUser.class)) {
            return RequiresRole.class.getSimpleName();
        } else if (method.hasMethodAnnotation(RequiresRole.class)) {
            return RequiresRole.class.getSimpleName();
        } else if (method.hasMethodAnnotation(RequiresRoles.class)) {
            return RequiresRoles.class.getSimpleName();
        } else if (method.hasMethodAnnotation(RequiresPermission.class)) {
            return RequiresPermission.class.getSimpleName();
        } else if (method.hasMethodAnnotation(RequiresPermissions.class)) {
            return RequiresPermissions.class.getSimpleName();
        }
        return null;
    }
}
