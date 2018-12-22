package com.yin.erp.info.dict.dao;


import com.yin.erp.info.dict.entity.po.DictSizePo;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import javax.annotation.Resource;
import java.util.List;

/**
 * 字典尺码
 *
 * @author yin
 */
@Resource
public interface DictSizeDao extends PagingAndSortingRepository<DictSizePo, String>, JpaSpecificationExecutor {

    /**
     * 根据尺码组查询
     *
     * @param groupId
     * @return
     */
    @Query("SELECT t FROM DictSizePo t WHERE t.groupId = :groupId ORDER BY t.orderIndex ASC")
    List<DictSizePo> findByGroupId(@Param("groupId") String groupId);


    /**
     * 根据尺码组ID和尺码名称查询
     *
     * @param groupId
     * @param name
     * @return
     */
    DictSizePo findByGroupIdAndName(String groupId, String name);


    /**
     * 根据尺码组删除
     *
     * @param groupId
     * @return
     */
    int deleteByGroupId(@Param("groupId") String groupId);

}
