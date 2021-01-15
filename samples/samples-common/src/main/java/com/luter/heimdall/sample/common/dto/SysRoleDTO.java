package com.luter.heimdall.sample.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleDTO implements Serializable {
    /**
     * The Id.
     */
    private Long id;
    /**
     * The Username.
     */
    private String name;
}
