package com.yin.erp.bill.warehouseinventory.service;


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
import com.yin.erp.bill.warehouseinventory.dao.WarehouseInventoryDao;
import com.yin.erp.bill.warehouseinventory.dao.WarehouseInventoryDetailDao;
import com.yin.erp.bill.warehouseinventory.dao.WarehouseInventoryGoodsDao;
import com.yin.erp.bill.warehouseinventory.entity.po.WarehouseInventoryPo;
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
 * 仓库损益
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class WarehouseInventoryService extends BillService {

    @Autowired
    private WarehouseInventoryDao warehouseInventoryDao;
    @Autowired
    private WarehouseInventoryGoodsDao warehouseInventoryGoodsDao;
    @Autowired
    private WarehouseInventoryDetailDao warehouseInventoryDetailDao;
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
        return billCommonService.findBillPage(vo, warehouseInventoryDao, new String[]{"warehouseName", "warehouseCode", "warehouseName", "warehouseCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new WarehouseInventoryPo(), vo, userSessionBo, warehouseInventoryDao, warehouseInventoryGoodsDao, warehouseInventoryDetailDao, "CKSY");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            warehouseInventoryDetailDao.deleteAllByBillId(id);
            warehouseInventoryGoodsDao.deleteAllByBillId(id);
            warehouseInventoryDao.deleteById(id);
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
            WarehouseInventoryPo po = warehouseInventoryDao.findById(id).get();
            po.setAuditUserId(userSessionBo.getId());
            po.setAuditUserName(userSessionBo.getName());
            po.setStatus(vo.getStatus());
            po.setAuditDate(d);
            warehouseInventoryDao.save(po);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : warehouseInventoryDetailDao.findByBillId(id)) {
                    stockWarehouseService.minus(detail, po.getWarehouseId());
                }
            }
        }
        warehouseInventoryDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            WarehouseInventoryPo po = warehouseInventoryDao.findById(id).get();
            po.setStatus("AUDIT_FAILURE");
            warehouseInventoryDao.save(po);
            for (BillDetailPo detail : warehouseInventoryDetailDao.findByBillId(id)) {
                stockWarehouseService.add(detail, po.getWarehouseId());
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
        return billCommonService.findById(id, warehouseInventoryDao, warehouseInventoryGoodsDao, warehouseInventoryDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "c2s");
    }

    @Override
    public String uploadBillWarehouseCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(1));
    }

    @Override
    public String uploadBillSupplierCode(Row row) throws MessageException {
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
        billCommonService.export(vo, response, this, warehouseInventoryGoodsDao, warehouseInventoryDetailDao, "warehouse", "toWarehouse", true);
    }

}
