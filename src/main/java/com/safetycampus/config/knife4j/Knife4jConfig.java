package com.safetycampus.config.knife4j;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("平安校园一键报警联网平台 API")
                        .version("1.0.0")
                        .description("平安校园一键报警联网平台接口文档")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@safetycampus.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public GroupedOpenApi schoolApi() {
        return GroupedOpenApi.builder()
                .group("学校接入")
                .pathsToMatch("/school/**")
                .build();
    }

    @Bean
    public GroupedOpenApi alarmApi() {
        return GroupedOpenApi.builder()
                .group("警情汇聚")
                .pathsToMatch("/alarm/**")
                .build();
    }

    @Bean
    public GroupedOpenApi superviseApi() {
        return GroupedOpenApi.builder()
                .group("督办流转")
                .pathsToMatch("/supervise/**")
                .build();
    }

    @Bean
    public GroupedOpenApi notifyApi() {
        return GroupedOpenApi.builder()
                .group("联动通知")
                .pathsToMatch("/notify/**", "/contact/**", "/police/**")
                .build();
    }

    @Bean
    public GroupedOpenApi reportApi() {
        return GroupedOpenApi.builder()
                .group("考核报表")
                .pathsToMatch("/report/**", "/assess/**", "/risk/**")
                .build();
    }

    @Bean
    public GroupedOpenApi systemApi() {
        return GroupedOpenApi.builder()
                .group("系统管理")
                .pathsToMatch("/system/**", "/auth/**", "/user/**", "/role/**", "/dept/**", "/param/**")
                .build();
    }
}
