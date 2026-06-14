package cn.iocoder.yudao.module.lims.service.workflow;

public final class LimsTaskEventType {

    public static final String CREATED = "created";
    public static final String SCHEDULED = "scheduled";
    public static final String ASSIGNED = "assigned";
    public static final String READINESS_PASSED = "readiness_passed";
    public static final String STARTED = "started";
    public static final String RECORD_SUBMITTED = "record_submitted";
    public static final String REVIEW_SUBMITTED = "review_submitted";
    public static final String APPROVED = "approved";
    public static final String REJECTED = "rejected";
    public static final String HOLD = "hold";
    public static final String RESUMED = "resumed";
    public static final String COMPLETED = "completed";
    public static final String REPORTED = "reported";

    private LimsTaskEventType() {
    }

}
