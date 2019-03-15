package com.yin.erp.user.role.entity.vo;


import com.yin.common.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

/**
 * 登录用户
 */
@Getter
@Setter
public class RoleVo extends BasePageVo {

    private String id;

    @Length(max = 100)
    private String name;


}
