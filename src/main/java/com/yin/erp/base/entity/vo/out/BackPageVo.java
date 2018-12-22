package com.yin.erp.base.entity.vo.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 返回的分页VO
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@Getter
@Setter
public class BackPageVo<T> {

    private List<T> content;
    private Long totalElements;
}
