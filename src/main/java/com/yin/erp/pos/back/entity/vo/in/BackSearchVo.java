package com.yin.erp.pos.back.entity.vo.in;

import com.yin.common.entity.vo.BaseVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 退货查询VO
 *
 * @author yin.weilong
 * @date 2019.02.13
 */
@Getter
@Setter
public class BackSearchVo extends BaseVo {

    /**
     * 单据编号
     */
    private String billCode;

    /**
     * 渠道ID
     */
    private String channelId;

}
