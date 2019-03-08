package com.yin.erp.bill.channelinventory.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.bill.channelinventory.dao.ChannelInventoryDao;
import com.yin.erp.bill.channelinventory.dao.ChannelInventoryDetailDao;
import com.yin.erp.bill.channelinventory.dao.ChannelInventoryGoodsDao;
import com.yin.erp.bill.channelinventory.entity.po.ChannelInventoryPo;
import com.yin.erp.bill.channelloss.service.ChannelLossService;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.BillInventoryVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.service.BillCommonInventoryService;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private ChannelLossService channelLossService;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;
    @Autowired
    private BillCommonService billCommonService;
    @Autowired
    private BillCommonInventoryService billCommonInventoryService;

    /**
     * 查询
     *
     * @param vo
     * @return
     * @throws MessageException
     */
    @Override
    public BackPageVo<BillVo> findBillPage(SearchBillVo vo) throws MessageException {
        return billCommonService.findBillPage(vo, channelInventoryDao, new String[]{"channelName", "channelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new ChannelInventoryPo(), vo, userSessionBo, channelInventoryDao, channelInventoryGoodsDao, channelInventoryDetailDao, "QDPD");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, channelInventoryDao, channelInventoryGoodsDao, channelInventoryDetailDao);
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
            billCommonService.audit(id, vo, userSessionBo, d, channelInventoryDao, channelInventoryGoodsDao, channelInventoryDetailDao);
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
            billCommonService.unAudit(id, channelInventoryDao, channelInventoryGoodsDao, channelInventoryDetailDao);
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
        billCommonService.uploadBill(file, userSessionBo, this, "ci");
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, channelInventoryGoodsDao, channelInventoryDetailDao, "channel", null, false);
    }


    /**
     * 查询所有的可用结存时间
     *
     * @return
     */
    public List<String> loadInventoryDateList(String channelId) {
        return channelInventoryDao.findBillDate4Pd(channelId).stream().map(d -> d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).collect(Collectors.toList());
    }

    /**
     * 盘点
     *
     * @param vo
     * @param userSessionBo
     * @throws MessageException
     */
    public void inventory(BillInventoryVo vo, UserSessionBo userSessionBo) throws MessageException {
        billCommonInventoryService.inventory("CHANNEL", vo, userSessionBo, channelInventoryDao, channelInventoryDetailDao, channelLossService);
    }

}
