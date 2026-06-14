package cn.iocoder.yudao.module.lab.controller.admin.quality.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 实验室符合性通用分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LabQualityRecordPageReqVO extends PageParam {

    @Schema(description = "业务编号")
    private String recordNo;

    @Schema(description = "业务名称")
    private String recordName;

    @Schema(description = "状态")
    private String status;

}
