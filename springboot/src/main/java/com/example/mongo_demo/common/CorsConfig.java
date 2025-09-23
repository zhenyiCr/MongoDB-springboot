package com.example.mongo_demo.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsconfiguration = new CorsConfiguration();
        corsconfiguration.addAllowedOrigin("*"); // 1 设置访问源地址
        corsconfiguration.addAllowedHeader("*"); // 2 设置访问源请求头
        corsconfiguration.addAllowedMethod("*"); // 3 配置访问源请求方法
        source.registerCorsConfiguration("/**", corsconfiguration); // 4 对接口配置跨域设置
        return new CorsFilter(source);
    }
}
