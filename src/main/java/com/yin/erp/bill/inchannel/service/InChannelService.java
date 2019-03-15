package com.yin.erp.bill.inchannel.service;


import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.exceptions.MessageException;
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
import com.yin.erp.bill.inchannel.dao.InChannelDao;
import com.yin.erp.bill.inchannel.dao.InChannelDetailDao;
import com.yin.erp.bill.inchannel.dao.InChannelGoodsDao;
import com.yin.erp.bill.inchannel.entity.po.InChannelPo;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDetailDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelGoodsDao;
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
 * 渠道收货
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class InChannelService extends BillService {

    @Autowired
    private InChannelDao inChannelDao;
    @Autowired
    private InChannelGoodsDao inChannelGoodsDao;
    @Autowired
    private InChannelDetailDao inChannelDetailDao;
    @Autowired
    private StockChannelService stockChannelService;
    @Autowired
    private Warehouse2ChannelDao warehouse2ChannelDao;
    @Autowired
    private Warehouse2ChannelGoodsDao warehouse2ChannelGoodsDao;
    @Autowired
    private Warehouse2ChannelDetailDao warehouse2ChannelDetailDao;
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
        return billCommonService.findBillPage(vo, inChannelDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode", "parentBillCode"});
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
        billCommonService.changeOneToOneParentStatus(vo.getId(), BillStatusEnum.AUDITED, inChannelDao, warehouse2ChannelDao, null);
        BillPo billPo = billCommonService.save(new InChannelPo(), vo, userSessionBo, inChannelDao, inChannelGoodsDao, inChannelDetailDao, "QDSH", this.findParentGoodsList(vo.getParentBillId()), warehouse2ChannelDao);
        //修改上游单据
        billCommonService.changeOneToOneParentStatus(billPo.getId(), BillStatusEnum.QUOTE, inChannelDao, warehouse2ChannelDao, billPo.getId());
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
            billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.AUDITED, inChannelDao, warehouse2ChannelDao, null);
            billCommonService.deleteById(id, inChannelDao, inChannelGoodsDao, inChannelDetailDao);
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
            InChannelPo po = inChannelDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, inChannelDao, inChannelGoodsDao, inChannelDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : inChannelDetailDao.findByBillId(id)) {
                    stockChannelService.add(detail, po.getChannelId());
                    //修改上游单据数量 TODO
                }
                //修改上游单据状态
                billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.COMPLETE, inChannelDao, warehouse2ChannelDao);
            }
        }
        inChannelDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            InChannelPo po = inChannelDao.findById(id).get();
            billCommonService.unAudit(id, inChannelDao, inChannelGoodsDao, inChannelDetailDao);
            for (BillDetailPo detail : inChannelDetailDao.findByBillId(id)) {
                stockChannelService.minus(detail, po.getChannelId());
                //修改上游单据数量 TODO
            }
            //修改上游单据状态
            billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.QUOTE, inChannelDao, warehouse2ChannelDao);
        }
    }


    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        return billCommonService.findById(id, inChannelDao, inChannelGoodsDao, inChannelDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "inc", warehouse2ChannelDao);
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, inChannelGoodsDao, inChannelDetailDao, "channel", "warehouse", true);
    }


    /**
     * 查询上级单据仓库出货
     *
     * @param code
     * @return
     */
    public Page<BillPo> findParentBill(String code) {
        return billCommonService.findParentBill(code, warehouse2ChannelDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode"});
    }

    /**
     * 查询上级单据明细仓库出货
     *
     * @param id
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findParentGoodsList(String id) throws MessageException {
        return billCommonService.findParentGoodsList(id, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao);
    }

}
