package com.yin.erp.pos.cash.entity.vo.in;

import com.yin.common.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class PosMyBillVo extends BasePageVo {

    /**
     * 渠道ID
     */
    private String channelId;

}
