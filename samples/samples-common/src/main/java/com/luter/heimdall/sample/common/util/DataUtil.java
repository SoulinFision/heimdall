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

import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.authorization.authority.MethodAndUrlGrantedAuthority;
import com.luter.heimdall.core.authorization.authority.SimpleGrantedAuthority;
import com.luter.heimdall.core.manager.AuthorizationManager;
import com.luter.heimdall.sample.common.dto.SysResourceDTO;
import com.luter.heimdall.sample.common.dto.SysRoleResourceDTO;
import com.luter.heimdall.sample.common.dto.SysUserDTO;
import com.luter.heimdall.sample.common.encoder.BCryptPasswordEncoder;
import org.springframework.http.HttpMethod;

import java.util.*;
import java.util.stream.Collectors;

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
 * <p>
 * <p>
 * 对于角色授权
 * <p>
 * 系统权限数据通过角色-资源关系表关联获取，这种情况下就会出现一个 url 属于多个角色的情况。
 * 所以权限拦截规则需要对关系数据按照 url 分组处理
 * <p>
 * 权限匹配的时候，通过 contains 进行判断，只要具备多个角色中的一个，就具有权限
 *
 * @author Luter
 */
public final class DataUtil {

    /**
     * The constant PASSWORD.
     */
    private final static String PASSWORD = new BCryptPasswordEncoder().encode("aaaaaa");

    /**
     * 模拟 资源---角色 关系表数据
     *
     * @return the role resource list
     */
    public static List<SysRoleResourceDTO> getRoleResourceList() {
        List<SysRoleResourceDTO> roleResources = new ArrayList<>();
        roleResources.add(new SysRoleResourceDTO().setUrl("/pet/cat").setRoleName("admin"));
        roleResources.add(new SysRoleResourceDTO().setUrl("/pet/cat").setRoleName("user"));
        roleResources.add(new SysRoleResourceDTO().setUrl("/pet/cat/*").setRoleName("guest"));
        return roleResources;
    }


    /**
     * 获取系统角色权限规则
     *
     * @return the role perms
     */
    public static Map<String, Collection<String>> getRolePerms() {
        final List<SysRoleResourceDTO> resources = DataUtil.getRoleResourceList();
        //url 分组
        final Map<String, List<SysRoleResourceDTO>> collect = resources.stream().collect(Collectors.groupingBy(SysRoleResourceDTO::getUrl));
        Map<String, Collection<String>> map = new LinkedHashMap<>();
        for (Map.Entry<String, List<SysRoleResourceDTO>> stringListEntry : collect.entrySet()) {
            final List<SysRoleResourceDTO> value = stringListEntry.getValue();
            final List<String> perms = value.stream().map(SysRoleResourceDTO::getRoleName).collect(Collectors.toList());
            map.put(stringListEntry.getKey(), perms);
        }
        return map;
    }


    /**
     * 获取用户角色权限规则
     *
     * @param principal the principal
     * @return the role perms
     */
    public static List<? extends GrantedAuthority> getUserRolePerms(String principal) {
        List<SimpleGrantedAuthority> adminPerms = new ArrayList<>();
        adminPerms.add(new SimpleGrantedAuthority("admin"));
        adminPerms.add(new SimpleGrantedAuthority("user"));
        adminPerms.add(new SimpleGrantedAuthority("guest"));
        List<SimpleGrantedAuthority> luterPerms = new ArrayList<>();
        luterPerms.add(new SimpleGrantedAuthority("user"));
        switch (principal) {
            case "APP:1":
                return adminPerms;
            case "APP:2":
                return luterPerms;
            case "PC:1":
                return adminPerms;
            case "PC:2":
                return luterPerms;
            default:
                return new ArrayList<>();
        }
    }

    /**
     * 模拟系统 资源表 数据
     * <p>
     * 一个唯一资源 对应一个唯一 perm
     *
     * @return the simple resource list
     */
    public static List<SysResourceDTO> getExactUrlResourceList() {
        List<SysResourceDTO> resourceList = new ArrayList<>();
        resourceList.add(new SysResourceDTO(1L, "新增数据", null, "/pet/cat/save", "catSave"));
        resourceList.add(new SysResourceDTO(2L, "查看列表", null, "/pet/cat/list", "catList"));
        resourceList.add(new SysResourceDTO(3L, "删除数据", null, "/pet/cat/delete/*", "catDelete"));
        resourceList.add(new SysResourceDTO(4L, "修改数据", null, "/pet/cat/update", "catUpdate"));
        resourceList.add(new SysResourceDTO(5L, "查看详情", null, "/pet/cat/detail/*", "catDetail"));
        return resourceList;
    }

    /**
     * 精确匹配路由 Url 的系统权限
     *
     * @return the exact url perms
     */
    public static Map<String, Collection<String>> getExactUrlPerms() {
        final List<SysResourceDTO> resources = DataUtil.getExactUrlResourceList();
        Map<String, Collection<String>> perms = new LinkedHashMap<>(resources.size());
        //实际使用的时候，需要对 url 和 perm 进行校验
        for (SysResourceDTO resource : resources) {
            List<String> p = new ArrayList<>(1);
            p.add(resource.getPerm());
            perms.put(resource.getUrl(), p);
        }
        return perms;
    }

    /**
     * 获取精确路由 url 的 用户权限
     *
     * @param principal the principal
     * @return the exact user perms
     */
    public static List<? extends GrantedAuthority> getExactUserPerms(String principal) {
        final List<SysResourceDTO> resources = DataUtil.getExactUrlResourceList();
        List<SimpleGrantedAuthority> luterPerms = new ArrayList<>();
        luterPerms.add(new SimpleGrantedAuthority(resources.get(1).getPerm()));
        luterPerms.add(new SimpleGrantedAuthority(resources.get(2).getPerm()));
        List<SimpleGrantedAuthority> adminPerms = resources.stream().map(d -> new SimpleGrantedAuthority(d.getPerm())).collect(Collectors.toList());
        switch (principal) {
            case "APP:1":
                return adminPerms;
            case "APP:2":
                return luterPerms;
            case "PC:1":
                return adminPerms;
            case "PC:2":
                return luterPerms;
            default:
                return new ArrayList<>();
        }
    }

    /**
     * Gets simple user list.
     *
     * @return the simple user list
     */
    public static List<SysUserDTO> getSimpleUserList() {
        List<SysUserDTO> userList = new ArrayList<>();
        //系统用户
        userList.add(new SysUserDTO(1L, "admin", "17777777777", PASSWORD, true));
        userList.add(new SysUserDTO(2L, "luter", "18888888888", PASSWORD, true));
        return userList;
    }

    /**
     * 模拟 系统资源表 数据
     * <p>
     * 生成一批 restful 风格 资源
     * Method+url 不能重复
     *
     * @return the restful resource list
     */
    private static List<SysResourceDTO> getRestfulResourceList() {
        List<SysResourceDTO> resourceList = new ArrayList<>();
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
    public static Map<String, Collection<String>> getRestfulPerm() {
        final List<SysResourceDTO> resources = DataUtil.getRestfulResourceList();
        Map<String, Collection<String>> perms = new LinkedHashMap<>();
        //实际使用的时候，需要对 url 和 perm 进行校验
        for (SysResourceDTO resource : resources) {
            //restful 形式下的权限、角色标志无意义，一句 method+url 进行授权
            perms.put(resource.getUrl(), null);
        }
        return perms;
    }

    public static List<? extends GrantedAuthority> getUserRestfulPerm(String principal) {
        final List<SysResourceDTO> resources = DataUtil.getRestfulResourceList();
        List<MethodAndUrlGrantedAuthority> luterPerms = new ArrayList<>();
        luterPerms.add(new MethodAndUrlGrantedAuthority(resources.get(1).getMethod(), resources.get(1).getUrl()));
        luterPerms.add(new MethodAndUrlGrantedAuthority(resources.get(2).getMethod(), resources.get(2).getUrl()));
        List<MethodAndUrlGrantedAuthority> adminPerms =
                resources.stream().map(d -> new MethodAndUrlGrantedAuthority(d.getMethod(), d.getUrl())).collect(Collectors.toList());
        switch (principal) {
            case "APP:1":
                return adminPerms;
            case "APP:2":
                return luterPerms;
            case "PC:1":
                return adminPerms;
            case "PC:2":
                return luterPerms;
            default:
                return new ArrayList<>();
        }
    }
}
