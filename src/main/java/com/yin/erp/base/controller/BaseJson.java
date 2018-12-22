package com.yin.erp.base.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@Builder
public class BaseJson implements Serializable {

    private String message;

    private Integer code;

    private Object data;

    public static BaseJson getSuccess(String message, Object data) {
        return BaseJson.builder().code(0).message(message).data(data).build();
    }

    public static BaseJson getSuccess(String message) {
        return BaseJson.builder().code(0).message(message).build();
    }

    public static BaseJson getSuccess(Object data) {
        return BaseJson.builder().code(0).message("操作成功").data(data).build();
    }

    public static BaseJson getSuccess() {
        return BaseJson.builder().code(0).message("操作成功").build();
    }

    public static BaseJson getError() {
        return BaseJson.builder().code(1).message("操作成功").build();
    }


    public static BaseJson getError(String message) {
        return BaseJson.builder().code(1).message(message).build();
    }


}
