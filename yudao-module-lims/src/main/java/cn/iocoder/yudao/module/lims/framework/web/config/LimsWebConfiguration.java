package cn.iocoder.yudao.module.lims.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * lims 模块的 web 组件的 Configuration。
 */
@Configuration(proxyBeanMethods = false)
public class LimsWebConfiguration {

    @Bean
    public GroupedOpenApi limsGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("lims");
    }

}
