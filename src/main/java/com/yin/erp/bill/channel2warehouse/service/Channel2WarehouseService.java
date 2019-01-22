package com.yin.erp.bill.channel2warehouse.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseDao;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseDetailDao;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseGoodsDao;
import com.yin.erp.bill.channel2warehouse.entity.po.Channel2WarehousePo;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.stock.service.StockChannelService;
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
public class Channel2WarehouseService extends BillService {

    @Autowired
    private Channel2WarehouseDao channel2WarehouseDao;
    @Autowired
    private Channel2WarehouseGoodsDao channel2WarehouseGoodsDao;
    @Autowired
    private Channel2WarehouseDetailDao channel2WarehouseDetailDao;
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
        return billCommonService.findBillPage(vo, channel2WarehouseDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public void save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        billCommonService.save(new Channel2WarehousePo(), vo, userSessionBo, channel2WarehouseDao, channel2WarehouseGoodsDao, channel2WarehouseDetailDao, "QDTH");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            channel2WarehouseDetailDao.deleteAllByBillId(id);
            channel2WarehouseGoodsDao.deleteAllByBillId(id);
            channel2WarehouseDao.deleteById(id);
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
            Channel2WarehousePo po = channel2WarehouseDao.findById(id).get();
            po.setAuditUserId(userSessionBo.getId());
            po.setAuditUserName(userSessionBo.getName());
            po.setStatus(vo.getStatus());
            po.setAuditDate(d);
            channel2WarehouseDao.save(po);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : channel2WarehouseDetailDao.findByBillId(id)) {
                    stockChannelService.minus(detail, po.getChannelId());
                }
            }
        }
        channel2WarehouseDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Channel2WarehousePo po = channel2WarehouseDao.findById(id).get();
            po.setStatus("AUDIT_FAILURE");
            channel2WarehouseDao.save(po);
            for (BillDetailPo detail : channel2WarehouseDetailDao.findByBillId(id)) {
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
        return billCommonService.findById(id, channel2WarehouseDao, channel2WarehouseGoodsDao, channel2WarehouseDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "c2w");
    }

    @Override
    public String uploadBillChannelCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(1));
    }

    @Override
    public String uploadBillWarehouseCode(Row row) throws MessageException {
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
        billCommonService.export(vo, response, this, channel2WarehouseGoodsDao, channel2WarehouseDetailDao, "channel", "warehouse", false);
    }


}
