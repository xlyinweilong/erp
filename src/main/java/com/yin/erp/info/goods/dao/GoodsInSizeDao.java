package com.yin.erp.info.goods.dao;


import com.yin.erp.info.goods.entity.po.GoodsInSizePo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 货品内长资料
 *
 * @author yin
 */
@Resource
public interface GoodsInSizeDao extends PagingAndSortingRepository<GoodsInSizePo, String>, JpaSpecificationExecutor {

    /**
     * 通过货品ID删除
     *
     * @param goodsId
     * @return
     */
    @Modifying
    @Query("delete from GoodsInSizePo t where t.goodsId = :goodsId")
    int deleteByGoodsId(@Param("goodsId") String goodsId);

    /**
     * 根据货品查询
     *
     * @param goodsId
     * @return
     */
    List<GoodsInSizePo> findByGoodsId(String goodsId);

    /**
     * 根据货品和内长查询
     *
     * @param goodsId
     * @return
     */
    GoodsInSizePo findByGoodsIdAndInSizeId(String goodsId, String inSizeId);

    /**
     * 查询数量
     *
     * @param inSizeId
     * @return
     */
    long countByInSizeId(String inSizeId);

}
