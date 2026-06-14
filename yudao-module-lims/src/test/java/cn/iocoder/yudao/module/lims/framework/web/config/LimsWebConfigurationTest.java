package cn.iocoder.yudao.module.lims.framework.web.config;

import cn.iocoder.yudao.module.lims.service.workflow.ReportOutputGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimsWebConfigurationTest {

    @TempDir
    private Path outputDir;

    @AfterEach
    void tearDown() {
        System.clearProperty(ReportOutputGenerator.OUTPUT_DIR_PROPERTY);
    }

    @Test
    void resolveReportOutputResourceLocation_shouldUseConfiguredOutputDirectory() {
        System.setProperty(ReportOutputGenerator.OUTPUT_DIR_PROPERTY, outputDir.toString());

        String location = LimsWebConfiguration.resolveReportOutputResourceLocation();

        assertEquals(outputDir.toAbsolutePath().normalize().toUri().toString(), location);
        assertTrue(location.startsWith("file:"));
        assertTrue(location.endsWith("/"));
    }

    @Test
    void reportOutputUrlPattern_shouldMatchGeneratedArtifactUrls() {
        assertEquals("/lims/report-output/**", LimsWebConfiguration.REPORT_OUTPUT_URL_PATTERN);
    }

}
