package com.yin.erp.bill.common.service;

import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import org.apache.poi.ss.usermodel.Row;

import java.math.BigDecimal;

/**
 * 单据服务
 *
 * @author yin.weilong
 * @date 2018.12.18
 */
public class BillService implements BillServiceInterface {

    public BackPageVo<BillVo> findBillPage(SearchBillVo vo) throws MessageException {
        return null;
    }

    @Override
    public void save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
    }

    public String uploadBillParentBillCode(Row row) throws MessageException {
        return null;
    }

    public String uploadBillChannelCode(Row row) throws MessageException {
        return null;
    }

    public String uploadBillSupplierCode(Row row) throws MessageException {
        return null;
    }

    public String uploadBillWarehouseCode(Row row) throws MessageException {
        return null;
    }

    public String uploadBillGoodsCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(3));
    }

    public String uploadBillGoodsColorCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(4));
    }

    public String uploadBillGoodsColorName(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(5));
    }

    public String uploadBillGoodsSizeName(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(6));
    }

    public BigDecimal uploadBillPrice(Row row) throws MessageException {
        return ExcelReadUtil.getBigDecimal(row.getCell(7));
    }

    public Integer uploadBillBillCount(Row row) throws MessageException {
        return ExcelReadUtil.getInteger(row.getCell(8));
    }
}
