package com.example.campustrade.config;

import com.example.campustrade.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//注册拦截器
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final LoginInterceptor loginInterceptor;

    public WebMvcConfig(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    //拦截页面设置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
         registry.addInterceptor(loginInterceptor)
                 .addPathPatterns("/products/**","/favorites/**","/admin/**","/orders/**","/files/**")
                 .excludePathPatterns("/auth/register","/auth/login");
    }
    //告诉浏览器照片url链接区电脑的哪个位置找
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:D:/xxx/uploads/images/");
    }
    //spring8080和vue5173的跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
