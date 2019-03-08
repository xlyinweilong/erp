package com.yin.erp.bill.channelloss.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.bill.channelloss.dao.ChannelLossDao;
import com.yin.erp.bill.channelloss.dao.ChannelLossDetailDao;
import com.yin.erp.bill.channelloss.dao.ChannelLossGoodsDao;
import com.yin.erp.bill.channelloss.entity.po.ChannelLossPo;
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
import com.yin.erp.stock.service.StockChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 渠道损益
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ChannelLossService extends BillService {

    @Autowired
    private ChannelLossDao channelLossDao;
    @Autowired
    private ChannelLossGoodsDao channelLossGoodsDao;
    @Autowired
    private ChannelLossDetailDao channelLossDetailDao;
    @Autowired
    private StockChannelService stockChannelService;
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
        return billCommonService.findBillPage(vo, channelLossDao, new String[]{"channelName", "channelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new ChannelLossPo(), vo, userSessionBo, channelLossDao, channelLossGoodsDao, channelLossDetailDao, "QDSY", null, settlementDao);
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, channelLossDao, channelLossGoodsDao, channelLossDetailDao);
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
            ChannelLossPo po = channelLossDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, channelLossDao, channelLossGoodsDao, channelLossDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name()) || vo.getStatus().equals(BillStatusEnum.COMPLETE.name())) {
                for (BillDetailPo detail : channelLossDetailDao.findByBillId(id)) {
                    stockChannelService.minus(detail, po.getChannelId());
                }
            }
        }
        channelLossDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            ChannelLossPo po = channelLossDao.findById(id).get();
            billCommonService.unAudit(id, channelLossDao, channelLossGoodsDao, channelLossDetailDao);
            for (BillDetailPo detail : channelLossDetailDao.findByBillId(id)) {
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
        return billCommonService.findById(id, channelLossDao, channelLossGoodsDao, channelLossDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "cl");
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, channelLossGoodsDao, channelLossDetailDao, "channel", null, false);
    }

}
