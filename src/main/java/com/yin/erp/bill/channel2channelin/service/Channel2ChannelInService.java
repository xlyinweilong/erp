package com.yin.erp.bill.channel2channelin.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.channel2channelin.dao.Channel2ChannelInDao;
import com.yin.erp.bill.channel2channelin.dao.Channel2ChannelInDetailDao;
import com.yin.erp.bill.channel2channelin.dao.Channel2ChannelInGoodsDao;
import com.yin.erp.bill.channel2channelin.entity.po.Channel2ChannelInPo;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutDao;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutDetailDao;
import com.yin.erp.bill.channel2channelout.dao.Channel2ChannelOutGoodsDao;
import com.yin.erp.bill.channel2channelout.entity.po.Channel2ChannelOutPo;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.*;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.stock.service.StockChannelService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 渠道调入
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class Channel2ChannelInService extends BillService {

    @Autowired
    private Channel2ChannelInDao channel2ChannelInDao;
    @Autowired
    private Channel2ChannelInGoodsDao channel2ChannelInGoodsDao;
    @Autowired
    private Channel2ChannelInDetailDao channel2ChannelInDetailDao;
    @Autowired
    private Channel2ChannelOutDao channel2ChannelOutDao;
    @Autowired
    private Channel2ChannelOutGoodsDao channel2ChannelOutGoodsDao;
    @Autowired
    private Channel2ChannelOutDetailDao channel2ChannelOutDetailDao;
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
        return billCommonService.findBillPage(vo, channel2ChannelInDao, new String[]{"channelName", "channelCode", "toChannelName", "toChannelCode"});
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
        if (StringUtils.isNotBlank(vo.getId())) {
            //查询上游单据
            Channel2ChannelOutPo oldParent = channel2ChannelOutDao.findById(channel2ChannelInDao.findById(vo.getId()).get().getParentBillId()).get();
            oldParent.setChildBillId(null);
            oldParent.setStatus("AUDITED");
            channel2ChannelOutDao.save(oldParent);
        }
        BillPo billPo = billCommonService.save(new Channel2ChannelInPo(), vo, userSessionBo, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao, "QDDR");
        //查询上游单据
        Channel2ChannelOutPo newParent = channel2ChannelOutDao.findById(vo.getParentBillId()).get();
        newParent.setChildBillId(billPo.getId());
        newParent.setStatus("QUOTE");
        channel2ChannelOutDao.save(newParent);
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
            Channel2ChannelOutPo oldParent = channel2ChannelOutDao.findById(channel2ChannelInDao.findById(id).get().getParentBillId()).get();
            oldParent.setChildBillId(null);
            oldParent.setStatus("AUDITED");
            channel2ChannelOutDao.save(oldParent);
            billCommonService.deleteById(id, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao);
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
            Channel2ChannelInPo po = channel2ChannelInDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : channel2ChannelInDetailDao.findByBillId(id)) {
                    stockChannelService.add(detail, po.getToChannelId());
                }
                //修改上游单据状态
                Channel2ChannelOutPo parent = channel2ChannelOutDao.findById(po.getParentBillId()).get();
                parent.setStatus("COMPLETE");
                channel2ChannelOutDao.save(parent);
            }
        }
        channel2ChannelInDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            Channel2ChannelInPo po = channel2ChannelInDao.findById(id).get();
            billCommonService.unAudit(id, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao);
            for (BillDetailPo detail : channel2ChannelInDetailDao.findByBillId(id)) {
                stockChannelService.minus(detail, po.getToChannelId());
            }
            //修改上游单据状态
            Channel2ChannelOutPo parent = channel2ChannelOutDao.findById(po.getParentBillId()).get();
            parent.setStatus("QUOTE");
            channel2ChannelOutDao.save(parent);
        }
    }

    /**
     * 查询根据ID
     *
     * @param id
     * @return
     */
    public BillVo findById(String id) throws MessageException {
        return billCommonService.findById(id, channel2ChannelInDao, channel2ChannelInGoodsDao, channel2ChannelInDetailDao);
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
        billCommonService.export(vo, response, this, channel2ChannelInGoodsDao, channel2ChannelInDetailDao, "channel", "toChannel", true);
    }

    /**
     * 查询上级单据仓库出货
     *
     * @param code
     * @return
     */
    public Page<Channel2ChannelOutPo> findParentBill(String code) {
        //TODO 只查询未完成的单据
        Page<Channel2ChannelOutPo> page = channel2ChannelOutDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNull(root.get("childBillId")));
            predicates.add(criteriaBuilder.equal(root.get("status"), "AUDITED"));
            if (StringUtils.isNotBlank(code)) {
                predicates.add(criteriaBuilder.like(root.get("code"), "%" + code + "%"));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        }, PageRequest.of(0, 10, Sort.Direction.DESC, "createDate"));
        return page;
    }

    /**
     * 查询上级单据明细仓库出货
     *
     * @param id
     * @return
     * @throws MessageException
     */
    public List<BillGoodsVo> findParentGoodsList(String id) throws MessageException {
        List<BillGoodsVo> list = new ArrayList<>();
        List<BillGoodsPo> goodsList = channel2ChannelOutGoodsDao.findByBillId(id);
        List<BillDetailPo> detailList = channel2ChannelOutDetailDao.findByBillId(id);
        for (BillGoodsPo goodsPo : goodsList) {
            BillGoodsVo goodsVo = new BillGoodsVo();
            CopyUtil.copyProperties(goodsVo, goodsPo);
            List<BillDetailVo> detail = new ArrayList<>();
            for (BillDetailPo detailPo : detailList.stream().filter(d -> d.getBillGoodsId().equals(goodsPo.getId())).collect(Collectors.toList())) {
                BillDetailVo billDetailVo = new BillDetailVo();
                CopyUtil.copyProperties(billDetailVo, detailPo);
                billDetailVo.setColorId(detailPo.getGoodsColorId());
                billDetailVo.setSizeId(detailPo.getGoodsSizeId());
                detail.add(billDetailVo);
            }
            goodsVo.setDetail(detail);
            list.add(goodsVo);
        }
        return list;
    }

}
