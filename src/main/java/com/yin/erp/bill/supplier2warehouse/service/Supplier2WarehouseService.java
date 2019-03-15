package com.yin.erp.bill.supplier2warehouse.service;


import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillDetailQuotedPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.purchase.dao.PurchaseDao;
import com.yin.erp.bill.purchase.dao.PurchaseDetailDao;
import com.yin.erp.bill.purchase.dao.PurchaseGoodsDao;
import com.yin.erp.bill.purchase.entity.po.PurchaseDetailPo;
import com.yin.erp.bill.purchase.entity.po.PurchasePo;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseDao;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseDetailDao;
import com.yin.erp.bill.supplier2warehouse.dao.Supplier2WarehouseGoodsDao;
import com.yin.erp.bill.supplier2warehouse.entity.po.Supplier2WarehousePo;
import com.yin.erp.stock.service.StockWarehouseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    private PurchaseDao purchaseDao;
    @Autowired
    private PurchaseGoodsDao purchaseGoodsDao;
    @Autowired
    private PurchaseDetailDao purchaseDetailDao;
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
        return billCommonService.findBillPage(vo, supplier2WarehouseDao, new String[]{"warehouseCode", "warehouseName", "supplierName", "supplierCode", "parentBillCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        Supplier2WarehousePo po = (Supplier2WarehousePo) billCommonService.save(new Supplier2WarehousePo(), vo, userSessionBo, supplier2WarehouseDao, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao, "CGSH", this.findParentGoodsList(vo.getParentBillId()), purchaseDao);

        return po;
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Supplier2WarehousePo po = supplier2WarehouseDao.findById(id).get();
            billCommonService.deleteById(id, supplier2WarehouseDao, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao);
        }
    }

    /**
     * 审核
     *
     * @param vo
     */
    @Override
    public void audit(BaseAuditVo vo, UserSessionBo userSessionBo) throws MessageException {
        Date date = new Date();
        for (String id : vo.getIds()) {
            Supplier2WarehousePo po = supplier2WarehouseDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, date, supplier2WarehouseDao, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : supplier2WarehouseDetailDao.findByBillId(id)) {
                    stockWarehouseService.add(detail, po.getWarehouseId());
                }
                //减少上游数量
                if (StringUtils.isNotBlank(po.getParentBillId())) {
                    Integer totalCount = 0;
                    List<BillDetailPo> detailList = supplier2WarehouseDetailDao.findByBillId(po.getId());
                    List<BillDetailPo> parentDetailList = purchaseDetailDao.findByBillId(po.getParentBillId());
                    for (BillDetailPo parentDetail : parentDetailList) {
                        Optional<BillDetailPo> detailOptional = detailList.stream().filter(d -> d.getGoodsId().equals(parentDetail.getGoodsId()) && d.getGoodsColorId().equals(parentDetail.getGoodsColorId()) && d.getGoodsSizeId().equals(parentDetail.getGoodsSizeId())).findFirst();
                        if (detailOptional.isPresent()) {
                            BillDetailPo detailPo = detailOptional.get();
                            BillDetailQuotedPo pd = (BillDetailQuotedPo) parentDetail;
                            pd.setQuotedCount(pd.getQuotedCount() + detailPo.getBillCount());
                            totalCount += detailPo.getBillCount();
//                    if (pd.getQuotedCount() > pd.getBillCount()) {
//                        throw new MessageException("上游单据数量已经不足引用");
//                    }
                            purchaseDetailDao.save((PurchaseDetailPo) pd);
                        }
                    }
                    PurchasePo billQuotedPo = purchaseDao.findById(po.getParentBillId()).get();
                    billQuotedPo.setTotalQuotedCount(billQuotedPo.getTotalQuotedCount() + totalCount);
                    billQuotedPo.setStatus(billQuotedPo.getTotalQuotedCount() < billQuotedPo.getTotalCount() ? BillStatusEnum.AUDITED.name() : BillStatusEnum.COMPLETE.name());
//            if (billQuotedPo.getTotalQuotedCount() > billQuotedPo.getTotalCount()) {
//                throw new MessageException("上游单据数量已经不足引用");
//            }
                    purchaseDao.save(billQuotedPo);
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
            if (StringUtils.isNotBlank(po.getParentBillId())) {
                //释放引用数量
                Integer totalCount = 0;
                List<BillDetailPo> detailList = supplier2WarehouseDetailDao.findByBillId(po.getId());
                List<BillDetailPo> parentDetailList = purchaseDetailDao.findByBillId(po.getParentBillId());
                for (BillDetailPo parentDetail : parentDetailList) {
                    Optional<BillDetailPo> detailOptional = detailList.stream().filter(d -> d.getGoodsId().equals(parentDetail.getGoodsId()) && d.getGoodsColorId().equals(parentDetail.getGoodsColorId()) && d.getGoodsSizeId().equals(parentDetail.getGoodsSizeId())).findFirst();
                    if (detailOptional.isPresent()) {
                        BillDetailPo detailPo = detailOptional.get();
                        BillDetailQuotedPo pd = (BillDetailQuotedPo) parentDetail;
                        pd.setQuotedCount(pd.getQuotedCount() - detailPo.getBillCount());
                        totalCount += detailPo.getBillCount();
                        purchaseDetailDao.save((PurchaseDetailPo) pd);
                    }
                }
                PurchasePo billQuotedPo = purchaseDao.findById(po.getParentBillId()).get();
                billQuotedPo.setTotalQuotedCount(billQuotedPo.getTotalQuotedCount() - totalCount);
                billQuotedPo.setStatus(billQuotedPo.getTotalQuotedCount() < billQuotedPo.getTotalCount() ? BillStatusEnum.AUDITED.name() : BillStatusEnum.COMPLETE.name());
                purchaseDao.save(billQuotedPo);
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
        billCommonService.uploadBill(file, userSessionBo, this, "s2w", purchaseDao);
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, supplier2WarehouseGoodsDao, supplier2WarehouseDetailDao, "supplier", "warehouse", true);
    }

    /**
     * 查询上游单据
     *
     * @param code
     * @return
     */
    public Page<BillPo> findParentBill(String code) {
        return billCommonService.findParentBill(code, purchaseDao, new String[]{ "supplierName", "supplierCode"});
    }

    /**
     * 查询上级单据明细
     *
     * @param id
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findParentGoodsList(String id) throws MessageException {
        return billCommonService.findParentGoodsList(id, purchaseGoodsDao, purchaseDetailDao);
    }
}
