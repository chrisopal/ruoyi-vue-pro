package cn.iocoder.yudao.module.lims.service.workflow;

import java.util.Set;

public final class LimsTaskStatus {

    public static final String GENERATED = "generated";
    public static final String SCHEDULED = "scheduled";
    public static final String ASSIGNED = "assigned";
    public static final String READY = "ready";
    public static final String TESTING = "testing";
    public static final String DATA_SUBMITTED = "data_submitted";
    public static final String REVIEWING = "reviewing";
    public static final String APPROVED = "approved";
    public static final String COMPLETED = "completed";
    public static final String REPORTED = "reported";
    public static final String HOLD = "hold";
    public static final String REWORK = "rework";
    public static final String CANCELLED = "cancelled";

    public static final Set<String> REPORT_ALLOWED = Set.of(APPROVED, COMPLETED, REPORTED);

    private LimsTaskStatus() {
    }

}
