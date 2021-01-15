package com.luter.heimdall.sample.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 模拟角色-权限关系表数据
 * <p>
 * 正常情况下，这个表存储的是 role.id 和 resource.id，
 * 然后通过 join 关联对应资源，此处方便演示，用的最终字段
 *
 * @author Luter
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleResourceDTO implements Serializable {

    /**
     * url
     */
    private String url;
    /**
     * 角色名称
     */
    private String roleName;
}
