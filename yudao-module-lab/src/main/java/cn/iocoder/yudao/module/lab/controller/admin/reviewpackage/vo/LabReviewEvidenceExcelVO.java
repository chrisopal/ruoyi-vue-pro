package cn.iocoder.yudao.module.lab.controller.admin.reviewpackage.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
public class LabReviewEvidenceExcelVO {

    @ExcelProperty("证据类型")
    private String evidenceName;

    @ExcelProperty("来源对象")
    private String sourceObject;

    @ExcelProperty("关联对象编号")
    private String linkedObjectNo;

    @ExcelProperty("覆盖分类")
    private String clauseCategory;

    @ExcelProperty("关联状态")
    private String linkStatus;

    @ExcelProperty("备注")
    private String remark;

}
