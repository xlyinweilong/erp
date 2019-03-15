package com.yin.erp.bill.warehouse2supplier.service;


import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.warehouse2supplier.dao.Warehouse2SupplierDao;
import com.yin.erp.bill.warehouse2supplier.dao.Warehouse2SupplierDetailDao;
import com.yin.erp.bill.warehouse2supplier.dao.Warehouse2SupplierGoodsDao;
import com.yin.erp.bill.warehouse2supplier.entity.po.Warehouse2SupplierPo;
import com.yin.erp.stock.service.StockWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 仓库出货
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class Warehouse2SupplierService extends BillService {

    @Autowired
    private Warehouse2SupplierDao warehouse2SupplierDao;
    @Autowired
    private Warehouse2SupplierGoodsDao warehouse2SupplierGoodsDao;
    @Autowired
    private Warehouse2SupplierDetailDao warehouse2SupplierDetailDao;
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
        return billCommonService.findBillPage(vo, warehouse2SupplierDao, new String[]{"warehouseCode", "warehouseName", "supplierName", "supplierCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new Warehouse2SupplierPo(), vo, userSessionBo, warehouse2SupplierDao, warehouse2SupplierGoodsDao, warehouse2SupplierDetailDao, "CKTH");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, warehouse2SupplierDao, warehouse2SupplierGoodsDao, warehouse2SupplierDetailDao);
        }
    }

    /**
     * 审核
     *
     * @param vo
     */
    @Override
    public void audit(BaseAuditVo vo, UserSessionBo userSessionBo) throws MessageException {
        Date d = new Date();
        for (String id : vo.getIds()) {
            Warehouse2SupplierPo po = warehouse2SupplierDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, warehouse2SupplierDao, warehouse2SupplierGoodsDao, warehouse2SupplierDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : warehouse2SupplierDetailDao.findByBillId(id)) {
                    stockWarehouseService.minus(detail, po.getWarehouseId());
                }
            }
        }
        warehouse2SupplierDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Warehouse2SupplierPo po = warehouse2SupplierDao.findById(id).get();
            billCommonService.unAudit(id, warehouse2SupplierDao, warehouse2SupplierGoodsDao, warehouse2SupplierDetailDao);
            for (BillDetailPo detail : warehouse2SupplierDetailDao.findByBillId(id)) {
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
        return billCommonService.findById(id, warehouse2SupplierDao, warehouse2SupplierGoodsDao, warehouse2SupplierDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "w2s");
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, warehouse2SupplierGoodsDao, warehouse2SupplierDetailDao, "warehouse", "supplier", false);
    }


}
