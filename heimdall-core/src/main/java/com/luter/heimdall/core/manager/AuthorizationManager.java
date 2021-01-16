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

package com.luter.heimdall.core.manager;

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.authorization.authority.MethodAndUrlGrantedAuthority;
import com.luter.heimdall.core.authorization.authority.SimpleGrantedAuthority;
import com.luter.heimdall.core.authorization.dao.AuthorizationMetaDataCacheDao;
import com.luter.heimdall.core.authorization.service.AuthorizationMetaDataService;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.exception.UnAuthorizedException;
import com.luter.heimdall.core.exception.UnAuthticatedException;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.utils.PathUtil;
import com.luter.heimdall.core.utils.StrUtils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 授权管理器
 *
 * @author Luter
 */
@Slf4j
public class AuthorizationManager {

    /**
     * 拥有所有method的权限
     */
    public static final String ALL_METHOD_NAME = "ALL";

    /**
     * 系统权限源数据提供服务
     */
    private final AuthorizationMetaDataService authorizationMetaDataService;
    /**
     * 权限信息缓存Dao
     */
    private final AuthorizationMetaDataCacheDao authorizationDao;
    /**
     * 认证管理器
     */
    private final AuthenticationManager authenticationManager;

    /**
     * 授权管理器
     *
     * @param authorizationMetaDataService 系统权限规则数据提供服务
     * @param authorizationDao             系统权限规则缓存Dao
     * @param authenticationManager        认证管理器，用来获取当前登录用户
     */
    public AuthorizationManager(AuthorizationMetaDataService authorizationMetaDataService, AuthorizationMetaDataCacheDao authorizationDao, AuthenticationManager authenticationManager) {
        this.authorizationMetaDataService = authorizationMetaDataService;
        this.authorizationDao = authorizationDao;
        this.authenticationManager = authenticationManager;

    }

    /**
     * 授权,不通过抛出 UnAuthorizedException 异常
     * <p>
     * 1、获取系统权限拦截规则
     * <p>
     * 2、获取request的请求Url，通过AntPathMatcher与系统权限拦截规则中的Url进行匹配，匹配上，进入授权，不匹配则放行
     * <p>
     * 3、获取到当前登录用户具有的权限，遍历授权
     * <p>
     * 4、判断用户权限类型，如果是：MethodAndUrlGrantedAuthority进入restful授权，如果是SimpleGrantedAuthority，进入普通精确Url授权
     * <p>
     * 4.1 MethodAndUrlGrantedAuthority 授权，遍历用户权限，确定具备此url的权限，然后拿到method，如果method也匹配或者为ALL则授权通过。
     * <p>
     * 4.2 SimpleGrantedAuthority授权，遍历用户权限，查找与所需perm匹配的用户权限，如果有，授权通过
     *
     * @param request the request
     */
    public void authorize(HttpServletRequest request) {
        isAuthorized(request, true);
    }

    /**
     * 授权
     * <p>
     * 1、获取系统权限拦截规则
     * <p>
     * 2、获取request的请求Url，通过AntPathMatcher与系统权限拦截规则中的Url进行匹配，匹配上，进入授权，不匹配则放行
     * <p>
     * 3、获取到当前登录用户具有的权限，遍历授权
     * <p>
     * 4、判断用户权限类型，如果是：MethodAndUrlGrantedAuthority进入restful授权，如果是SimpleGrantedAuthority，进入普通精确Url授权
     * <p>
     * 4.1 MethodAndUrlGrantedAuthority 授权，遍历用户权限，确定具备此url的权限，然后拿到method，如果 Method 与当前request 的 method 一致
     * <p>
     * 或者 method ="all",授权通过.
     * <p>
     * 4.2 SimpleGrantedAuthority授权，遍历用户权限，查找与所需perm匹配的用户权限，如果有，授权通过
     *
     * @param request   the request
     * @param isThrowEx 如果授权不通过，是否抛出异常
     * @return the boolean
     */
    public boolean isAuthorized(HttpServletRequest request, boolean isThrowEx) {
        String url = request.getRequestURI(), method = request.getMethod();
        //url不空
        if (StrUtils.isNotBlank(url) && StrUtils.isNotBlank(method)) {
            Map<String, Collection<String>> authorities = getSysAuthorities();
            //系统权限拦截规则不为空，开始授权
            if (null != authorities && !authorities.isEmpty()) {
                log.debug("系统权限 : {}", authorities);
                PathUtil antPathMatcherUtil = new PathUtil();
                //遍历判断
                for (Map.Entry<String, Collection<String>> entry : authorities.entrySet()) {
                    //要拦截的url
                    final String filterUrl = entry.getKey();
                    //需要的perm授权标识，MethodAndUrlGrantedAuthority模式下无效
                    final Collection<String> filterPerm = entry.getValue();
                    //当前请求url是否需要被拦截
                    final boolean match = antPathMatcherUtil.match(filterUrl, url);
                    log.debug("授权= 请求资源:[{}:{}],拦截url:[{}], {}", method, url, filterUrl, match ? "规则匹配,开始授权" : "规则不匹配,忽略");
                    //这个Url需要授权拦截
                    if (match) {
                        log.info("授权= 请求资源:[{}:{}],拦截url:[{}], {}", method, url, filterUrl, "规则匹配,开始授权");
                        //拿到用户自身的权限
                        final List<? extends GrantedAuthority> userAuthorities = getUserAuthorities();
                        //遍历用户具有的系统权限
                        for (GrantedAuthority userAuthority : userAuthorities) {
                            //如果是对MethodAndUrlGrantedAuthority类型权限，则通过 method 和 url 进行授权
                            if (userAuthority instanceof MethodAndUrlGrantedAuthority) {
                                MethodAndUrlGrantedAuthority mga = (MethodAndUrlGrantedAuthority) userAuthority;
                                //当前访问的url与当前用户权限里的的url匹配
                                if (antPathMatcherUtil.match(mga.getUrl(), url)) {
                                    //再看看方法是否匹配
                                    //如果请求method也匹配或者是ALL，则放行。(不区分大小写)
                                    if (method.equalsIgnoreCase(mga.getMethod()) || ALL_METHOD_NAME.equalsIgnoreCase(mga.getMethod())) {
                                        log.info("Restful授权=  授权通过. 请求资源: [{}:{}],用户权限:[{}]", method, url, mga);
                                        return true;
                                    }
                                }
                                log.debug("Restful授权= 权限不匹配,请求资源: [{}:{}],用户权限:[{}]", method, url, mga);
                            }
                            //普通的精确url权限类型: SimpleGrantedAuthority，则对Perm标识符进行授权
                            else if (userAuthority instanceof SimpleGrantedAuthority) {
                                SimpleGrantedAuthority mga = (SimpleGrantedAuthority) userAuthority;
                                //如果系统权限需要的Perm标识与用户具有的某个权限标识匹配，则授权通过(不区分大小写)
                                if (filterPerm.contains(mga.getAuthority())) {
                                    log.info("普通权限标识符授权= 权限匹配成功. 请求资源: [{}:{}],需要权限标识:[{}],匹配到权限:[{}]", method, url, filterPerm, mga);
                                    return true;
                                }
                                log.debug("普通权限标识符授权=权限不匹配. 请求资源: [{}:{}],需要权限标识:[{}],匹配到权限:[{}]", method, url, filterPerm, mga);
                            } else {
                                //传进来的不是GrantedAuthority的实现类，没法处理，就当不需要权限，默认通过
                                log.error("授权= 权限类型不支持,不是GrantedAuthority的实现类. 默认授权通过");
                                return true;
                            }
                        }
                        log.warn("授权=  请求资源:[{}:{}],需要权限:[{}], 用户所有权限:[{}] ,授权不通过", method, url, filterPerm, userAuthorities);
                        if (isThrowEx) {
                            throw new UnAuthorizedException("The current user is not permitted to access resource [" +
                                    method + StrUtils.COLON + url + "] , Access denied.");
                        } else {
                            return false;
                        }
                    }
                }
            }

        }
        //不需要的拦截的url，全部放行
        return true;
    }

    /**
     * 获取用户的权限
     * <p>
     * 如果没登录，UnAuthticatedException
     * 如果权限为空，默认未授权：UnAuthorizedException
     *
     * @return the user authorities
     */
    public List<? extends GrantedAuthority> getUserAuthorities() {
        final SimpleSession currentUser = authenticationManager.getCurrentUser();
        //没登录
        if (null == currentUser) {
            throw new UnAuthticatedException();
        }
        if (ConfigManager.getConfig().getAuthority().isUserCachedEnabled()) {
            //从缓存获取用户权限
            log.warn("用户权限缓存开启,从缓存获取用户权限");
            final SessionDAO sessionDAO = authenticationManager.getSessionDAO();
            List<? extends GrantedAuthority> userAuthorities = sessionDAO.getUserAuthorities(currentUser.getId());
            //缓存中没有，通过接口从数据库获取
            if (null == userAuthorities || userAuthorities.isEmpty()) {
                log.warn("用户权限缓存开启,通过接口从数据库获取");
                userAuthorities = authorizationMetaDataService.loadUserAuthorities(currentUser);
                //数据库也没有，抛出无权限异常
                if (null == userAuthorities || userAuthorities.isEmpty()) {
                    log.warn("用户权限缓存开启,从数据库未获取到任何用户权限，访问拒绝");
                    throw new UnAuthorizedException();
                } else {
                    //把数据库获取到的权限进行缓存
                    sessionDAO.setUserAuthorities(currentUser.getId(), userAuthorities);
                    log.warn("用户权限缓存开启,从数据库获取到的用户权限\n{}", userAuthorities);
                }
            }
            return userAuthorities;
        } else {
            log.warn("用户权限缓存未启用，直接从数据库获取");
            return authorizationMetaDataService.loadUserAuthorities(currentUser);
        }

    }

    /**
     * 获取系统权限拦截配置
     * 先从缓存中获取，如果缓存中没有，通过数据提供服务获取。
     * 要对返回数据做空值判断
     *
     * @return the map
     */
    public Map<String, Collection<String>> getSysAuthorities() {
        if (ConfigManager.getConfig().getAuthority().isSysCachedEnabled()) {
            Map<String, Collection<String>> authorities = authorizationDao.getSysAuthorities();
            if (null == authorities || authorities.isEmpty()) {
                log.debug("缓存中系统权限为空，尝试从提供者服务获取");
                final Map<String, Collection<String>> dbAuthorityMap = authorizationMetaDataService.loadSysAuthorities();
                if (null != dbAuthorityMap && dbAuthorityMap.size() > 0) {
                    authorizationDao.setSysAuthorities(dbAuthorityMap);
                    authorities = dbAuthorityMap;
                    log.debug("从提供者服务获取到系统权限总数:{}", authorities.size());
                } else {
                    log.debug("数据库里也没有查到权限");
                }
            }
            log.info("加载到的系统权限:{}", authorities);
            return authorities;
        } else {
            log.warn("系统权限缓存未开启，直接从数据库获取系统权限");
            return authorizationMetaDataService.loadSysAuthorities();
        }

    }

    /**
     * Gets authorization service.
     *
     * @return the authorization service
     */
    public AuthorizationMetaDataService getAuthorizationMetaDataService() {
        return authorizationMetaDataService;
    }

    /**
     * Gets authorization dao.
     *
     * @return the authorization dao
     */
    public AuthorizationMetaDataCacheDao getAuthorizationDao() {
        return authorizationDao;
    }

    /**
     * Gets authentication manager.
     *
     * @return the authentication manager
     */
    public AuthenticationManager getAuthenticationManager() {
        return authenticationManager;
    }
}
