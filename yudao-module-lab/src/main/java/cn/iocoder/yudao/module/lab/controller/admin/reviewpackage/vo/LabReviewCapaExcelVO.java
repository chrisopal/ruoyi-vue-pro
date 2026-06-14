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
public class LabReviewCapaExcelVO {

    @ExcelProperty("CAPA 编号")
    private String capaNo;

    @ExcelProperty("关联 NC")
    private String ncNo;

    @ExcelProperty("原因分析")
    private String rootCause;

    @ExcelProperty("纠正/预防措施")
    private String action;

    @ExcelProperty("责任人")
    private String owner;

    @ExcelProperty("状态")
    private String status;

}
