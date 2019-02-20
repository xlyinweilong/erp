package com.yin.erp.vip.grade.entity.vo;


import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

/**
 * 会员等级
 *
 * @author yin
 */
@Getter
@Setter
public class VipGradeVo extends BasePageVo {

    @Length(max = 32)
    private String id;

    @Length(max = 100)
    private String name;

    private Integer indexDepth;

    private boolean defaultGrade = false;

    private Integer lowestXpValue;

    private BigDecimal discount;
}
