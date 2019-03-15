package com.yin.erp.bill.inwarehouse.service;


import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseDao;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseDetailDao;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseGoodsDao;
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
import com.yin.erp.bill.inwarehouse.dao.InWarehouseDao;
import com.yin.erp.bill.inwarehouse.dao.InWarehouseDetailDao;
import com.yin.erp.bill.inwarehouse.dao.InWarehouseGoodsDao;
import com.yin.erp.bill.inwarehouse.entity.po.InWarehousePo;
import com.yin.erp.stock.service.StockWarehouseService;
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
 * 仓库收退货
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class InWarehouseService extends BillService {

    @Autowired
    private InWarehouseDao inWarehouseDao;
    @Autowired
    private InWarehouseGoodsDao inWarehouseGoodsDao;
    @Autowired
    private InWarehouseDetailDao inWarehouseDetailDao;
    @Autowired
    private StockWarehouseService stockWarehouseService;
    @Autowired
    private Channel2WarehouseDao channel2WarehouseDao;
    @Autowired
    private Channel2WarehouseGoodsDao channel2WarehouseGoodsDao;
    @Autowired
    private Channel2WarehouseDetailDao channel2WarehouseDetailDao;
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
        return billCommonService.findBillPage(vo, inWarehouseDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode","parentBillCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        //如果是修改，删除原来的上游引用s
        billCommonService.changeOneToOneParentStatus(vo.getId(), BillStatusEnum.AUDITED, inWarehouseDao, channel2WarehouseDao, null);
        BillPo billPo = billCommonService.save(new InWarehousePo(), vo, userSessionBo, inWarehouseDao, inWarehouseGoodsDao, inWarehouseDetailDao, "CKSTH", this.findParentGoodsList(vo.getParentBillId()), channel2WarehouseDao);
        //修改上游单据
        billCommonService.changeOneToOneParentStatus(billPo.getId(), BillStatusEnum.QUOTE, inWarehouseDao, channel2WarehouseDao, billPo.getId());
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
            billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.AUDITED, inWarehouseDao, channel2WarehouseDao, null);
            billCommonService.deleteById(id, inWarehouseDao, inWarehouseGoodsDao, inWarehouseDetailDao);
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
            InWarehousePo po = inWarehouseDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, inWarehouseDao, inWarehouseGoodsDao, inWarehouseDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : inWarehouseDetailDao.findByBillId(id)) {
                    stockWarehouseService.add(detail, po.getWarehouseId());
                    //修改上游单据数量 TODO
                }
                //修改上游单据状态
                billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.COMPLETE, inWarehouseDao, channel2WarehouseDao);
            }

        }
        inWarehouseDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            InWarehousePo po = inWarehouseDao.findById(id).get();
            billCommonService.unAudit(id, inWarehouseDao, inWarehouseGoodsDao, inWarehouseDetailDao);
            for (BillDetailPo detail : inWarehouseDetailDao.findByBillId(id)) {
                stockWarehouseService.minus(detail, po.getWarehouseId());
                //修改上游单据数量 TODO
            }
            //修改上游单据状态
            billCommonService.changeOneToOneParentStatus(id, BillStatusEnum.QUOTE, inWarehouseDao, channel2WarehouseDao);
        }
    }


    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        return billCommonService.findById(id, inWarehouseDao, inWarehouseGoodsDao, inWarehouseDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "inw", channel2WarehouseDao);
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, inWarehouseGoodsDao, inWarehouseDetailDao, "warehouse", "channel", true);
    }


    /**
     * 查询上级单据仓库出货
     *
     * @param code
     * @return
     */
    public Page<BillPo> findParentBill(String code) {
        return billCommonService.findParentBill(code, channel2WarehouseDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode"});
    }

    /**
     * 查询上级单据明细仓库出货
     *
     * @param id
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findParentGoodsList(String id) throws MessageException {
        return billCommonService.findParentGoodsList(id, channel2WarehouseGoodsDao, channel2WarehouseDetailDao);
    }

}
