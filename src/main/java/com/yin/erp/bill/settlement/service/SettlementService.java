package com.yin.erp.bill.settlement.service;


import com.yin.erp.base.entity.vo.in.BaseDeleteVo;
import com.yin.erp.base.exceptions.MessageException;
import com.yin.erp.base.utils.GenerateUtil;
import com.yin.erp.bill.channelinventory.dao.ChannelInventoryDao;
import com.yin.erp.bill.channelinventory.entity.po.ChannelInventoryPo;
import com.yin.erp.bill.channelloss.dao.ChannelLossDao;
import com.yin.erp.bill.channelloss.entity.po.ChannelLossPo;
import com.yin.erp.bill.channelloss.service.ChannelLossService;
import com.yin.erp.bill.common.enums.BillStatusEnum;
import com.yin.erp.bill.common.service.BillService;
import com.yin.erp.bill.settlement.dao.SettlementDao;
import com.yin.erp.bill.settlement.entity.po.SettlementPo;
import com.yin.erp.bill.warehouseinventory.dao.WarehouseInventoryDao;
import com.yin.erp.bill.warehouseinventory.entity.po.WarehouseInventoryPo;
import com.yin.erp.bill.warehouseloss.dao.WarehouseLossDao;
import com.yin.erp.bill.warehouseloss.entity.po.WarehouseLossPo;
import com.yin.erp.bill.warehouseloss.service.WarehouseLossService;
import com.yin.erp.info.channel.entity.po.ChannelPo;
import com.yin.erp.info.warehouse.entity.po.WarehousePo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 配货单服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class SettlementService extends BillService {

    @Autowired
    private SettlementDao settlementDao;
    @Autowired
    private WarehouseLossService warehouseLossService;
    @Autowired
    private WarehouseLossDao warehouseLossDao;
    @Autowired
    private WarehouseInventoryDao warehouseInventoryDao;
    @Autowired
    private ChannelLossService channelLossService;
    @Autowired
    private ChannelLossDao channelLossDao;
    @Autowired
    private ChannelInventoryDao channelInventoryDao;
    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;


    /**
     * 保存
     *
     * @param warehousePo
     * @param localDate
     * @return
     */
    public SettlementPo save(WarehousePo warehousePo, LocalDate localDate) {
        SettlementPo settlementPo = new SettlementPo();
        settlementPo.setCode("CKJC" + GenerateUtil.createSerialNumber());
        settlementPo.setBillDate(localDate);
        settlementPo.setType("WAREHOUSE");
        settlementPo.setWarehouseCode(warehousePo.getCode());
        settlementPo.setWarehouseId(warehousePo.getId());
        settlementPo.setWarehouseName(warehousePo.getName());
        settlementPo.setStatus(BillStatusEnum.AUDITED.name());
        settlementDao.save(settlementPo);
        return settlementPo;
    }

    /**
     * 保存
     *
     * @param channelPo
     * @param localDate
     * @return
     */
    public SettlementPo save(ChannelPo channelPo, LocalDate localDate) {
        SettlementPo settlementPo = new SettlementPo();
        settlementPo.setCode("QDJC" + GenerateUtil.createSerialNumber());
        settlementPo.setBillDate(localDate);
        settlementPo.setType("CHANNEL");
        settlementPo.setChannelCode(channelPo.getCode());
        settlementPo.setChannelId(channelPo.getId());
        settlementPo.setChannelName(channelPo.getName());
        settlementPo.setStatus(BillStatusEnum.AUDITED.name());
        settlementDao.save(settlementPo);
        return settlementPo;
    }

    /**
     * 删除
     *
     * @param vo
     */
    public void delete(BaseDeleteVo vo) throws MessageException {
        for (String id : vo.getIds()) {
            SettlementPo po = settlementDao.findById(id).get();
            if (po.getType().equals("WAREHOUSE")) {
                //查询损益，修改损益状态为审核后
                List<WarehouseLossPo> lossList = warehouseLossDao.findAllByParentBillId(id);
                lossList.stream().forEach(l -> l.setStatus(BillStatusEnum.AUDITED.name()));
                warehouseLossDao.saveAll(lossList);
                //反审核损益单
                BaseDeleteVo baseDeleteVo = new BaseDeleteVo();
                baseDeleteVo.setIds(lossList.stream().map(l -> l.getId()).collect(Collectors.toList()));
                warehouseLossService.unAudit(baseDeleteVo);
                //删除损益单
                warehouseLossService.delete(baseDeleteVo);
                //修改盘点单据状态和上游
                List<WarehouseInventoryPo> pdList = warehouseInventoryDao.findAllByParentBillId(id);
                pdList.stream().forEach(p -> {
                    p.setParentBillCode(null);
                    p.setParentBillId(null);
                    p.setStatus(BillStatusEnum.AUDITED.name());
                });
                warehouseInventoryDao.saveAll(pdList);
            }else if(po.getType().equals("CHANNEL")){
                //查询损益，修改损益状态为审核后
                List<ChannelLossPo> lossList = channelLossDao.findAllByParentBillId(id);
                lossList.stream().forEach(l -> l.setStatus(BillStatusEnum.AUDITED.name()));
                channelLossDao.saveAll(lossList);
                //反审核损益单
                BaseDeleteVo baseDeleteVo = new BaseDeleteVo();
                baseDeleteVo.setIds(lossList.stream().map(l -> l.getId()).collect(Collectors.toList()));
                channelLossService.unAudit(baseDeleteVo);
                //删除损益单
                channelLossService.delete(baseDeleteVo);
                //修改盘点单据状态和上游
                List<ChannelInventoryPo> pdList = channelInventoryDao.findAllByParentBillId(id);
                pdList.stream().forEach(p -> {
                    p.setParentBillCode(null);
                    p.setParentBillId(null);
                    p.setStatus(BillStatusEnum.AUDITED.name());
                });
                channelInventoryDao.saveAll(pdList);
            }
            settlementDao.delete(po);
        }
    }

}
