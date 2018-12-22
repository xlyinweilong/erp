package com.yin.erp.base.entity.po;


import com.yin.erp.base.utils.GenerateUtil;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseDataPo implements Serializable {

    @Id
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
//    @GeneratedValue(generator = "system-uuid")
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid-string")
    @Column(name = "id", length = 32)
    private String id = GenerateUtil.createUUID();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseDataPo that = (BaseDataPo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
