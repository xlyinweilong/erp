package com.yin.erp.base.upload;

import com.yin.erp.base.utils.ExcelReadUtil;
import com.yin.erp.info.dict.dao.DictSizeDao;
import com.yin.erp.info.dict.entity.po.DictSizePo;
import com.yin.erp.info.goods.dao.GoodsColorDao;
import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 上传验证
 *
 * @author yin.weilong
 * @date 2019.03.04
 */
@Service
public class UploadValidateService {

    @Autowired
    private GoodsColorDao goodsColorDao;
    @Autowired
    private DictSizeDao dictSizeDao;

    /**
     * 校验颜色，返回颜色PO
     *
     * @param goodsId
     * @param colorCode
     * @param colorName
     * @param row
     * @param errorCellNum
     * @return
     */
    public GoodsColorPo validateGoodsColor(String goodsId, String colorCode, String colorName, Row row, int errorCellNum) {
        //验证颜色
        List<GoodsColorPo> colorList = goodsColorDao.findByGoodsId(goodsId);
        GoodsColorPo goodsColorPo = null;
        List<GoodsColorPo> goodsColorPoList = colorList.stream().filter(g -> g.getColorCode().equals(colorCode)).collect(Collectors.toList());
        if (goodsColorPoList.isEmpty()) {
            ExcelReadUtil.addErrorToRow(row, errorCellNum, "颜色编号没定位颜色");
            return null;
        }
        if (goodsColorPoList.size() > 1) {
            Optional<GoodsColorPo> goodsColorPoOptional = goodsColorPoList.stream().filter(g -> g.getColorName().equals(colorName)).findFirst();
            if (!goodsColorPoOptional.isPresent()) {
                ExcelReadUtil.addErrorToRow(row, errorCellNum, "颜色编号和名称没定位颜色");
                return null;
            } else {
                goodsColorPo = goodsColorPoOptional.get();
            }
        }
        if (goodsColorPoList.size() == 1) {
            goodsColorPo = goodsColorPoList.get(0);
        }
        return goodsColorPo;
    }

    /**
     * 校验尺码，返回尺码PO
     *
     * @param sizeGroupId
     * @param sizeName
     * @param row
     * @param errorCellNum
     * @return
     */
    public DictSizePo validateGoodsSize(String sizeGroupId, String sizeName, Row row, int errorCellNum) {
        List<DictSizePo> sizeList = dictSizeDao.findByGroupId(sizeGroupId);
        Optional<DictSizePo> dictSizePo = sizeList.stream().filter(g -> g.getName().equals(sizeName)).findFirst();
        if (!dictSizePo.isPresent()) {
            ExcelReadUtil.addErrorToRow(row, errorCellNum, "尺码不存在");
            return null;
        }
        return dictSizePo.get();
    }

}
