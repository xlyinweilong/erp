package com.yin.erp.bill.supplier2channel.service;


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
import com.yin.erp.bill.supplier2channel.dao.Supplier2ChannelDao;
import com.yin.erp.bill.supplier2channel.dao.Supplier2ChannelDetailDao;
import com.yin.erp.bill.supplier2channel.dao.Supplier2ChannelGoodsDao;
import com.yin.erp.bill.supplier2channel.entity.po.Supplier2ChannelPo;
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
 * 渠道收供应商
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class Supplier2ChannelService extends BillService {

    @Autowired
    private Supplier2ChannelDao supplier2ChannelDao;
    @Autowired
    private Supplier2ChannelGoodsDao supplier2ChannelGoodsDao;
    @Autowired
    private Supplier2ChannelDetailDao supplier2ChannelDetailDao;
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
        return billCommonService.findBillPage(vo, supplier2ChannelDao, new String[]{"supplierCode", "supplierName", "channelName", "channelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public void save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        billCommonService.save(new Supplier2ChannelPo(), vo, userSessionBo, supplier2ChannelDao, supplier2ChannelGoodsDao, supplier2ChannelDetailDao, "CKTH");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            supplier2ChannelDetailDao.deleteAllByBillId(id);
            supplier2ChannelGoodsDao.deleteAllByBillId(id);
            supplier2ChannelDao.deleteById(id);
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
            Supplier2ChannelPo po = supplier2ChannelDao.findById(id).get();
            po.setAuditUserId(userSessionBo.getId());
            po.setAuditUserName(userSessionBo.getName());
            po.setStatus(vo.getStatus());
            po.setAuditDate(d);
            supplier2ChannelDao.save(po);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : supplier2ChannelDetailDao.findByBillId(id)) {
                    stockChannelService.add(detail, po.getChannelId());
                }
            }
        }
        supplier2ChannelDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Supplier2ChannelPo po = supplier2ChannelDao.findById(id).get();
            po.setStatus("AUDIT_FAILURE");
            supplier2ChannelDao.save(po);
            for (BillDetailPo detail : supplier2ChannelDetailDao.findByBillId(id)) {
                stockChannelService.minus(detail, po.getChannelId());
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
        return billCommonService.findById(id, supplier2ChannelDao, supplier2ChannelGoodsDao, supplier2ChannelDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "s2c");
    }

    @Override
    public String uploadBillSupplierCode(Row row) throws MessageException {
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
        billCommonService.export(vo, response, this, supplier2ChannelGoodsDao, supplier2ChannelDetailDao, "supplier", "channel", false);
    }

}
