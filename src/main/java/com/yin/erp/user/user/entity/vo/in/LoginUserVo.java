package com.yin.erp.user.user.entity.vo.in;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 登录用户
 */
@Getter
@Setter
public class LoginUserVo {

    private String name;

    private String token;

    @NotBlank
    @Length(min = 1, max = 50)
    private String username;

    @NotBlank
    @Length(min = 6, max = 30)
    private String password;

}
