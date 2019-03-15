package com.yin.erp.bill.common.entity.vo.in;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.yin.common.entity.vo.BaseVo;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class BillInventoryVo extends BaseVo {

    @NotBlank
    private String eleId;

    @NotBlank
    private String type;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate date;
}
