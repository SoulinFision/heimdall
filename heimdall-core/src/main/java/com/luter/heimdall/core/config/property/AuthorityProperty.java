package com.luter.heimdall.core.config.property;

import lombok.Data;

@Data
public class AuthorityProperty {

    /**
     * 是否开启系统权限缓存，默认：true，开启
     * <p>
     * 如果关闭，则每次权限校验都会从数据库直接获取
     */
    private boolean sysCachedEnabled = true;
    /**
     * 系统权限在缓存中的key
     */
    private String sysCachedKey = "heimdall:sysAuthorities";
    /**
     * 系统权限在缓存中保存的时长，单位:小时.
     * <p>
     * 超过这个时长将会被清理，默认 :24小时
     */
    private long sysExpire = 24;

    /**
     * 是否开启用户权限缓存，默认：true，开启
     * <p>
     * 如果关闭，则每次权限校验都会从数据库直接获取
     */
    private boolean userCachedEnabled = true;
    /**
     * 用户权限在缓存中的key
     */
    private String userCachedKey = "heimdall:userAuthorities";
    /**
     * 用户权限在缓存中保存的时长，单位:小时.
     * <p>
     * 超过这个时长将会被清理，默认 :24小时
     */
    private long userExpire = 24;
}
