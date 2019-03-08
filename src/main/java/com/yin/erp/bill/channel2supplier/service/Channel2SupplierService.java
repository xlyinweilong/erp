package com.yin.erp.bill.channel2supplier.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.bill.channel2supplier.dao.Channel2SupplierDao;
import com.yin.erp.bill.channel2supplier.dao.Channel2SupplierDetailDao;
import com.yin.erp.bill.channel2supplier.dao.Channel2SupplierGoodsDao;
import com.yin.erp.bill.channel2supplier.entity.po.Channel2SupplierPo;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.stock.service.StockChannelService;
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
public class Channel2SupplierService extends BillService {

    @Autowired
    private Channel2SupplierDao channel2SupplierDao;
    @Autowired
    private Channel2SupplierGoodsDao channel2SupplierGoodsDao;
    @Autowired
    private Channel2SupplierDetailDao channel2SupplierDetailDao;
    @Autowired
    private StockChannelService stockChannelService;
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
        return billCommonService.findBillPage(vo, channel2SupplierDao, new String[]{"supplierCode", "supplierName", "channelName", "channelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new Channel2SupplierPo(), vo, userSessionBo, channel2SupplierDao, channel2SupplierGoodsDao, channel2SupplierDetailDao, "CGTH");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, channel2SupplierDao, channel2SupplierGoodsDao, channel2SupplierDetailDao);
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
            Channel2SupplierPo po = channel2SupplierDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, channel2SupplierDao, channel2SupplierGoodsDao, channel2SupplierDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : channel2SupplierDetailDao.findByBillId(id)) {
                    stockChannelService.minus(detail, po.getChannelId());
                }
            }
        }
        channel2SupplierDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Channel2SupplierPo po = channel2SupplierDao.findById(id).get();
            billCommonService.unAudit(id, channel2SupplierDao, channel2SupplierGoodsDao, channel2SupplierDetailDao);
            for (BillDetailPo detail : channel2SupplierDetailDao.findByBillId(id)) {
                stockChannelService.add(detail, po.getChannelId());
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
        return billCommonService.findById(id, channel2SupplierDao, channel2SupplierGoodsDao, channel2SupplierDetailDao);
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

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, channel2SupplierGoodsDao, channel2SupplierDetailDao, "channel", "supplier", false);
    }

}
