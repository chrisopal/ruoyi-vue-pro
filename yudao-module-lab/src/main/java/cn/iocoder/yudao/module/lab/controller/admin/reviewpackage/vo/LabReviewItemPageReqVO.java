package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 评审材料条目分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabReviewItemPageReqVO extends PageParam {

    @Schema(description = "评审批次编号", example = "1")
    private Long batchId;

    @Schema(description = "条目类型", example = "checklist")
    private String itemType;

    @Schema(description = "条款编号", example = "PERSONNEL")
    private String clauseCode;

    @Schema(description = "条款分类", example = "personnel")
    private String clauseCategory;

    @Schema(description = "NC 编号", example = "NC-MVP-001")
    private String ncNo;

    @Schema(description = "CAPA 编号", example = "CAPA-MVP-001")
    private String capaNo;

}
