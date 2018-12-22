package com.yin.erp.base.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.MultipartConfigElement;

/**
 * 上传配置
 *
 * @author yin.weilong
 * @date 2018.12.19
 */
@Configuration
public class UploadConfig {

    @Value("${erp.file.temp.url}")
    private String erpFileTempUrl;

    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setLocation(erpFileTempUrl);
        return factory.createMultipartConfig();
    }
}
