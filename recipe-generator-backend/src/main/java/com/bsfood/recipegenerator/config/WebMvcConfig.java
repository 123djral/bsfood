package com.bsfood.recipegenerator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置 - 提供本地图片访问
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 本地图片保存路径
    private static final String PICTURE_PATH = "C:/develop/codeBase/bsfood1/picture";

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置 /picture/** 路径映射到本地文件夹
        registry.addResourceHandler("/picture/**")
                .addResourceLocations("file:" + PICTURE_PATH + "/");
    }
}
