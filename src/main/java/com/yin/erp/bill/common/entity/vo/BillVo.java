package com.yin.erp.bill.common.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.erp.base.entity.vo.in.BasePageVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 单据Vo
 *
 * @author yin.weilong
 * @date 2018.12.02
 */
@Getter
@Setter
public class BillVo extends BasePageVo {

    private String id;
    /**
     * 单号
     */
    private String code;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate billDate;

    private String supplierId;

    private String warehouseId;

    private String supplierName;

    private String warehouseName;

    private String supplierCode;

    private String warehouseCode;

    private BigDecimal totalAmount;

    private BigDecimal totalTagAmount;

    private Integer totalCount;

    private String status;

    @NotNull(message = "请输入货品")
    private List<BillGoodsVo> goodsList;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date auditDate;

    private String createUserName;

    private String auditUserName;


    /**
     * 单据状态
     *
     * @return
     */
    public String getStatusMean() {
        return BillStatusEnum.getMean(BillStatusEnum.valueOf(status));
    }

}
