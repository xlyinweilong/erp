package com.yin.erp.bill.delivery.service;


import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.exceptions.MessageException;
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
import com.yin.erp.bill.delivery.entity.po.DeliveryPo;
import com.yin.erp.bill.order.dao.OrderDao;
import com.yin.erp.bill.order.dao.OrderDetailDao;
import com.yin.erp.bill.order.dao.OrderGoodsDao;
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
 * 配货单服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class DeliveryService extends BillService {

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
        return billCommonService.findBillPage(vo, deliveryDao, new String[]{"channelName", "channelCode", "warehouseName", "warehouseCode", "parentBillCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        DeliveryPo po = (DeliveryPo) billCommonService.save(new DeliveryPo(), vo, userSessionBo, deliveryDao, deliveryGoodsDao, deliveryDetailDao, "PHD", this.findParentGoodsList(vo.getParentBillId()), orderDao);
        return po;
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            DeliveryPo po = deliveryDao.findById(id).get();
            billCommonService.deleteById(id, deliveryDao, deliveryGoodsDao, deliveryDetailDao);
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
            DeliveryPo po = deliveryDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, date, deliveryDao, deliveryGoodsDao, deliveryDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                //减少上游数量
                if (StringUtils.isNotBlank(po.getParentBillId())) {
                    billCommonService.cutManyToOneCount(po, deliveryDetailDao, orderDao, orderDetailDao);
                }
            }

        }
        deliveryDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            DeliveryPo po = deliveryDao.findById(id).get();
            billCommonService.unAudit(id, deliveryDao, deliveryGoodsDao, deliveryDetailDao);
            if (StringUtils.isNotBlank(po.getParentBillId())) {
                //释放引用数量
                billCommonService.freeManyToOneCount(po, deliveryDetailDao, orderDao, orderDetailDao);
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
        return billCommonService.findById(id, deliveryDao, deliveryGoodsDao, deliveryDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "delivery", orderDao);
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, deliveryGoodsDao, deliveryDetailDao, "warehouse", "channel", true);
    }

    /**
     * 查询上游单据
     *
     * @param code
     * @return
     */
    public Page<BillPo> findParentBill(String code) {
        return billCommonService.findParentBill(code, orderDao, new String[]{"channelName", "channelCode", "warehouseName", "warehouseCode"});
    }

    /**
     * 查询上级单据明细
     *
     * @param id
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findParentGoodsList(String id) throws MessageException {
        return billCommonService.findParentGoodsList(id, orderGoodsDao, orderDetailDao);
    }

}
