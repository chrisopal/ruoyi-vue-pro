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
public class LabReviewNonconformityExcelVO {

    @ExcelProperty("NC 编号")
    private String ncNo;

    @ExcelProperty("对应条款")
    private String clauseCode;

    @ExcelProperty("严重程度")
    private String severity;

    @ExcelProperty("不符合描述")
    private String description;

    @ExcelProperty("责任人")
    private String owner;

    @ExcelProperty("计划完成日期")
    private String dueDate;

}
