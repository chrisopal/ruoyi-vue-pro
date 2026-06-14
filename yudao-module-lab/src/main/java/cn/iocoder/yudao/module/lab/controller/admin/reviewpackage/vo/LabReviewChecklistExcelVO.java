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
public class LabReviewChecklistExcelVO {

    @ExcelProperty("标准")
    private String standardName;

    @ExcelProperty("条款")
    private String clauseCode;

    @ExcelProperty("分类")
    private String clauseCategory;

    @ExcelProperty("检查要点")
    private String checkPoint;

    @ExcelProperty("期望证据")
    private String expectedEvidence;

    @ExcelProperty("责任角色")
    private String ownerRole;

}
