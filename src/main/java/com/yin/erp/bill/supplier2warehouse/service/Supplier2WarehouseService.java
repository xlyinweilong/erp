package com.yin.erp.bill.supplier2warehouse.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseDao;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseDetailDao;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseGoodsDao;
import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehousePo;
import com.yin.erp.stock.service.StockWarehouseService;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 厂家来货
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class Supplier2WarehouseService extends BillService {

    @Autowired
    private Supplier2WarehouseDao supplier2WarehouseDao;
    @Autowired
    private Supplier2WarehouseGoodsDao supplier2WarehouseGoodsDao;
    @Autowired
    private Supplier2WarehouseDetailDao supplier2WarehouseDetailDao;
    @Autowired
    private StockWarehouseService stockWarehouseService;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;
    @Autowired
    private BillCommonService billCommonService;

    /**
     * 查询
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @Override
    public BackPageVo<BillVo> findBillPage(SearchBillVo vo) throws MessageException {
        return billCommonService.findBillPage(vo, supplier2WarehouseDao, new String[]{"warehouseCode", "warehouseName", "supplierName", "supplierCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new Supplier2WarehousePo(), vo, userSessionBo, supplier2WarehouseDao, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao, "CGSH");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, supplier2WarehouseDao, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao);
        }
    }

    /**
     * 审核
     *
     * @param vo
     */
    public void audit(BaseAuditVo vo, UserSessionBo userSessionBo) throws MessageException {
        Date d = new Date();
        for (String id : vo.getIds()) {
            Supplier2WarehousePo po = supplier2WarehouseDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, supplier2WarehouseDao, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : supplier2WarehouseDetailDao.findByBillId(id)) {
                    stockWarehouseService.add(detail, po.getWarehouseId());
                }
            }
        }
        supplier2WarehouseDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Supplier2WarehousePo po = supplier2WarehouseDao.findById(id).get();
            billCommonService.unAudit(id, supplier2WarehouseDao, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao);
            for (BillDetailPo detail : supplier2WarehouseDetailDao.findByBillId(id)) {
                stockWarehouseService.minus(detail, po.getWarehouseId());
            }
        }
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        return billCommonService.findById(id, supplier2WarehouseDao, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "s2w");
    }

    @Override
    public String uploadBillSupplierCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(1));
    }

    @Override
    public String uploadBillWarehouseCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(2));
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao, "supplier", "warehouse", false);
    }

}
