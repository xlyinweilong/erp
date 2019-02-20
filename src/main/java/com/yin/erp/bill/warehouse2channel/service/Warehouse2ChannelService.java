package com.yin.erp.bill.warehouse2channel.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.*;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.inchannel.service.InChannelService;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDetailDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelGoodsDao;
import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelPo;
import com.yin.erp.config.sysconfig.service.ConfigService;
import com.yin.erp.stock.service.StockWarehouseService;
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
public class Warehouse2ChannelService extends BillService {

    @Autowired
    private Warehouse2ChannelDao warehouse2ChannelDao;
    @Autowired
    private Warehouse2ChannelGoodsDao warehouse2ChannelGoodsDao;
    @Autowired
    private Warehouse2ChannelDetailDao warehouse2ChannelDetailDao;
    @Autowired
    private StockWarehouseService stockWarehouseService;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;
    @Autowired
    private BillCommonService billCommonService;
    @Autowired
    private ConfigService configService;
    @Autowired
    private InChannelService inChannelService;


    /**
     * 查询
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @Override
    public BackPageVo<BillVo> findBillPage(SearchBillVo vo) throws MessageException {
        return billCommonService.findBillPage(vo, warehouse2ChannelDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new Warehouse2ChannelPo(), vo, userSessionBo, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao, "CKCH");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao);
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
            Warehouse2ChannelPo po = warehouse2ChannelDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : warehouse2ChannelDetailDao.findByBillId(id)) {
                    stockWarehouseService.minus(detail, po.getWarehouseId());
                }
                //自动生成调入
                if (configService.getChannelConfigValue("channel_ckch_auto_qdsh", po.getChannelId()) == 0) {
                    BillVo billVo = new BillVo();
                    billVo.setStatus(BillStatusEnum.PENDING.name());
                    billVo.setBillDate(po.getBillDate());
                    billVo.setParentBillCode(po.getCode());
                    billVo.setParentBillId(po.getId());
                    billVo.setChannelName(po.getChannelName());
                    billVo.setChannelCode(po.getChannelCode());
                    billVo.setChannelId(po.getChannelId());
                    billVo.setWarehouseName(po.getWarehouseName());
                    billVo.setWarehouseId(po.getWarehouseId());
                    billVo.setWarehouseCode(po.getWarehouseCode());
                    List<BillGoodsVo> billGoodsVoList = new ArrayList<>();
                    List<BillGoodsPo> goodsListPo = warehouse2ChannelGoodsDao.findByBillId(po.getId());
                    List<BillDetailPo> detailListPo = warehouse2ChannelDetailDao.findByBillId(po.getId());
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
                    BillPo billPo = inChannelService.save(billVo, userSessionBo);
                    BaseAuditVo baseAuditVo = new BaseAuditVo();
                    baseAuditVo.setIds(Arrays.asList(billPo.getId()));
                    baseAuditVo.setStatus(BillStatusEnum.AUDITED.name());
                    inChannelService.audit(baseAuditVo, userSessionBo);
                }

            }
        }
        warehouse2ChannelDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Warehouse2ChannelPo po = warehouse2ChannelDao.findById(id).get();
            billCommonService.unAudit(id, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao);
            for (BillDetailPo detail : warehouse2ChannelDetailDao.findByBillId(id)) {
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
        return billCommonService.findById(id, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "w2c");
    }

    @Override
    public String uploadBillWarehouseCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(1));
    }


    @Override
    public String uploadBillChannelCode(Row row) throws MessageException {
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
        billCommonService.export(vo, response, this, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao, "warehouse", "channel", false);
    }


}
