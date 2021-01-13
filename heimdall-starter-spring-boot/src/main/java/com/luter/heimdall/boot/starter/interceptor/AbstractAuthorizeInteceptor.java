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
