package com.yin.erp.info.channel.entity.vo;

import com.yin.common.entity.vo.in.BasePageVo;
import com.yin.erp.config.sysconfig.entity.po.ConfigChannelPo;
import com.yin.erp.config.sysconfig.entity.po.ConfigPo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;

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

    @NotBlank
    private String name;

    private String groupId;

    private String groupName;

    @NotBlank
    private String marketPointId;

    private String marketPointCode;

    /**
     * 传入配置
     */
    private List<ConfigPo> channelConfigList;

    /**
     * 传出配置
     */
    private List<ConfigChannelPo> channelConfigChannelList;

}
