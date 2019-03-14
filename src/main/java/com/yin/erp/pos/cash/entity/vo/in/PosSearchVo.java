package com.yin.erp.pos.cash.entity.vo.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 查询VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class PosSearchVo extends BasePageVo {

    private String channelCode;
    private String goodsCode;
    private String goodsColorCode;
    private String goodsColorName;
    private String goodsSizeName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate endDate;

}
