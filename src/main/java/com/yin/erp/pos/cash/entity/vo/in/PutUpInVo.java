package com.yin.erp.pos.cash.entity.vo.in;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * 挂单-挂入
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class PutUpInVo {

    private String id;

    @NotBlank
    private String code;

    @NotBlank
    private String json;

}
