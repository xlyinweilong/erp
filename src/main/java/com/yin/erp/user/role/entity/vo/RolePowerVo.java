package com.yin.erp.user.role.entity.vo;


import com.yin.common.entity.vo.in.BasePageVo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 角色权限
 */
@Getter
@Setter
public class RolePowerVo extends BasePageVo {

    private String roleName;

    private String roleId;

    private List<String> selectPowerKeys;

    private List<String> selectGoodsGroupIds;

    private List<String> selectChannelGroupIds;

    private List<String> selectWarehouseGroupIds;

    private List<String> selectSupplierGroupIds;
}
