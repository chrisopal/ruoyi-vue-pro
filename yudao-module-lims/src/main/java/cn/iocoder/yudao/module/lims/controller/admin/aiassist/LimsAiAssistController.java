package cn.iocoder.yudao.module.lims.controller.admin.aiassist;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.lims.controller.admin.aiassist.vo.LimsAiAssistCenterRespVO;
import cn.iocoder.yudao.module.lims.service.aiassist.LimsAiAssistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - LIMS AI 标准与解读中心")
@RestController
@RequestMapping("/lims/ai-assist")
@Validated
public class LimsAiAssistController {

    @Resource
    private LimsAiAssistService aiAssistService;

    @GetMapping("/center")
    @Operation(summary = "获得 AI 标准与解读中心")
    @PreAuthorize("@ss.hasPermission('lims:ai-assist:query')")
    public CommonResult<LimsAiAssistCenterRespVO> getCenter(
            @RequestParam(value = "requestId", required = false) Long requestId,
            @RequestParam(value = "question", required = false) String question) {
        return success(aiAssistService.getCenter(requestId, question));
    }

}
