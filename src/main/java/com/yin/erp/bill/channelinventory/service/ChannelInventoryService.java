package com.yin.erp.bill.channelinventory.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.channelinventory.dao.ChannelInventoryDao;
import com.yin.erp.bill.channelinventory.dao.ChannelInventoryDetailDao;
import com.yin.erp.bill.channelinventory.dao.ChannelInventoryGoodsDao;
import com.yin.erp.bill.channelinventory.entity.po.ChannelInventoryPo;
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
 * 渠道损益
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class ChannelInventoryService extends BillService {

    @Autowired
    private ChannelInventoryDao channelInventoryDao;
    @Autowired
    private ChannelInventoryGoodsDao channelInventoryGoodsDao;
    @Autowired
    private ChannelInventoryDetailDao channelInventoryDetailDao;
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
        return billCommonService.findBillPage(vo, channelInventoryDao, new String[]{"channelName", "channelCode", "channelName", "channelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public void save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        billCommonService.save(new ChannelInventoryPo(), vo, userSessionBo, channelInventoryDao, channelInventoryGoodsDao, channelInventoryDetailDao, "QDDR");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            channelInventoryDetailDao.deleteAllByBillId(id);
            channelInventoryGoodsDao.deleteAllByBillId(id);
            channelInventoryDao.deleteById(id);
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
            ChannelInventoryPo po = channelInventoryDao.findById(id).get();
            po.setAuditUserId(userSessionBo.getId());
            po.setAuditUserName(userSessionBo.getName());
            po.setStatus(vo.getStatus());
            po.setAuditDate(d);
            channelInventoryDao.save(po);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : channelInventoryDetailDao.findByBillId(id)) {
                    stockChannelService.minus(detail, po.getChannelId());
                }
            }
        }
        channelInventoryDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            ChannelInventoryPo po = channelInventoryDao.findById(id).get();
            po.setStatus("AUDIT_FAILURE");
            channelInventoryDao.save(po);
            for (BillDetailPo detail : channelInventoryDetailDao.findByBillId(id)) {
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
        return billCommonService.findById(id, channelInventoryDao, channelInventoryGoodsDao, channelInventoryDetailDao);
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
        billCommonService.export(vo, response, this, channelInventoryGoodsDao, channelInventoryDetailDao, "channel", "toChannel", true);
    }

}
