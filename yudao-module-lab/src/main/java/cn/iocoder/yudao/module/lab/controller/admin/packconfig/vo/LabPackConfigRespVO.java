package cn.iocoder.yudao.module.lab.controller.admin.packconfig.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 方案包可视化配置 Response VO")
@Data
public class LabPackConfigRespVO {

    private Long domainPackId;
    private List<LabPackConfigSaveReqVO.WorkflowNode> workflowNodes;
    private List<LabPackConfigSaveReqVO.TestItem> testItems;
    private List<LabPackConfigSaveReqVO.ResultField> resultFields;
    private List<LabPackConfigSaveReqVO.ReportSection> reportSections;

}
