package com.yin.erp.user.diy.entity.vo.in;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 偏好VO
 *
 * @author yin.weilong
 * @date 2019.03.07
 */
@Getter
@Setter
public class DiyVo {

    @NotBlank
    private String type;

    @NotNull
    private List<String> keys;
}
