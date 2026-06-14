import type { TagProps } from 'element-plus'
import type { LimsTaskVO } from '@/api/lims/workflow'

type StatusMeta = {
  label: string
  type: TagProps['type']
}

const UNKNOWN_STATUS: StatusMeta = { label: '-', type: 'info' }

export const TASK_STATUS_META: Record<string, StatusMeta> = {
  generated: { label: '待生成', type: 'info' },
  scheduled: { label: '已排程', type: 'primary' },
  assigned: { label: '已指派', type: 'primary' },
  ready: { label: '已就绪', type: 'warning' },
  testing: { label: '检测中', type: 'warning' },
  data_submitted: { label: '数据已提交', type: 'success' },
  reviewing: { label: '复核中', type: 'warning' },
  approved: { label: '已批准', type: 'success' },
  completed: { label: '已完成', type: 'success' },
  reported: { label: '已出报告', type: 'success' },
  hold: { label: '挂起', type: 'danger' },
  rework: { label: '返工中', type: 'danger' },
  cancelled: { label: '已取消', type: 'info' }
}

export const SCHEDULE_STATUS_META: Record<string, StatusMeta> = {
  unscheduled: { label: '未排程', type: 'info' },
  scheduled: { label: '已排程', type: 'primary' },
  conflict: { label: '冲突', type: 'danger' },
  locked: { label: '已锁定', type: 'warning' }
}

export const REVIEW_STATUS_META: Record<string, StatusMeta> = {
  none: { label: '未提交', type: 'info' },
  pending: { label: '待处理', type: 'warning' },
  approved: { label: '通过', type: 'success' },
  rejected: { label: '驳回', type: 'danger' }
}

export const TASK_STATUS_OPTIONS = Object.entries(TASK_STATUS_META).map(([value, meta]) => ({
  value,
  label: meta.label
}))

export const SCHEDULE_STATUS_OPTIONS = Object.entries(SCHEDULE_STATUS_META).map(([value, meta]) => ({
  value,
  label: meta.label
}))

export const REVIEW_STATUS_OPTIONS = Object.entries(REVIEW_STATUS_META).map(([value, meta]) => ({
  value,
  label: meta.label
}))

const READY_ALLOWED = new Set(['scheduled', 'assigned'])
const START_ALLOWED = new Set(['ready', 'rework'])
const RAW_RECORD_ALLOWED = new Set(['testing'])
const QC_RECORD_ALLOWED = new Set(['data_submitted'])
const REVIEW_ALLOWED = new Set(['reviewing'])

export const normalizeStatus = (value?: string | null) => (value || '').toLowerCase().trim()

const getMeta = (map: Record<string, StatusMeta>, value?: string | null) =>
  map[normalizeStatus(value)] || (value ? { label: value, type: 'info' as const } : UNKNOWN_STATUS)

export const formatTaskStatus = (value?: string | null) => getMeta(TASK_STATUS_META, value).label
export const formatScheduleStatus = (value?: string | null) => getMeta(SCHEDULE_STATUS_META, value).label
export const formatReviewStatus = (value?: string | null) => getMeta(REVIEW_STATUS_META, value).label

export const getTaskStatusTagType = (value?: string | null) => getMeta(TASK_STATUS_META, value).type
export const getScheduleStatusTagType = (value?: string | null) => getMeta(SCHEDULE_STATUS_META, value).type
export const getReviewStatusTagType = (value?: string | null) => getMeta(REVIEW_STATUS_META, value).type

export const formatReportEligible = (value?: boolean | null) => (value ? '可报告' : '不可报告')
export const getReportEligibleTagType = (value?: boolean | null): TagProps['type'] =>
  value ? 'success' : 'info'

export const canMarkReady = (task?: Pick<LimsTaskVO, 'taskStatus'> | null) =>
  READY_ALLOWED.has(normalizeStatus(task?.taskStatus))

export const canStartTask = (task?: Pick<LimsTaskVO, 'taskStatus'> | null) =>
  START_ALLOWED.has(normalizeStatus(task?.taskStatus))

export const canSubmitRawRecord = (task?: Pick<LimsTaskVO, 'taskStatus'> | null) =>
  RAW_RECORD_ALLOWED.has(normalizeStatus(task?.taskStatus))

export const canSubmitQcRecord = (task?: Pick<LimsTaskVO, 'taskStatus'> | null) =>
  QC_RECORD_ALLOWED.has(normalizeStatus(task?.taskStatus))

export const canReviewTask = (task?: Pick<LimsTaskVO, 'taskStatus'> | null) =>
  REVIEW_ALLOWED.has(normalizeStatus(task?.taskStatus))

export const formatTaskWindow = (task?: Pick<LimsTaskVO, 'plannedStartTime' | 'plannedEndTime'> | null) => {
  if (!task?.plannedStartTime && !task?.plannedEndTime) {
    return '-'
  }
  return `${task?.plannedStartTime || '-'} ~ ${task?.plannedEndTime || '-'}`
}

export const formatActorLabel = (name?: string | null, id?: number | null) => {
  if (name && id) {
    return `${name} (#${id})`
  }
  if (name) {
    return name
  }
  if (id) {
    return `#${id}`
  }
  return '-'
}
