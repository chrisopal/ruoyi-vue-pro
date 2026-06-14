package cn.iocoder.yudao.module.lims.framework.web.config;

import cn.iocoder.yudao.framework.swagger.config.YudaoSwaggerAutoConfiguration;
import cn.iocoder.yudao.module.lims.service.workflow.ReportOutputGenerator;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * lims 模块的 web 组件的 Configuration。
 */
@Configuration(proxyBeanMethods = false)
public class LimsWebConfiguration implements WebMvcConfigurer {

    static final String REPORT_OUTPUT_URL_PATTERN = "/lims/report-output/**";
    private static final Path DEFAULT_REPORT_OUTPUT_DIR = Path.of("target", "lims-report-output");

    @Bean
    public GroupedOpenApi limsGroupedOpenApi() {
        return YudaoSwaggerAutoConfiguration.buildGroupedOpenApi("lims");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(REPORT_OUTPUT_URL_PATTERN)
                .addResourceLocations(resolveReportOutputResourceLocation());
    }

    static String resolveReportOutputResourceLocation() {
        String configured = System.getProperty(ReportOutputGenerator.OUTPUT_DIR_PROPERTY);
        Path outputDir = StringUtils.hasText(configured) ? Path.of(configured) : DEFAULT_REPORT_OUTPUT_DIR;
        return outputDir.toAbsolutePath().normalize().toUri().toString();
    }

}
