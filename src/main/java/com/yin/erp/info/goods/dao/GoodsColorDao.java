package com.yin.erp.info.goods.dao;


import com.yin.erp.info.goods.entity.po.GoodsColorPo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 货品颜色资料
 *
 * @author yin
 */
@Resource
public interface GoodsColorDao extends PagingAndSortingRepository<GoodsColorPo, String>, JpaSpecificationExecutor {

    /**
     * 通过货品删除
     *
     * @param goodsId
     * @return
     */
    @Modifying
    @Query("delete from GoodsColorPo t where t.goodsId = :goodsId")
    int deleteByGoodsId(@Param("goodsId") String goodsId);

    /**
     * 根据货品查询
     *
     * @param goodsId
     * @return
     */
    List<GoodsColorPo> findByGoodsId(String goodsId);

    /**
     * 根据货品和颜色查询
     *
     * @param goodsId
     * @param colorId
     * @return
     */
    GoodsColorPo findByGoodsIdAndColorId(String goodsId, String colorId);

    /**
     * 颜色ID的数量
     *
     * @param colorId
     * @return
     */
    long countByColorId(String colorId);

}
