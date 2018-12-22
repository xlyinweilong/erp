package com.yin.erp.info.channel.entity.vo;

import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 渠道VO
 *
 * @author yin
 */
@Getter
@Setter
public class ChannelVo extends BasePageVo {

    private String id;

    private String code;

    private String name;

}
