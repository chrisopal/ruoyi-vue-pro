package cn.iocoder.yudao.module.lims.controller.admin.dashboard;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.lims.controller.admin.dashboard.vo.LimsOperationsDashboardRespVO;
import cn.iocoder.yudao.module.lims.service.dashboard.LimsOperationsDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - TIC LIMS 评审与运营看板")
@RestController
@RequestMapping("/lims/dashboard")
@Validated
public class LimsOperationsDashboardController {

    @Resource
    private LimsOperationsDashboardService operationsDashboardService;

    @GetMapping("/operations")
    @Operation(summary = "获得 TIC LIMS 评审与运营看板")
    @PreAuthorize("@ss.hasPermission('lab:dashboard:query')")
    public CommonResult<LimsOperationsDashboardRespVO> getOperationsDashboard() {
        return success(operationsDashboardService.getOperationsDashboard());
    }

}
