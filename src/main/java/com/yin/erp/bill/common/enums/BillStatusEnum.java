package com.yin.erp.bill.common.enums;

/**
 * 单据状态
 *
 * @author yin.weilong
 * @date 2018.12.03
 */
public enum BillStatusEnum {
    DRAFT, PENDING, AUDITED, COMPLETE, QUOTE, AUDIT_FAILURE;

    /**
     * 单据中文意思
     *
     * @param billStatusEnum
     * @return
     */
    public static String getMean(BillStatusEnum billStatusEnum) {
        switch (billStatusEnum) {
            case DRAFT:
                return "草稿";
            case PENDING:
                return "待审核";
            case AUDITED:
                return "通过审核";
            case AUDIT_FAILURE:
                return "审核失败";
            case COMPLETE:
                return "完成";
            case QUOTE:
                return "被引用";
            default:
                return null;
        }
    }
}
