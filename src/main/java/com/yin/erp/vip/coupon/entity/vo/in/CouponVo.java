package com.yin.erp.vip.coupon.entity.vo.in;

import com.yin.erp.vip.coupon.entity.po.VipCouponPo;
import lombok.Getter;
import lombok.Setter;

/**
 * 代用卷
 *
 * @author yin.weilong
 * @date 2019.03.10
 */
@Getter
@Setter
public class CouponVo extends VipCouponPo {

    /**
     * 产生数量
     */
    private Integer createCount;
}
