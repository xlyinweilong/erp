package com.yin.erp.bill.warehouseloss.service;


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
import com.yin.erp.bill.settlement.dao.SettlementDao;
import com.yin.erp.bill.warehouseloss.dao.WarehouseLossDao;
import com.yin.erp.bill.warehouseloss.dao.WarehouseLossDetailDao;
import com.yin.erp.bill.warehouseloss.dao.WarehouseLossGoodsDao;
import com.yin.erp.bill.warehouseloss.entity.po.WarehouseLossPo;
import com.yin.erp.stock.service.StockWarehouseService;
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
public class WarehouseLossService extends BillService {

    @Autowired
    private WarehouseLossDao warehouseLossDao;
    @Autowired
    private WarehouseLossGoodsDao warehouseLossGoodsDao;
    @Autowired
    private WarehouseLossDetailDao warehouseLossDetailDao;
    @Autowired
    private StockWarehouseService stockWarehouseService;
    @Autowired
    private SettlementDao settlementDao;
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
        return billCommonService.findBillPage(vo, warehouseLossDao, new String[]{"warehouseName", "warehouseCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new WarehouseLossPo(), vo, userSessionBo, warehouseLossDao, warehouseLossGoodsDao, warehouseLossDetailDao, "CKSY", null, settlementDao);
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, warehouseLossDao, warehouseLossGoodsDao, warehouseLossDetailDao);
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
            WarehouseLossPo po = warehouseLossDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, warehouseLossDao, warehouseLossGoodsDao, warehouseLossDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name()) || vo.getStatus().equals(BillStatusEnum.COMPLETE.name())) {
                for (BillDetailPo detail : warehouseLossDetailDao.findByBillId(id)) {
                    stockWarehouseService.minus(detail, po.getWarehouseId());
                }
            }
        }
        warehouseLossDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            WarehouseLossPo po = warehouseLossDao.findById(id).get();
            billCommonService.unAudit(id, warehouseLossDao, warehouseLossGoodsDao, warehouseLossDetailDao);
            for (BillDetailPo detail : warehouseLossDetailDao.findByBillId(id)) {
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
        return billCommonService.findById(id, warehouseLossDao, warehouseLossGoodsDao, warehouseLossDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "wl");
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, warehouseLossGoodsDao, warehouseLossDetailDao, "warehouse", null, true);
    }

}
