package com.yin.erp.bill.inwarehouse.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.entity.vo.out.BackPageVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.feign.user.bo.UserSessionBo;
import com.yin.erp.base.utils.CopyUtil;
import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseDao;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseDetailDao;
import com.yin.erp.bill.channel2warehouse.dao.Channel2WarehouseGoodsDao;
import com.yin.erp.bill.channel2warehouse.entity.po.Channel2WarehousePo;
import com.yin.erp.bill.common.entity.po.BillDetailPo;
import com.yin.erp.bill.common.entity.po.BillGoodsPo;
import com.yin.erp.bill.common.entity.po.BillPo;
import com.yin.erp.bill.common.entity.vo.BillVo;
import com.yin.erp.bill.common.entity.vo.in.*;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillCommonService;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.inwarehouse.dao.InWarehouseDao;
import com.yin.erp.bill.inwarehouse.dao.InWarehouseDetailDao;
import com.yin.erp.bill.inwarehouse.dao.InWarehouseGoodsDao;
import com.yin.erp.bill.inwarehouse.entity.po.InWarehousePo;
import com.yin.erp.stock.service.StockWarehouseService;
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
        return billCommonService.findBillPage(vo, inWarehouseDao, new String[]{"warehouseCode", "warehouseName", "channelName", "channelCode"});
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
            Channel2WarehousePo oldParent = channel2WarehouseDao.findById(inWarehouseDao.findById(vo.getId()).get().getParentBillId()).get();
            oldParent.setChildBillId(null);
            oldParent.setStatus("AUDITED");
            channel2WarehouseDao.save(oldParent);
        }
        BillPo billPo = billCommonService.save(new InWarehousePo(), vo, userSessionBo, inWarehouseDao, inWarehouseGoodsDao, inWarehouseDetailDao, "CKSTH", this.findParentGoodsList(vo.getParentBillId()));
        //查询上游单据
        Channel2WarehousePo newParent = channel2WarehouseDao.findById(vo.getParentBillId()).get();
        newParent.setChildBillId(billPo.getId());
        newParent.setStatus("QUOTE");
        channel2WarehouseDao.save(newParent);
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
            Channel2WarehousePo oldParent = channel2WarehouseDao.findById(inWarehouseDao.findById(id).get().getParentBillId()).get();
            oldParent.setChildBillId(null);
            oldParent.setStatus("AUDITED");
            channel2WarehouseDao.save(oldParent);
            billCommonService.deleteById(id, inWarehouseDao, inWarehouseGoodsDao, inWarehouseDetailDao);
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
            InWarehousePo po = inWarehouseDao.findById(id).get();
            billCommonService.audit(id, vo, userSessionBo, d, inWarehouseDao, inWarehouseGoodsDao, inWarehouseDetailDao);
            if (vo.getStatus().equals(BillStatusEnum.AUDITED.name())) {
                for (BillDetailPo detail : inWarehouseDetailDao.findByBillId(id)) {
                    stockWarehouseService.add(detail, po.getWarehouseId());
                    //修改上游单据数量 TODO
                }
                //修改上游单据状态
                Channel2WarehousePo parent = channel2WarehouseDao.findById(po.getParentBillId()).get();
                parent.setStatus("COMPLETE");
                channel2WarehouseDao.save(parent);
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
            Channel2WarehousePo parent = channel2WarehouseDao.findById(po.getParentBillId()).get();
            parent.setStatus("QUOTE");
            channel2WarehouseDao.save(parent);
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
        billCommonService.export(vo, response, this, inWarehouseGoodsDao, inWarehouseDetailDao, "warehouse", "channel", true);
    }


    /**
     * 查询上级单据仓库出货
     *
     * @param code
     * @return
     */
    public Page<Channel2WarehousePo> findParentBill(String code) {
        Page<Channel2WarehousePo> page = channel2WarehouseDao.findAll((root, criteriaQuery, criteriaBuilder) -> {
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
        List<BillGoodsPo> goodsList = channel2WarehouseGoodsDao.findByBillId(id);
        List<BillDetailPo> detailList = channel2WarehouseDetailDao.findByBillId(id);
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
