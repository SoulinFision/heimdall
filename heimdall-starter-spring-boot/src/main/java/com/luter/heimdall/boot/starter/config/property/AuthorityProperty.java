package com.luter.heimdall.boot.starter.config.property;

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
     * 注意: 过期时间只对 redis 缓存有效，内存缓存不存在过期
     * <p>
     * 系统权限在缓存中保存的时长，单位:小时.
     * <p>
     * 超过将会被清理，默认 :24小时.
     * <p>
     * 过期时间尽量设置的长一些
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
     * 注意: 过期时间只对 redis 缓存有效，内存缓存不存在过期
     * <p>
     * 用户权限在缓存中保存的时长，单位:小时.
     * <p>
     * 超过将会被清理，默认 :24小时
     * <p>
     * 过期时间尽量设置的长一些
     */
    private long userExpire = 24;


}
