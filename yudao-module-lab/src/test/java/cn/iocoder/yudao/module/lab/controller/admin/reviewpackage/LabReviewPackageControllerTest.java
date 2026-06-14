package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewBatchPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewBatchSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewCapaExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewChecklistExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewEvidenceExcelVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewItemPageReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewItemSaveReqVO;
import cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo.LabReviewNonconformityExcelVO;
import cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage.LabReviewBatchDO;
import cn.iocoder.yudao.module.lab.dal.dataobject.reviewpackage.LabReviewItemDO;
import cn.iocoder.yudao.module.lab.service.reviewpackage.LabReviewPackageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LabReviewPackageControllerTest {

    private final LabReviewPackageController controller = new LabReviewPackageController();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(controller, "reviewPackageService", new StubReviewPackageService());
    }

    @Test
    void exportChecklist_shouldWriteExcel() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.exportChecklist(response, "RP-MVP-001");

        assertExcelResponse(response);
    }

    @Test
    void exportEvidence_shouldWriteExcel() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.exportEvidence(response, "RP-MVP-001");

        assertExcelResponse(response);
    }

    @Test
    void exportNonconformity_shouldWriteExcel() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.exportNonconformity(response, "RP-MVP-001");

        assertExcelResponse(response);
    }

    @Test
    void exportCapa_shouldWriteExcel() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        controller.exportCapa(response, "RP-MVP-001");

        assertExcelResponse(response);
    }

    private void assertExcelResponse(MockHttpServletResponse response) {
        assertEquals("application/vnd.ms-excel;charset=UTF-8", response.getContentType());
        assertTrue(response.getHeader("Content-Disposition").startsWith("attachment;filename="));
        assertTrue(response.getContentAsByteArray().length > 0);
    }

    private static class StubReviewPackageService implements LabReviewPackageService {

        @Override
        public Long createReviewBatch(LabReviewBatchSaveReqVO createReqVO) {
            return 1L;
        }

        @Override
        public void updateReviewBatch(LabReviewBatchSaveReqVO updateReqVO) {
        }

        @Override
        public void deleteReviewBatch(Long id) {
        }

        @Override
        public LabReviewBatchDO getReviewBatch(Long id) {
            LabReviewBatchDO batch = new LabReviewBatchDO();
            batch.setId(id);
            batch.setBatchCode("RP-MVP-001");
            batch.setBatchName("MVP 评审材料包");
            return batch;
        }

        @Override
        public PageResult<LabReviewBatchDO> getReviewBatchPage(LabReviewBatchPageReqVO pageReqVO) {
            return new PageResult<>(List.of(), 0L);
        }

        @Override
        public Long createReviewItem(LabReviewItemSaveReqVO createReqVO) {
            return 1L;
        }

        @Override
        public void updateReviewItem(LabReviewItemSaveReqVO updateReqVO) {
        }

        @Override
        public void deleteReviewItem(Long id) {
        }

        @Override
        public LabReviewItemDO getReviewItem(Long id) {
            LabReviewItemDO item = new LabReviewItemDO();
            item.setId(id);
            item.setBatchId(1L);
            item.setItemType("checklist");
            return item;
        }

        @Override
        public PageResult<LabReviewItemDO> getReviewItemPage(LabReviewItemPageReqVO pageReqVO) {
            return new PageResult<>(List.of(), 0L);
        }

        @Override
        public List<LabReviewChecklistExcelVO> getChecklistRows(String batchCode) {
            return List.of(new LabReviewChecklistExcelVO("ISO/IEC 17025", "PERSONNEL", "人员",
                    "确认人员能力、授权、培训和监督记录完整", "人员授权", "技术负责人"));
        }

        @Override
        public List<LabReviewEvidenceExcelVO> getEvidenceRows(String batchCode) {
            return List.of(new LabReviewEvidenceExcelVO("报告", "lims_report", "RPT-2026-MVP-001",
                    "报告", "已关联", "报告预览和归档入口"));
        }

        @Override
        public List<LabReviewNonconformityExcelVO> getNonconformityRows(String batchCode) {
            return List.of(new LabReviewNonconformityExcelVO("NC-MVP-001", "TECHNICAL_RECORD", "一般",
                    "部分原始记录缺少复核人签名", "质量负责人", "2026-07-15"));
        }

        @Override
        public List<LabReviewCapaExcelVO> getCapaRows(String batchCode) {
            return List.of(new LabReviewCapaExcelVO("CAPA-MVP-001", "NC-MVP-001",
                    "原始记录复核流程缺少系统提醒", "增加记录复核待办和签名完整性检查", "质量负责人", "进行中"));
        }

    }

}
