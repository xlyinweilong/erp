package com.yin.erp.bill.channel2channelout.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.channel2channelin.service.Channel2ChannelInService;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutDao;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutDetailDao;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutGoodsDao;
import com.yin.erp.bill.channel2channelout.entity.po.Channel2ChannelOutPo;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.*;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.config.sysconfig.service.ConfigService;
import com.yin.erp.stock.service.StockChannelService;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 仓库出货
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class Channel2ChannelOutService extends BillService {

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
    @Autowired
    private ConfigService configService;
    @Autowired
    private Channel2ChannelInService channel2ChannelInService;

    /**
     * 查询
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @Override
    public BackPageVo<BillVo> findBillPage(SearchBillVo vo) throws MessageException {
        return billCommonService.findBillPage(vo, channel2ChannelOutDao, new String[]{"channelName", "channelCode", "toChannelName", "toChannelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new Channel2ChannelOutPo(), vo, userSessionBo, channel2ChannelOutDao, channel2ChannelOutGoodsDao, channel2ChannelOutDetailDao, "QDDC");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, channel2ChannelOutDao, channel2ChannelOutGoodsDao, channel2ChannelOutDetailDao);
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
            Channel2ChannelOutPo po = channel2ChannelOutDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, channel2ChannelOutDao, channel2ChannelOutGoodsDao, channel2ChannelOutDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : channel2ChannelOutDetailDao.findByBillId(id)) {
                    stockChannelService.minus(detail, po.getChannelId());
                }
                //自动生成调入
                if (configService.getChannelConfigValue("channel_qddc_auto_qddu", po.getToChannelId()) == 0) {
                    BillVo billVo = new BillVo();
                    billVo.setStatus(BillStatusEnum.PENDING.name());
                    billVo.setBillDate(po.getBillDate());
                    billVo.setParentBillCode(po.getCode());
                    billVo.setParentBillId(po.getId());
                    billVo.setChannelName(po.getChannelName());
                    billVo.setChannelCode(po.getChannelCode());
                    billVo.setChannelId(po.getChannelId());
                    billVo.setToChannelCode(po.getToChannelCode());
                    billVo.setToChannelId(po.getToChannelId());
                    billVo.setToChannelName(po.getToChannelName());
                    List<BillGoodsVo> billGoodsVoList = new ArrayList<>();
                    List<BillGoodsPo> goodsListPo = channel2ChannelOutGoodsDao.findByBillId(po.getId());
                    List<BillDetailPo> detailListPo = channel2ChannelOutDetailDao.findByBillId(po.getId());
                    for (BillGoodsPo billGoodsPo : goodsListPo) {
                        BillGoodsVo billGoodsVo = new BillGoodsVo();
                        billGoodsVo.setGoodsCode(billGoodsPo.getGoodsCode());
                        billGoodsVo.setTagPrice(billGoodsPo.getTagPrice());
                        billGoodsVo.setPrice(billGoodsPo.getPrice());
                        billGoodsVo.setGoodsName(billGoodsPo.getGoodsName());
                        billGoodsVo.setGoodsId(billGoodsPo.getGoodsId());
                        List<BillDetailVo> billDetailVoList = new ArrayList<>();
                        detailListPo.stream().filter(dp -> dp.getGoodsId().equals(billGoodsPo.getGoodsId())).forEach(dp -> {
                            BillDetailVo billDetailVo = new BillDetailVo();
                            billDetailVo.setBillCount(dp.getBillCount());
                            billDetailVo.setSizeId(dp.getGoodsSizeId());
                            billDetailVo.setColorId(dp.getGoodsColorId());
                            billDetailVoList.add(billDetailVo);
                        });
                        billGoodsVo.setDetail(billDetailVoList);
                        billGoodsVoList.add(billGoodsVo);
                    }
                    billVo.setGoodsList(billGoodsVoList);
                    BillPo billPo = channel2ChannelInService.save(billVo, userSessionBo);
                    BaseAuditVo baseAuditVo = new BaseAuditVo();
                    baseAuditVo.setIds(Arrays.asList(billPo.getId()));
                    baseAuditVo.setStatus(BillStatusEnum.AUDITED.name());
                    channel2ChannelInService.audit(baseAuditVo, userSessionBo);
                }
            }
        }
        channel2ChannelOutDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Channel2ChannelOutPo po = channel2ChannelOutDao.findById(id).get();
            billCommonService.unAudit(id, channel2ChannelOutDao, channel2ChannelOutGoodsDao, channel2ChannelOutDetailDao);
            for (BillDetailPo detail : channel2ChannelOutDetailDao.findByBillId(id)) {
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
        return billCommonService.findById(id, channel2ChannelOutDao, channel2ChannelOutGoodsDao, channel2ChannelOutDetailDao);
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
    public String uploadBillChannelCode(Row row) throws MessageException {
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
        billCommonService.export(vo, response, this, channel2ChannelOutGoodsDao, channel2ChannelOutDetailDao, "channel", "supplier", false);
    }

}
