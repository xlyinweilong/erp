package com.yin.erp.info.employ.entity.vo;

import com.yin.erp.base.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

/**
 * 营业员VO
 *
 * @author yin
 */
@Getter
@Setter
public class EmployVo extends BasePageVo {

    private String id;

    private String code;

    private String name;

    private String searchKey;

}
