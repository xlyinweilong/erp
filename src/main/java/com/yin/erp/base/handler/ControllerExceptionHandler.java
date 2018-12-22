package com.yin.erp.base.handler;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
import com.yin.erp.base.controller.BaseJson;
import com.yin.erp.base.exceptions.BaseException;
import com.yin.erp.base.exceptions.MessageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * 全局控制器异常处理机制
 *
 * @author yin.weilong
 * @date 2018.11.11
 */
@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * 全局异常处理
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseJson handle(Exception e) {
        return BaseJson.getError(e.getMessage());
    }

    /**
     * 项目异常信息
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public BaseJson handleBaseException(Exception e) {
        return BaseJson.getError(e.getMessage());
    }

    /**
     * 操作级别异常信息
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MessageException.class)
    @ResponseBody
    public BaseJson handleMessageException(Exception e) {
        return BaseJson.getError(e.getMessage());
    }

    /**
     * 主键冲突
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = DuplicateKeyException.class)
    @ResponseBody
    public BaseJson handleDuplicateKeyException(Exception e) {
        LOGGER.error("主键冲突：" + e.getMessage());
        return BaseJson.getError("主键冲突：" + e.getMessage());
    }

    /**
     * 唯一索引重复
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MySQLIntegrityConstraintViolationException.class)
    @ResponseBody
    public BaseJson handleMySQLIntegrityConstraintViolationException(Exception e) {
        return BaseJson.getError("数据已经存在");
    }

    /**
     * 唯一索引重复
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    @ResponseBody
    public BaseJson handleDataIntegrityViolationException(Exception e) {
        return BaseJson.getError("数据已经存在");
    }


}
