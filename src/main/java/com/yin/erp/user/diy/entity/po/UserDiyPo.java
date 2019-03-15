package com.yin.erp.user.diy.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yin.common.entity.po.BaseDataPo;
import com.yin.erp.user.user.entity.po.UserPo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 用户偏好
 *
 * @author yin
 */
@Entity
@Table(name = "u_diy")
@Getter
@Setter
public class UserDiyPo extends BaseDataPo {

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * 偏好类型
     */
    @Column(name = "type")
    private String type;

    /**
     * 偏好KEY
     */
    @Column(name = "diy_key")
    private String key;

    @JsonIgnore
    @ManyToOne(cascade = {}, optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    private UserPo userPo;

}
