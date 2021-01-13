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

package com.luter.heimdall.core.authorization.aspect;

import com.luter.heimdall.core.annotation.*;
import com.luter.heimdall.core.authorization.handler.AuthorizationFilterHandler;
import com.luter.heimdall.core.exception.UnAuthorizedException;
import com.luter.heimdall.core.exception.UnAuthticatedException;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Arrays;

/**
 * 权限注解拦截器
 * <p>
 * 1、首先匹配方法注解，进入权限验证环节
 * <p>
 * 2、拿到注解上需要的角色或者权限标识符
 * <p>
 * 3、拿到用户自身信息中具备的所有角色或者权限标识符
 * <p>
 * 4、遍历所有用户角色或者权限，如果角色或者权限标识匹配，则授权通过，否则不通过
 *
 * @author Luter
 */
@Aspect
@Slf4j
public class AuthorizationAnnotationAspect {
    /**
     * The constant POINT_CUP_EXPRESSION.
     */
    private static final String POINT_CUP_EXPRESSION =
            "@within(com.luter.heimdall.core.annotation.RequiresUser)||@annotation(com.luter.heimdall.core.annotation.RequiresUser)" +
                    "||@within(com.luter.heimdall.core.annotation.RequiresRole)||@annotation(com.luter.heimdall.core.annotation.RequiresRole)" +
                    "||@within(com.luter.heimdall.core.annotation.RequiresRoles)||@annotation(com.luter.heimdall.core.annotation.RequiresRoles)" +
                    "||@within(com.luter.heimdall.core.annotation.RequiresPermission)||@annotation(com.luter.heimdall.core.annotation.RequiresPermission)" +
                    "||@within(com.luter.heimdall.core.annotation.RequiresPermissions)||@annotation(com.luter.heimdall.core.annotation.RequiresPermissions)";

    /**
     * The Permission hanlder.
     */
    private final AuthorizationFilterHandler authorizationFilterHandler;

    /**
     * Instantiates a new Security aspect.
     *
     * @param permissionHanlder the permission hanlder
     */
    public AuthorizationAnnotationAspect(AuthorizationFilterHandler permissionHanlder) {
        this.authorizationFilterHandler = permissionHanlder;
    }

    /**
     * Point cut.
     */
    @Pointcut(POINT_CUP_EXPRESSION)
    public void pointCut() {
    }

    /**
     * On before.
     *
     * @param joinPoint the join point
     */
    @Before("pointCut()")
    public void onBefore(JoinPoint joinPoint) {
        handleRequiresUser(joinPoint);
        handleRequiresRole(joinPoint);
        handleRequiresRoles(joinPoint);
        handleRequiresPermission(joinPoint);
        handleRequiresPermissions(joinPoint);
    }


    /**
     * Handle requires user.
     *
     * @param joinPoint the join point
     */
    public void handleRequiresUser(JoinPoint joinPoint) {
        log.debug("注解权限拦截= 开始处理登录注解:RequiresUser");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getMethod().getName();
        RequiresUser annotation = signature.getMethod().getAnnotation(RequiresUser.class);
        if (null == annotation) {
            // 验证类上的
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequiresUser.class);
            if (null != annotation) {
                log.debug("注解权限拦截= 从类: {} 上找到注解:RequiresUser", className);
            }

        } else {
            log.debug("注解权限拦截= 从方法:{}上找到注解:RequiresUser", className + "." + methodName + "()");
        }
        if (annotation != null) {
            if (!authorizationFilterHandler.isAuthenticated()) {
                throw new UnAuthticatedException();
            }
            log.debug("注解权限拦截= RequiresUser: 通过");
        }
    }

    /**
     * Handle require role.
     *
     * @param joinPoint the join point
     */
    public void handleRequiresRole(JoinPoint joinPoint) {
        log.debug("注解权限拦截= 开始处理登录注解:RequiresRole");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getMethod().getName();
        RequiresRole annotation = signature.getMethod().getAnnotation(RequiresRole.class);
        if (null == annotation) {
            // 验证类上的
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequiresRole.class);
            if (null != annotation) {
                log.debug("注解权限拦截= 从类: {} 上找到注解:RequiresRole", className);
            }

        } else {
            log.debug("注解权限拦截= 从方法:{}上找到注解:RequiresRole", className + "." + methodName + "()");
        }
        if (annotation != null) {
            String roleName = annotation.value();
            log.debug("注解权限拦截= 单个角色权限验证: role: {}", roleName);
            if (StrUtils.isNotBlank(roleName)) {
                if (!authorizationFilterHandler.hasRole(roleName)) {
                    throw new UnAuthorizedException("You do not have the permission . Role:" + roleName + ". Access denied.");
                }
                log.debug("注解权限拦截= 单个角色权限验证通过: role: {}", roleName);
            }
        }
    }

    /**
     * Handle require roles.
     *
     * @param joinPoint the join point
     */
    public void handleRequiresRoles(JoinPoint joinPoint) {
        log.debug("注解权限拦截= 开始处理登录注解:RequiresRoles");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getMethod().getName();
        RequiresRoles annotation = signature.getMethod().getAnnotation(RequiresRoles.class);
        if (null == annotation) {
            // 验证类上的
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequiresRoles.class);
            if (null != annotation) {
                log.debug("注解权限拦截= 从类: {} 上找到注解:RequiresRoles", className);
            }

        } else {
            log.debug("注解权限拦截= 从方法:{}上找到注解:RequiresRoles", className + "." + methodName + "()");
        }
        if (annotation != null) {
            String[] roles = annotation.value();
            Mod mode = annotation.mode();
            log.debug("注解权限拦截= 多角色权限验证: roles: {},Mod:{} ", roles, mode);
            if (roles.length > 0) {
                if (Mod.ALL.equals(mode)) {
                    if (!authorizationFilterHandler.hasAllRoles(roles)) {
                        throw new UnAuthorizedException("You do not have the permission. All Roles:" + Arrays.toString(roles) + ". Access denied.");
                    }
                } else {
                    if (!authorizationFilterHandler.hasAnyRoles(roles)) {
                        throw new UnAuthorizedException("You do not have the permission. Any Roles:" + Arrays.toString(roles) + ". Access denied.");
                    }
                }
            }
        }
    }


    /**
     * Handle requires permission.
     *
     * @param joinPoint the join point
     */
    public void handleRequiresPermission(JoinPoint joinPoint) {
        log.debug("注解权限拦截= 开始处理登录注解:RequiresPermission");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getMethod().getName();
        RequiresPermission annotation = signature.getMethod().getAnnotation(RequiresPermission.class);
        if (null == annotation) {
            // 验证类上的
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequiresPermission.class);
            if (null != annotation) {
                log.debug("注解权限拦截= 从类: {} 上找到注解:RequiresPermission", className);
            }

        } else {
            log.debug("注解权限拦截= 从方法:{}上找到注解:RequiresPermission", className + "." + methodName + "()");
        }
        if (annotation != null) {
            String perm = annotation.value();
            log.debug("注解权限拦截= 单个 权限标志 权限验证,需要权限: Permission: [{}]", perm);
            if (StrUtils.isNotBlank(perm)) {
                if (!authorizationFilterHandler.hasPermission(perm)) {
                    throw new UnAuthorizedException("You do not have the permission : " + perm + ". Access denied.");
                } else {
                    log.debug("注解权限拦截= 单个 权限标志 权限验证,具备权限: Permission: [{}]", perm);
                }
            }
        }
    }

    /**
     * Handle requires permissions.
     *
     * @param joinPoint the join point
     */
    public void handleRequiresPermissions(JoinPoint joinPoint) {
        log.debug("注解权限拦截= 开始处理登录注解:RequiresPermissions");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getMethod().getName();
        RequiresPermissions annotation = signature.getMethod().getAnnotation(RequiresPermissions.class);
        if (null == annotation) {
            // 验证类上的
            annotation = joinPoint.getTarget().getClass().getAnnotation(RequiresPermissions.class);
            if (null != annotation) {
                log.debug("注解权限拦截= 从类: {} 上找到注解:RequiresPermissions", className);
            }

        } else {
            log.debug("注解权限拦截= 从方法:{}上找到注解:RequiresPermissions", className + "." + methodName + "()");
        }
        if (annotation != null) {
            String[] permissions = annotation.value();
            Mod mode = annotation.mode();
            log.debug("注解权限拦截= 多 权限标志 权限验证: Permissions: [{}],Mod:[{}] ", permissions, mode);
            if (permissions.length > 0) {
                if (Mod.ALL.equals(mode)) {
                    if (!authorizationFilterHandler.hasAllPermissions(permissions)) {
                        throw new UnAuthorizedException("You do not have all of the permissions  :" + Arrays.toString(permissions) + ". Access denied.");
                    } else {
                        log.debug("注解权限拦截= 多 权限标志 权限验证,具备权限: Permissions: [{}],Mod:[{}]", permissions, mode);
                    }
                } else {
                    if (!authorizationFilterHandler.hasAnyPermissions(permissions)) {
                        throw new UnAuthorizedException("You do not have any of the permission:" + Arrays.toString(permissions) + ". Access denied.");
                    } else {
                        log.debug("注解权限拦截= 多 权限标志 权限验证,具备权限: Permissions: [{}],Mod:[{}]", permissions, mode);
                    }
                }
            }
        }
    }

}
