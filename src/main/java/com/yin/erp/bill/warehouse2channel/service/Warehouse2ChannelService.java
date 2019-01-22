package com.yin.erp.bill.warehouse2channel.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDetailDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelGoodsDao;
import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelPo;
import com.yin.erp.stock.service.StockWarehouseService;
import org.apache.poi.ss.usermodel.Row;
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
    public void save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        billCommonService.save(new Warehouse2ChannelPo(), vo, userSessionBo, warehouse2ChannelDao, warehouse2ChannelGoodsDao, warehouse2ChannelDetailDao, "CKCH");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            warehouse2ChannelDetailDao.deleteAllByBillId(id);
            warehouse2ChannelGoodsDao.deleteAllByBillId(id);
            warehouse2ChannelDao.deleteById(id);
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
            po.setAuditUserId(userSessionBo.getId());
            po.setAuditUserName(userSessionBo.getName());
            po.setStatus(vo.getStatus());
            po.setAuditDate(d);
            warehouse2ChannelDao.save(po);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : warehouse2ChannelDetailDao.findByBillId(id)) {
                    stockWarehouseService.minus(detail, po.getWarehouseId());
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
            po.setStatus("AUDIT_FAILURE");
            warehouse2ChannelDao.save(po);
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
