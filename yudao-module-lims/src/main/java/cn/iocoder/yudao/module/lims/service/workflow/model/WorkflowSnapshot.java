package cn.iocoder.yudao.module.lims.service.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WorkflowSnapshot {

    private String snapshotJson;
    private String snapshotHash;
    private LocalDateTime frozenAt;
    private Long domainPackId;
    private String packCode;
    private String packVersion;

}
