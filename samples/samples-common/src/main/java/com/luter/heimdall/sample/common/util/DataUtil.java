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

package com.luter.heimdall.sample.common.util;

import com.luter.heimdall.core.manager.AuthorizationManager;
import com.luter.heimdall.sample.common.dto.SysResourceDTO;
import com.luter.heimdall.sample.common.dto.SysUserDTO;
import com.luter.heimdall.sample.common.encoder.BCryptPasswordEncoder;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 模拟数据工具类
 * <p>
 * <p>
 * <p>
 * 系统权限以Map<String,String>形式存储在缓存里。
 * Map.key= url
 * Map.value = perm
 * <p>
 * 对于精确url授权，要求Url唯一，也就是Key不可以重复
 * <p>
 * <p>
 * 对于restful形式授权，key+method不可以重复。
 * <p>
 * 由于是以Map形式保存在缓存里，所以重复key只会保留一个
 * <p>
 * 这种方式下，key就相当于一个拦截规则，也就是说
 * 只要看到有这个url，就进入授权逻辑
 *
 * @author Luter
 */
public final class DataUtil {

    private final static String PASSWORD = new BCryptPasswordEncoder().encode("aaaaaa");

    /**
     * 模拟数据，生成一批精确url匹配形式的系统资源
     * <p>
     * <p>
     * 一个 url 对应一个唯一的资源权限(perm)标识。
     * 通过 url 判断请求是否需要授权访问以及需要什么资源权限标识(perm)
     * 通过用户信息实体持有的系统资源权限标识进行匹配授权
     *
     * @return the simple resource list
     */
    public static List<SysResourceDTO> getSimpleResourceList() {
        List<SysResourceDTO> resourceList = new ArrayList<>();
        ////精确匹配Url和权限标识符的，精确拦截到url，然后根据perm查看是否具备权限
        // 对应的UserDetails.getAuthorities方法里，要使用 SimpleGrantedAuthority
        //精确url模式下，url和perm均不能重复
        resourceList.add(new SysResourceDTO(1L, "新增数据", null, "/pet/cat/save", "catSave"));
        resourceList.add(new SysResourceDTO(2L, "查看列表", null, "/pet/cat/list", "catList"));
        resourceList.add(new SysResourceDTO(3L, "删除数据", null, "/pet/cat/delete", "catDelete"));
        resourceList.add(new SysResourceDTO(4L, "修改数据", null, "/pet/cat/update", "catUpdate"));
        resourceList.add(new SysResourceDTO(5L, "查看详情", null, "/pet/cat/detail", "catDetail"));
        return resourceList;
    }


    /**
     * Gets simple user list.
     *
     * @return the simple user list
     */
    public static List<SysUserDTO> getSimpleUserList() {
        List<SysUserDTO> userList = new ArrayList<>();
        final List<SysResourceDTO> simpleResourceList = getSimpleResourceList();
        /////用户
        List<SysResourceDTO> adminRes = new ArrayList<>();
        //用户权限
        //        admin
        adminRes.add(simpleResourceList.get(0));
        adminRes.add(simpleResourceList.get(1));
        //      luter 全部权限
        List<SysResourceDTO> luterRes = new ArrayList<>(simpleResourceList);
        //系统用户
        userList.add(new SysUserDTO(1L, "admin", "17777777777", PASSWORD, true, adminRes, Arrays.asList("admin", "user")));
        userList.add(new SysUserDTO(2L, "luter", "18888888888", PASSWORD, true, luterRes, Arrays.asList("guest", "user")));
        return userList;
    }

    /**
     * 模拟数据，生成一批 restful 形式的 系统资源
     *
     * @return the restful resource list
     */
    public static List<SysResourceDTO> getRestfulResourceList() {
        List<SysResourceDTO> resourceList = new ArrayList<>();
        ///////
        //授权逻辑实现:AuthorizationManager.isAuthorized()
        //restful形式授权：通过method+url判断：MethodAndUrlGrantedAuthority
        //普通精确Url形式授权:通过url和对应权限标志判断：SimpleGrantedAuthority
        ///////
        // restful形式权限资源，首先根据url拦截是否需要授权，然后根据method和url查找此用户是否具备权限
        //此方式下，perm 权限标识变成了这种形式:METHOD:URL，参见:MethodAndUrlGrantedAuthority.getAuthority()方法
        //如果使用RequiresPermission、RequiresPermissions 注解，value参数中要注意写法
        //比如： @RequiresPermission("GET:/pet/cat")、@RequiresPermission("POST:/pet/cat")
        // 对应的UserDetails.getAuthorities方法里，要使用 MethodAndUrlGrantedAuthority
        resourceList.add(new SysResourceDTO(1L, "新增数据", HttpMethod.POST.name(), "/pet/cat", ""));
        resourceList.add(new SysResourceDTO(2L, "查看列表", HttpMethod.GET.name(), "/pet/cat", ""));
        resourceList.add(new SysResourceDTO(3L, "删除数据", HttpMethod.DELETE.name(), "/pet/cat/*", ""));
        resourceList.add(new SysResourceDTO(4L, "修改数据", HttpMethod.PUT.name(), "/pet/cat", ""));
        resourceList.add(new SysResourceDTO(5L, "查看详情", HttpMethod.GET.name(), "/pet/cat/*", ""));
        resourceList.add(new SysResourceDTO(6L, "查看详情", AuthorizationManager.ALL_METHOD_NAME, "/pet/cat", ""));
        return resourceList;
    }

    /**
     * Gets restful user list.
     *
     * @return the restful user list
     */
    public static List<SysUserDTO> getRestfulUserList() {
        List<SysUserDTO> userList = new ArrayList<>();
        final List<SysResourceDTO> restfulResourceList = getRestfulResourceList();
        //用户
        List<SysResourceDTO> adminRes = new ArrayList<>();
        //用户权限
        //        admin
        adminRes.add(restfulResourceList.get(0));
        adminRes.add(restfulResourceList.get(1));
        //      luter 全部权限
        List<SysResourceDTO> luterRes = new ArrayList<>(restfulResourceList);
        //系统用户
        userList.add(new SysUserDTO(1L, "admin", "17777777777", PASSWORD, true, adminRes, Arrays.asList("admin", "user")));
        userList.add(new SysUserDTO(2L, "luter", "18888888888", PASSWORD, true, luterRes, Arrays.asList("admin", "guest")));
        return userList;
    }


}
