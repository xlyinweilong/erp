package com.yin.erp.bill.noticechannel2channelout.service;


import com.yin.common.entity.bo.UserSessionBo;
import com.yin.common.entity.vo.in.BaseDeleteVo;
import com.yin.common.entity.vo.out.BackPageVo;
import com.yin.common.exceptions.MessageException;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.BaseAuditVo;
import com.yin.erp.bill.common.entity.vo.in.BaseBillExportVo;
import com.yin.erp.bill.common.entity.vo.in.SearchBillVo;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.noticechannel2channelout.dao.NoticeChannel2ChannelOutDao;
import com.yin.erp.bill.noticechannel2channelout.dao.NoticeChannel2ChannelOutDetailDao;
import com.yin.erp.bill.noticechannel2channelout.dao.NoticeChannel2ChannelOutGoodsDao;
import com.yin.erp.bill.noticechannel2channelout.entity.po.NoticeChannel2ChannelOutPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 通知单-店铺调出服务
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class NoticeChannel2ChannelOutService extends BillService {

    @Autowired
    private NoticeChannel2ChannelOutDao noticeChannel2ChannelOutDao;
    @Autowired
    private NoticeChannel2ChannelOutGoodsDao noticeChannel2ChannelOutGoodsDao;
    @Autowired
    private NoticeChannel2ChannelOutDetailDao noticeChannel2ChannelOutDetailDao;
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
        return billCommonService.findBillPage(vo, noticeChannel2ChannelOutDao, new String[]{"channelName", "channelCode", "toChannelName", "toChannelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public BillPo save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        return billCommonService.save(new NoticeChannel2ChannelOutPo(), vo, userSessionBo, noticeChannel2ChannelOutDao, noticeChannel2ChannelOutGoodsDao, noticeChannel2ChannelOutDetailDao, "TQDDC");
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            billCommonService.deleteById(id, noticeChannel2ChannelOutDao, noticeChannel2ChannelOutGoodsDao, noticeChannel2ChannelOutDetailDao);
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
            NoticeChannel2ChannelOutPo po = noticeChannel2ChannelOutDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, noticeChannel2ChannelOutDao, noticeChannel2ChannelOutGoodsDao, noticeChannel2ChannelOutDetailDao);
        }
        noticeChannel2ChannelOutDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            NoticeChannel2ChannelOutPo po = noticeChannel2ChannelOutDao.findById(id).get();
            billCommonService.unAudit(id, noticeChannel2ChannelOutDao, noticeChannel2ChannelOutGoodsDao, noticeChannel2ChannelOutDetailDao);
        }
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        return billCommonService.findById(id, noticeChannel2ChannelOutDao, noticeChannel2ChannelOutGoodsDao, noticeChannel2ChannelOutDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "nc2cout");
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, noticeChannel2ChannelOutGoodsDao, noticeChannel2ChannelOutDetailDao, "channel", "toChannel", false);
    }

}
