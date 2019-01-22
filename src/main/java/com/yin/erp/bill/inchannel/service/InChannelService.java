package com.yin.erp.bill.inchannel.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.*;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.inchannel.dao.InChannelDao;
import com.yin.erp.bill.inchannel.dao.InChannelDetailDao;
import com.yin.erp.bill.inchannel.dao.InChannelGoodsDao;
import com.yin.erp.bill.inchannel.entity.po.InChannelPo;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelDetailDao;
import com.yin.erp.bill.warehouse2channel.dao.Warehouse2ChannelGoodsDao;
import com.yin.erp.bill.warehouse2channel.entity.po.Warehouse2ChannelPo;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 渠道收货
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class InChannelService extends BillService {

    @Autowired
    private InChannelDao inChannelDao;
    @Autowired
    private InChannelGoodsDao inChannelGoodsDao;
    @Autowired
    private InChannelDetailDao inChannelDetailDao;
    @Autowired
    private StockChannelService stockChannelService;
    @Autowired
    private Warehouse2ChannelDao warehouse2ChannelDao;
    @Autowired
    private Warehouse2ChannelGoodsDao warehouse2ChannelGoodsDao;
    @Autowired
    private Warehouse2ChannelDetailDao warehouse2ChannelDetailDao;
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
        return billCommonService.findBillPage(vo, inChannelDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode"});
    }

    /**
     * 保存单据
     *
     * @param vo
     * @throws MessageException
     */
    @Override
    public void save(BillVo vo, UserSessionBo userSessionBo) throws MessageException {
        billCommonService.save(new InChannelPo(), vo, userSessionBo, inChannelDao, inChannelGoodsDao, inChannelDetailDao, "QDSH", this.findParentGoodsList(vo.getParentBillId()));
    }


    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) {
        for (String id : vo.getIds()) {
            inChannelDetailDao.deleteAllByBillId(id);
            inChannelGoodsDao.deleteAllByBillId(id);
            inChannelDao.deleteById(id);
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
            InChannelPo po = inChannelDao.findById(id).get();
            po.setAuditUserId(userSessionBo.getId());
            po.setAuditUserName(userSessionBo.getName());
            po.setStatus(vo.getStatus());
            po.setAuditDate(d);
            inChannelDao.save(po);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : inChannelDetailDao.findByBillId(id)) {
                    stockChannelService.add(detail, po.getChannelId());
                    //修改上游单据数量 TODO
                }
            }
        }
        inChannelDao.flush();
    }

    /**
     * 反审核
     *
     * @param vo
     */
    public void unAudit(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            InChannelPo po = inChannelDao.findById(id).get();
            po.setStatus("AUDIT_FAILURE");
            inChannelDao.save(po);
            for (BillDetailPo detail : inChannelDetailDao.findByBillId(id)) {
                stockChannelService.minus(detail, po.getChannelId());
                //修改上游单据数量 TODO
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
        return billCommonService.findById(id, inChannelDao, inChannelGoodsDao, inChannelDetailDao);
    }

    /**
     * 导入单据
     *
     * @param file
     * @param userSessionBo
     */
    public void uploadBill(MultipartFile file, UserSessionBo userSessionBo) {
        billCommonService.uploadBill(file, userSessionBo, this, "inc", warehouse2ChannelDao);
    }

    @Override
    public String uploadBillParentBillCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(1));
    }

    @Override
    public String uploadBillGoodsCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(2));
    }

    @Override
    public String uploadBillGoodsColorCode(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(3));
    }

    @Override
    public String uploadBillGoodsColorName(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(4));
    }

    @Override
    public String uploadBillGoodsSizeName(Row row) throws MessageException {
        return ExcelReadUtil.getString(row.getCell(5));
    }

    @Override
    public BigDecimal uploadBillPrice(Row row) throws MessageException {
        return ExcelReadUtil.getBigDecimal(row.getCell(6));
    }

    @Override
    public Integer uploadBillBillCount(Row row) throws MessageException {
        return ExcelReadUtil.getInteger(row.getCell(7));
    }

    /**
     * 导出单据
     *
     * @param vo
     * @param response
     * @throws Exception
     */
    public void export(BaseBillExportVo vo, HttpServletResponse response) throws Exception {
        billCommonService.export(vo, response, this, inChannelGoodsDao, inChannelDetailDao, "channel", "warehouse", true);
    }


    /**
     * 查询上级单据仓库出货
     *
     * @param code
     * @return
     */
    public Page<Warehouse2ChannelPo> findParentBill(String code) {
        //TODO 只查询未完成的单据
        Page<Warehouse2ChannelPo> page = warehouse2ChannelDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
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
        List<BillGoodsPo> goodsList = warehouse2ChannelGoodsDao.findByBillId(id);
        List<BillDetailPo> detailList = warehouse2ChannelDetailDao.findByBillId(id);
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
