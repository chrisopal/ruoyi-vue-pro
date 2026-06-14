package cn.iocoder.yudao.module.lab.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * lab 模块的 web 组件的 Configuration。
 */
@Configuration(proxyBeanMethods = false)
public class LabWebConfiguration {

    @Bean
    public GroupedOpenApi labGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("lab");
    }

}
