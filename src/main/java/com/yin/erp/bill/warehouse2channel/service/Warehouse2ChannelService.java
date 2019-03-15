package com.yin.erp.bill.warehouse2channel.service;


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
import com.yin.erp.bill.delivery.dao.DeliveryDao;
import com.yin.erp.bill.delivery.dao.DeliveryDetailDao;
import com.yin.erp.bill.delivery.dao.DeliveryGoodsDao;
import com.yin.erp.bill.inchannel.service.InChannelService;
import com.yin.erp.bill.order.dao.OrderDao;
import com.yin.erp.bill.order.dao.OrderDetailDao;
import com.yin.erp.bill.order.dao.OrderGoodsDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDetailDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelGoodsDao;
import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelPo;
import com.yin.erp.config.sysconfig.service.ConfigService;
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
    private DeliveryDao deliveryDao;
    @Autowired
    private DeliveryGoodsDao deliveryGoodsDao;
    @Autowired
    private DeliveryDetailDao deliveryDetailDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderGoodsDao orderGoodsDao;
    @Autowired
    private OrderDetailDao orderDetailDao;
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
        return billCommonService.findBillPage(vo, warehouse2ChannelDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode", "parentBillCode", "grandParentBillCode"});
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
        billCommonService.changeOneToOneParentStatus(vo.getId(), BillStatusEnum.AUDITED, warehouse2ChannelDao, deliveryDao, null);
        BillPo billPo = billCommonService.save(new Warehouse2ChannelPo(), vo, userSessionBo, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao, "CKCH", this.findParentGoodsList(vo.getParentBillId()), deliveryDao);
        //修改上游单据
        billCommonService.changeOneToOneParentStatus(billPo.getId(), BillStatusEnum.QUOTE, warehouse2ChannelDao, deliveryDao, billPo.getId());
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
            billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.AUDITED, warehouse2ChannelDao, deliveryDao, null);
            billCommonService.deleteById(id, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao);
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
            Warehouse2ChannelPo po = warehouse2ChannelDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, date, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : warehouse2ChannelDetailDao.findByBillId(id)) {
                    stockWarehouseService.minus(detail, po.getWarehouseId());
                }
                //修改上游单据状态
                billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.COMPLETE, warehouse2ChannelDao, deliveryDao);
                //减少上上游数量
                if (StringUtils.isNotBlank(po.getGrandParentBillId()) && StringUtils.isBlank(po.getParentBillId())) {
                    billCommonService.cutManyToOneCount(po, warehouse2ChannelDetailDao, orderDao, orderDetailDao);
                }
                //自动生成调入
                if (configService.getChannelConfigValue("channel_ckch_auto_qdsh", po.getChannelId()) == 0) {
                    billCommonService.billCreateSubBill(po, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao, inChannelService, userSessionBo);
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
            //修改上游单据状态
            billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.QUOTE, warehouse2ChannelDao, deliveryDao);
            if (StringUtils.isNotBlank(po.getGrandParentBillId()) && StringUtils.isBlank(po.getParentBillId())) {
                //释放引用数量
                billCommonService.freeManyToOneCount(po, warehouse2ChannelDetailDao, orderDao, orderDetailDao);
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
        billCommonService.uploadBill(file, userSessionBo, this, "w2c", deliveryDao, orderDao);
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

    /**
     * 查询上级单据
     *
     * @param code
     * @return
     */
    public Page<BillPo> findParentBill(String code) {
        return billCommonService.findParentBill(code, deliveryDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode"});
    }

    /**
     * 查询上级单据明细
     *
     * @param id
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findParentGoodsList(String id) throws MessageException {
        return billCommonService.findParentGoodsList(id, deliveryGoodsDao, deliveryDetailDao);
    }

    /**
     * 查询上上级单据
     *
     * @param code
     * @return
     */
    public Page<BillPo> findGrandParentBill(String code) {
        return billCommonService.findParentBill(code, orderDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode"});
    }

    /**
     * 查询上上级单据明细
     *
     * @param id
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findGrandParentGoodsList(String id) throws MessageException {
        return billCommonService.findParentGoodsList(id, orderGoodsDao, orderDetailDao);
    }

}
