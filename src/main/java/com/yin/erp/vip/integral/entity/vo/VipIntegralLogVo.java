package com.yin.erp.vip.integral.entity.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 会员积分日志
 *
 * @author yin
 */
@Getter
@Setter
public class VipIntegralLogVo extends BasePageVo {

    private String searchKey;

    @Length(max = 32)
    private String id;

    @Length(max = 20)
    private String code;

    @Length(max = 100)
    private String name;

    private Integer sex = -1;

    private BigDecimal balance = BigDecimal.ZERO;

    private Integer integral = 0;

    private Integer xpValue = 0;

    @Length(max = 32)
    private String openChannelId;

    @Length(max = 32)
    private String openEmployId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate openDate;

}
