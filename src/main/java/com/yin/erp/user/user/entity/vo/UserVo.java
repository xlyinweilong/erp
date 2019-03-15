package com.yin.erp.user.user.entity.vo;


import com.yin.common.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 登录用户
 */
@Getter
@Setter
public class UserVo extends BasePageVo {

    private String searchKey;

    private String id;

    @Length(max = 100)
    @NotBlank
    private String name;

    @Length(max = 50)
    @NotBlank
    private String account;

    @Length(max = 50)
    @NotBlank
    private String passwd;

    private String roleId;

    private String roleName;
}
