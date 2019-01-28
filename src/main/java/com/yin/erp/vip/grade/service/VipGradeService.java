package com.yin.erp.vip.grade.service;

import com.yin.erp.vip.grade.dao.VipGradeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员等级服务层
 *
 * @author yin
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class VipGradeService {

    @Autowired
    private VipGradeDao vipGradeDao;

}
