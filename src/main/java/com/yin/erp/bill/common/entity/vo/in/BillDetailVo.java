package com.yin.erp.bill.common.entity.vo.in;

import com.yin.erp.base.entity.vo.BaseVo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

/**
 * 单据详情VO
 *
 * @author yin.weilong
 * @date 2018.12.14
 */
@Getter
@Setter
public class BillDetailVo extends BaseVo {

    @NotNull(message = "详情数量为：0~9999")
    @Min(value = 0, message = "详情数量为：0~9999")
    @Max(value = 99999, message = "详情数量为：0~9999")
    private Integer billCount;

    @NotBlank(message = "请输入颜色")
    @Size(min = 1, max = 32, message = "请输入颜色")
    private String colorId;

    @NotBlank(message = "请输入内长")
    @Size(min = 1, max = 32, message = "请输入内长")
    private String inSizeId;

    @NotBlank(message = "请输入尺码")
    @Size(min = 1, max = 32, message = "请输入尺码")
    private String sizeId;


}
