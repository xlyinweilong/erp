package com.yin.erp.bill.purchase.service;


import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.purchase.dao.PurchaseDao;
import com.yin.erp.bill.purchase.dao.PurchaseDetailDao;
import com.yin.erp.bill.purchase.dao.PurchaseGoodsDao;
import com.yin.erp.bill.purchase.entity.po.PurchasePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 采购单服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class PurchaseService extends BillService {

    @Autowired
    private PurchaseDao purchaseDao;
    @Autowired
    private PurchaseGoodsDao purchaseGoodsDao;
    @Autowired
    private PurchaseDetailDao purchaseDetailDao;
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
        return billCommonService.findBillPage(vo, purchaseDao, new String[]{"supplierId","supplierName"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new PurchasePo(), vo, userSessionBo, purchaseDao, purchaseGoodsDao, purchaseDetailDao, "CGD");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, purchaseDao, purchaseGoodsDao, purchaseDetailDao);
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
            PurchasePo po = purchaseDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, purchaseDao, purchaseGoodsDao, purchaseDetailDao);
        }
        purchaseDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            PurchasePo po = purchaseDao.findById(id).get();
            billCommonService.unAudit(id, purchaseDao, purchaseGoodsDao, purchaseDetailDao);
        }
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        return billCommonService.findById(id, purchaseDao, purchaseGoodsDao, purchaseDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "purchase");
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, purchaseGoodsDao, purchaseDetailDao, null, null, false);
    }

}
