package com.yin.erp.pos.cash.entity.vo.in;

import com.yin.common.entity.vo.BaseVo;
import lombok.Getter;
import lombok.Setter;

/**
 * POS日结
 *
 * @author yin.weilong
 * @date 2019.02.12
 */
@Getter
@Setter
public class PosDayKnotsInVo extends BaseVo {

    /**
     * 渠道
     */
    private String channelId;

}
