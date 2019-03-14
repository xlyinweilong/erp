package com.yin.erp.vip.common.vo;

import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询VO
 *
 * @author yin.weilong
 * @date 2019.03.09
 */
@Getter
@Setter
public class BaseVipSearchVo extends BasePageVo {

    private String searchKey;
}
