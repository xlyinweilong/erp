package com.yin.erp.bill.common.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.common.entity.vo.in.BasePageVo;
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

    /**
     * 手动单号
     */
    private String manualCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate billDate;

    private String parentBillId;

    private String parentBillCode;

    private String grandParentBillId;

    private String grandParentBillCode;

    private String supplierId;

    private String warehouseId;

    private String channelId;

    private String toChannelId;

    private String supplierName;

    private String warehouseName;

    private String channelName;

    private String toChannelName;

    private String supplierCode;

    private String warehouseCode;

    private String channelCode;

    private String toChannelCode;

    private BigDecimal totalAmount;

    private BigDecimal totalTagAmount;

    private Integer totalCount;

    private Integer totalQuotedCount;

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
     * 盘点-盘次
     */
    private Integer times;


    /**
     * 单据状态
     *
     * @return
     */
    public String getStatusMean() {
        return BillStatusEnum.getMean(BillStatusEnum.valueOf(status));
    }

}
