package com.yin.erp.bill.channel2channelin.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.bill.channel2channelin.dao.Channel2ChannelInDao;
import com.yin.erp.bill.channel2channelin.dao.Channel2ChannelInDetailDao;
import com.yin.erp.bill.channel2channelin.dao.Channel2ChannelInGoodsDao;
import com.yin.erp.bill.channel2channelin.entity.po.Channel2ChannelInPo;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutDao;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutDetailDao;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutGoodsDao;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.BillGoodsVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.stock.service.StockChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * 渠道调入
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class Channel2ChannelInService extends BillService {

    @Autowired
    private Channel2ChannelInDao channel2ChannelInDao;
    @Autowired
    private Channel2ChannelInGoodsDao channel2ChannelInGoodsDao;
    @Autowired
    private Channel2ChannelInDetailDao channel2ChannelInDetailDao;
    @Autowired
    private Channel2ChannelOutDao channel2ChannelOutDao;
    @Autowired
    private Channel2ChannelOutGoodsDao channel2ChannelOutGoodsDao;
    @Autowired
    private Channel2ChannelOutDetailDao channel2ChannelOutDetailDao;
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
        return billCommonService.findBillPage(vo, channel2ChannelInDao, new String[]{"channelName", "channelCode", "toChannelName", "toChannelCode", "parentBillCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        //如果是修改，删除原来的上游引用
        billCommonService.changeOneToOneParentStatus(vo.getId(), BillStatusEnum.AUDITED, channel2ChannelInDao, channel2ChannelOutDao, null);
        BillPo billPo = billCommonService.save(new Channel2ChannelInPo(), vo, userSessionBo, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao, "QDDR", this.findParentGoodsList(vo.getParentBillId()), channel2ChannelOutDao);
        //修改上游单据状态
        billCommonService.changeOneToOneParentStatus(billPo.getId(), BillStatusEnum.QUOTE, channel2ChannelInDao, channel2ChannelOutDao, billPo.getId());
        return billPo;
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            //删除上游引用
            billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.AUDITED, channel2ChannelInDao, channel2ChannelOutDao, null);
            billCommonService.deleteById(id, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao);
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
            Channel2ChannelInPo po = channel2ChannelInDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : channel2ChannelInDetailDao.findByBillId(id)) {
                    stockChannelService.add(detail, po.getToChannelId());
                }
                //修改上游单据状态
                billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.COMPLETE, channel2ChannelInDao, channel2ChannelOutDao);
            }
        }
        channel2ChannelInDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Channel2ChannelInPo po = channel2ChannelInDao.findById(id).get();
            billCommonService.unAudit(id, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao);
            for (BillDetailPo detail : channel2ChannelInDetailDao.findByBillId(id)) {
                stockChannelService.minus(detail, po.getToChannelId());
            }
            //修改上游单据状态
            billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.QUOTE, channel2ChannelInDao, channel2ChannelOutDao);
        }
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        return billCommonService.findById(id, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "c2cin", channel2ChannelOutDao);
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, channel2ChannelInGoodsDao, channel2ChannelInDetailDao, "channel", "toChannel", true);
    }

    /**
     * 查询上级单据仓库出货
     *
     * @param code
     * @return
     */
    public Page<BillPo> findParentBill(String code) {
        return billCommonService.findParentBill(code, channel2ChannelOutDao, new String[]{"channelName", "channelCode", "toChannelName", "toChannelCode"});
    }

    /**
     * 查询上级单据明细仓库出货
     *
     * @param id
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findParentGoodsList(String id) throws MessageException {
        return billCommonService.findParentGoodsList(id, channel2ChannelOutGoodsDao, channel2ChannelOutDetailDao);
    }

}
