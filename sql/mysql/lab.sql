-- Lab/LIMS module bootstrap for RuoYi-Vue-Pro.
-- This script is intentionally additive and idempotent for migration use.

CREATE TABLE IF NOT EXISTS `lab_domain_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测领域编码',
  `domain_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测领域名称',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '领域说明',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_domain_code_tenant_deleted` (`domain_code`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室检测领域配置';

CREATE TABLE IF NOT EXISTS `lab_domain_pack` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_id` bigint NOT NULL COMMENT '检测领域编号',
  `pack_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方案包编码',
  `pack_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方案包名称',
  `pack_version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '方案包版本',
  `industry` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '行业方向',
  `application_scope` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '适用范围',
  `workflow_schema` json NULL COMMENT '流程配置 JSON',
  `template_schema` json NULL COMMENT '模板配置 JSON',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_domain_pack_code_version_tenant_deleted` (`pack_code`, `pack_version`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_domain_pack_domain` (`domain_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室检测方案包配置';

CREATE TABLE IF NOT EXISTS `lab_standard` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `standard_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标准编码',
  `standard_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标准名称',
  `standard_version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标准版本',
  `standard_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '标准类型',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_standard_code_tenant_deleted` (`standard_code`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室认可/资质标准';

CREATE TABLE IF NOT EXISTS `lab_standard_clause` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `standard_id` bigint NOT NULL COMMENT '标准编号',
  `clause_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条款编号',
  `clause_title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条款标题',
  `clause_category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条款业务分类',
  `requirement_text` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '要求摘要',
  `evidence_type_codes` json NULL COMMENT '证据类型编码列表',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_clause_standard_code_tenant_deleted` (`standard_id`, `clause_code`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_clause_category` (`clause_category`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室标准条款映射';

CREATE TABLE IF NOT EXISTS `lab_evidence_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `evidence_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据编码',
  `evidence_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据名称',
  `source_object` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源对象',
  `clause_category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条款业务分类',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_evidence_code_tenant_deleted` (`evidence_code`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室证据类型';

CREATE TABLE IF NOT EXISTS `lab_evidence_object` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `evidence_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据编码',
  `evidence_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据名称',
  `evidence_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据类型',
  `source_object` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源对象',
  `source_object_id` bigint NULL DEFAULT NULL COMMENT '来源对象编号',
  `source_object_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源对象单号',
  `business_domain` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '业务域',
  `file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件地址',
  `file_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_format` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件格式',
  `evidence_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '证据哈希',
  `issued_by` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '签发机构',
  `issued_at` date NULL DEFAULT NULL COMMENT '签发日期',
  `valid_from` date NULL DEFAULT NULL COMMENT '有效期开始',
  `valid_to` date NULL DEFAULT NULL COMMENT '有效期结束',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'effective' COMMENT '状态',
  `summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '摘要',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_evidence_object_code_tenant_deleted` (`evidence_code`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_evidence_object_source` (`source_object`, `source_object_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_evidence_object_type_status` (`evidence_type`, `status`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室证据对象';

CREATE TABLE IF NOT EXISTS `lab_evidence_link` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `evidence_object_id` bigint NULL DEFAULT NULL COMMENT '证据对象编号',
  `evidence_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据编码',
  `evidence_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '证据名称',
  `evidence_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '证据文件地址',
  `evidence_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '证据哈希',
  `source_object` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据来源对象',
  `source_object_id` bigint NULL DEFAULT NULL COMMENT '证据来源对象编号',
  `source_object_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '证据来源对象单号',
  `linked_biz_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '关联业务类型',
  `linked_biz_id` bigint NULL DEFAULT NULL COMMENT '关联业务编号',
  `linked_biz_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联业务单号',
  `clause_id` bigint NULL DEFAULT NULL COMMENT '条款编号',
  `capability_scope_id` bigint NULL DEFAULT NULL COMMENT '能力范围编号',
  `clause_category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条款业务分类',
  `link_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'linked' COMMENT '关联状态',
  `link_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联原因',
  `verified_by` bigint NULL DEFAULT NULL COMMENT '核验人',
  `verified_at` datetime NULL DEFAULT NULL COMMENT '核验时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_evidence_link_object` (`evidence_object_id`) USING BTREE,
  KEY `idx_lab_evidence_link_source` (`source_object`, `source_object_no`) USING BTREE,
  KEY `idx_lab_evidence_link_biz` (`linked_biz_type`, `linked_biz_no`) USING BTREE,
  KEY `idx_lab_evidence_link_clause` (`clause_id`, `clause_category`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室证据关联';

CREATE TABLE IF NOT EXISTS `lab_review_batch` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `batch_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评审批次编码',
  `batch_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评审批次名称',
  `domain_pack_id` bigint NULL DEFAULT NULL COMMENT '检测方案包编号',
  `review_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '评审类型',
  `standard_codes` json NULL COMMENT '覆盖标准编码 JSON',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_review_batch_code_tenant_deleted` (`batch_code`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室评审材料包批次';

CREATE TABLE IF NOT EXISTS `lab_review_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `batch_id` bigint NOT NULL COMMENT '评审批次编号',
  `item_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '条目类型',
  `standard_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标准名称',
  `clause_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '条款编号',
  `clause_category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '条款分类',
  `check_point` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检查要点',
  `expected_evidence` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '期望证据',
  `owner_role` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '责任角色',
  `evidence_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '证据名称',
  `source_object` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源对象',
  `linked_object_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联对象编号',
  `link_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '关联状态',
  `nc_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'NC 编号',
  `severity` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '严重程度',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `owner` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '责任人',
  `due_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '计划完成日期',
  `capa_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'CAPA 编号',
  `root_cause` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原因分析',
  `action` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '纠正/预防措施',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_review_item_batch_type` (`batch_id`, `item_type`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室评审材料包条目';

CREATE TABLE IF NOT EXISTS `lab_template_version` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_pack_id` bigint NOT NULL COMMENT '检测方案包编号',
  `template_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板编码',
  `template_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板名称',
  `template_version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板版本',
  `template_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模板类型',
  `template_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT '模板发布状态',
  `section_schema` json NULL COMMENT '报告章节配置 JSON',
  `output_formats` json NULL COMMENT '输出格式 JSON',
  `data_source_schema` json NULL COMMENT '数据来源配置 JSON',
  `preview_schema` json NULL COMMENT '预览配置 JSON',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '启用状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_template_version_tenant_deleted` (`template_code`, `template_version`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_template_version_template_status` (`template_status`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室模板版本';

CREATE TABLE IF NOT EXISTS `lab_template_field_binding` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `template_id` bigint NOT NULL COMMENT '模板编号',
  `field_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段编码',
  `field_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段名称',
  `source_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源类型',
  `source_path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '来源路径',
  `required_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否必填',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_template_field_tenant_deleted` (`template_id`, `field_code`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室模板字段绑定';

CREATE TABLE IF NOT EXISTS `lab_pack_workflow_node` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_pack_id` bigint NOT NULL COMMENT '检测方案包编号',
  `node_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点编码',
  `node_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '节点名称',
  `role_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '处理角色',
  `required_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否必经',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_pack_workflow_node_pack` (`domain_pack_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '方案包流程节点配置';

CREATE TABLE IF NOT EXISTS `lab_pack_test_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_pack_id` bigint NOT NULL COMMENT '检测方案包编号',
  `item_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测项目编码',
  `item_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测项目名称',
  `method_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法编码',
  `method_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法名称',
  `standard_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标准编码',
  `result_unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '默认单位',
  `demo_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '演示值',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_pack_test_item_pack` (`domain_pack_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '方案包检测项目配置';

CREATE TABLE IF NOT EXISTS `lab_pack_result_field` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_pack_id` bigint NOT NULL COMMENT '检测方案包编号',
  `item_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测项目编码',
  `field_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '结果字段编码',
  `field_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '结果字段名称',
  `field_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段类型',
  `unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位',
  `required_flag` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否必填',
  `min_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最小值',
  `max_value` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '最大值',
  `enum_options` json NULL COMMENT '枚举选项',
  `demo_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '演示值',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_pack_result_field_pack` (`domain_pack_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_pack_result_field_item` (`item_code`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '方案包结果字段配置';

CREATE TABLE IF NOT EXISTS `lab_pack_report_section` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_pack_id` bigint NOT NULL COMMENT '检测方案包编号',
  `section_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节编码',
  `section_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节名称',
  `source_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据来源',
  `visible_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否显示',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_pack_report_section_pack` (`domain_pack_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '方案包报告章节配置';

CREATE TABLE IF NOT EXISTS `lab_pack_sample_requirement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_pack_id` bigint NOT NULL COMMENT '检测方案包编号',
  `requirement_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '样品要求编码',
  `requirement_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '样品要求名称',
  `requirement_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '要求类型',
  `requirement_text` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '要求内容',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_pack_sample_requirement_pack` (`domain_pack_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '方案包样品要求配置';

CREATE TABLE IF NOT EXISTS `lab_pack_qc_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_pack_id` bigint NOT NULL COMMENT '检测方案包编号',
  `rule_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '质控规则编码',
  `rule_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '质控规则名称',
  `rule_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规则类型',
  `rule_expression` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规则表达式',
  `acceptance_criteria` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接收准则',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_pack_qc_rule_pack` (`domain_pack_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '方案包质控规则配置';

CREATE TABLE IF NOT EXISTS `lab_pack_evidence_requirement` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `domain_pack_id` bigint NOT NULL COMMENT '检测方案包编号',
  `requirement_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据要求编码',
  `requirement_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '证据要求名称',
  `evidence_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '证据类型',
  `source_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '来源类型',
  `clause_category` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '条款类别',
  `required_flag` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否必需',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'active' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_pack_evidence_requirement_pack` (`domain_pack_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_pack_evidence_requirement_type` (`evidence_type`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '方案包证据要求配置';

INSERT INTO `system_dict_type` (`name`, `type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `deleted_time`)
SELECT '实验室领域状态', 'lab_domain_status', 0, '实验室检测领域启用状态', 'admin', NOW(), '', NOW(), b'0', NULL
WHERE NOT EXISTS (
  SELECT 1 FROM `system_dict_type` WHERE `type` = 'lab_domain_status' AND `deleted` = b'0'
);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 1, '启用', 'active', 'lab_domain_status', 0, 'success', '', '可用于业务流转', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'lab_domain_status' AND `value` = 'active' AND `deleted` = b'0'
);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 2, '草稿', 'draft', 'lab_domain_status', 0, 'info', '', '配置编制中', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'lab_domain_status' AND `value` = 'draft' AND `deleted` = b'0'
);

INSERT INTO `system_dict_data` (`sort`, `label`, `value`, `dict_type`, `status`, `color_type`, `css_class`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 3, '停用', 'disabled', 'lab_domain_status', 0, 'danger', '', '不再用于新业务', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_dict_data` WHERE `dict_type` = 'lab_domain_status' AND `value` = 'disabled' AND `deleted` = b'0'
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '实验室平台', '', 1, 35, 0, '/lab', 'ep:operation', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `name` = '实验室平台' AND `path` = '/lab' AND `deleted` = b'0'
);

SET @lab_root_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `name` = '实验室平台' AND `path` = '/lab' AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测方向包配置', '', 1, 20, @lab_root_menu_id, 'config', 'ep:box', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `path` = 'config' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
);

SET @lab_config_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `path` = 'config' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '实验室看板', 'lab:dashboard:query', 2, 5, @lab_config_menu_id, 'dashboard', 'ep:data-analysis', 'lab/dashboard/index', 'LabDashboard', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:dashboard:query' AND `deleted` = b'0'
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测领域', 'lab:domain:query', 2, 10, @lab_config_menu_id, 'domain', 'ep:collection-tag', 'lab/domain/index', 'LabDomain', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:domain:query' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0'
);

SET @lab_domain_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `permission` = 'lab:domain:query' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测领域新增', 'lab:domain:create', 3, 1, @lab_domain_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:domain:create' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测领域修改', 'lab:domain:update', 3, 2, @lab_domain_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:domain:update' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测领域删除', 'lab:domain:delete', 3, 3, @lab_domain_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:domain:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测方案包', 'lab:domain-pack:query', 2, 20, @lab_config_menu_id, 'domain-pack', 'ep:box', 'lab/domain-pack/index', 'LabDomainPack', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:domain-pack:query' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0'
);

SET @lab_domain_pack_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `permission` = 'lab:domain-pack:query' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测方案包新增', 'lab:domain-pack:create', 3, 1, @lab_domain_pack_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:domain-pack:create' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测方案包修改', 'lab:domain-pack:update', 3, 2, @lab_domain_pack_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:domain-pack:update' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测方案包删除', 'lab:domain-pack:delete', 3, 3, @lab_domain_pack_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:domain-pack:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '标准体系', 'lab:standard:query', 2, 22, @lab_config_menu_id, 'standard', 'ep:collection', 'lab/standard/index', 'LabStandard', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard:query' AND `deleted` = b'0'
);

SET @lab_standard_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `permission` = 'lab:standard:query' AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '标准新增', 'lab:standard:create', 3, 1, @lab_standard_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard:create' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '标准修改', 'lab:standard:update', 3, 2, @lab_standard_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard:update' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '标准删除', 'lab:standard:delete', 3, 3, @lab_standard_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '标准导出', 'lab:standard:export', 3, 4, @lab_standard_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款映射', 'lab:standard-clause:query', 2, 24, @lab_config_menu_id, 'standard-clause', 'ep:list', 'lab/standard-clause/index', 'LabStandardClause', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard-clause:query' AND `deleted` = b'0'
);

SET @lab_standard_clause_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `permission` = 'lab:standard-clause:query' AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款新增', 'lab:standard-clause:create', 3, 1, @lab_standard_clause_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard-clause:create' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款修改', 'lab:standard-clause:update', 3, 2, @lab_standard_clause_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard-clause:update' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款删除', 'lab:standard-clause:delete', 3, 3, @lab_standard_clause_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard-clause:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款导出', 'lab:standard-clause:export', 3, 4, @lab_standard_clause_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:standard-clause:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据对象', 'lab:evidence-object:query', 2, 25, @lab_config_menu_id, 'evidence-object', 'ep:files', 'lab/evidence-object/index', 'LabEvidenceObject', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-object:query' AND `deleted` = b'0'
);

SET @lab_evidence_object_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `permission` = 'lab:evidence-object:query' AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据对象新增', 'lab:evidence-object:create', 3, 1, @lab_evidence_object_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-object:create' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据对象修改', 'lab:evidence-object:update', 3, 2, @lab_evidence_object_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-object:update' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据对象删除', 'lab:evidence-object:delete', 3, 3, @lab_evidence_object_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-object:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据关联', 'lab:evidence-link:query', 2, 26, @lab_config_menu_id, 'evidence-link', 'ep:connection', 'lab/evidence-link/index', 'LabEvidenceLink', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-link:query' AND `deleted` = b'0'
);

SET @lab_evidence_link_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `permission` = 'lab:evidence-link:query' AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据关联新增', 'lab:evidence-link:create', 3, 1, @lab_evidence_link_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-link:create' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据关联修改', 'lab:evidence-link:update', 3, 2, @lab_evidence_link_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-link:update' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据关联删除', 'lab:evidence-link:delete', 3, 3, @lab_evidence_link_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-link:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据关联导出', 'lab:evidence-link:export', 3, 4, @lab_evidence_link_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:evidence-link:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '模板编制', 'lab:template:query', 2, 28, @lab_config_menu_id, 'template', 'ep:tickets', 'lab/template/index', 'LabTemplate', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:template:query' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0'
);

SET @lab_template_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `permission` = 'lab:template:query' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '模板保存', 'lab:template:save', 3, 1, @lab_template_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:template:save' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '报告预览', 'lab:template:preview', 3, 2, @lab_template_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:template:preview' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '评审材料包', 'lab:review-package:query', 2, 30, @lab_config_menu_id, 'review-package', 'ep:document-checked', 'lab/review-package/index', 'LabReviewPackage', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:review-package:query' AND `deleted` = b'0'
);

SET @lab_review_package_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `permission` = 'lab:review-package:query' AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '评审材料包导出', 'lab:review-package:export', 3, 1, @lab_review_package_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:review-package:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '评审材料包新增', 'lab:review-package:create', 3, 2, @lab_review_package_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:review-package:create' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '评审材料包修改', 'lab:review-package:update', 3, 3, @lab_review_package_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:review-package:update' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '评审材料包删除', 'lab:review-package:delete', 3, 4, @lab_review_package_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:review-package:delete' AND `deleted` = b'0');

INSERT INTO `lab_domain_profile` (`domain_code`, `domain_name`, `description`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'FOOD', '食品检测', '用于食品理化、微生物、污染物、营养成分等检测方向的流程与模板编制。', 'active', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (
  SELECT 1 FROM `lab_domain_profile` WHERE `domain_code` = 'FOOD' AND `tenant_id` = 1 AND `deleted` = b'0'
);

INSERT INTO `lab_domain_profile` (`domain_code`, `domain_name`, `description`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'INDUSTRIAL', '工业品检测', '用于材料、零部件、机械性能、可靠性等工业品检测方向的流程与模板编制。', 'active', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (
  SELECT 1 FROM `lab_domain_profile` WHERE `domain_code` = 'INDUSTRIAL' AND `tenant_id` = 1 AND `deleted` = b'0'
);

INSERT INTO `lab_domain_profile` (`domain_code`, `domain_name`, `description`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'ENVIRONMENT', '环境检测', '用于水、气、土壤、噪声等环境检测方向的流程与模板编制。', 'active', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (
  SELECT 1 FROM `lab_domain_profile` WHERE `domain_code` = 'ENVIRONMENT' AND `tenant_id` = 1 AND `deleted` = b'0'
);

INSERT INTO `lab_domain_pack` (`domain_id`, `pack_code`, `pack_name`, `pack_version`, `industry`, `application_scope`, `workflow_schema`, `template_schema`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, 'FOOD_ROUTINE_V1', '食品常规检测方案包', '1.0', '食品', '食品理化、微生物、污染物等常规检测场景',
       JSON_OBJECT('stages', JSON_ARRAY('request', 'sample', 'task', 'raw_record', 'review', 'report')),
       JSON_OBJECT('templates', JSON_ARRAY('sample_label', 'raw_record', 'report')),
       'published', '领域差异通过方案包配置承载，业务执行只引用方案包。', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_profile`
WHERE `domain_code` = 'FOOD' AND `tenant_id` = 1 AND `deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_domain_pack` WHERE `pack_code` = 'FOOD_ROUTINE_V1' AND `tenant_id` = 1 AND `deleted` = b'0'
  )
LIMIT 1;

INSERT INTO `lab_domain_pack` (`domain_id`, `pack_code`, `pack_name`, `pack_version`, `industry`, `application_scope`, `workflow_schema`, `template_schema`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, 'ENVIRONMENT_ROUTINE_V1', '环境常规检测方案包', '1.0', '环境', '水、气、土壤、噪声等环境检测场景',
       JSON_OBJECT('stages', JSON_ARRAY('request', 'sampling', 'sample', 'task', 'environment_trace', 'raw_record', 'review', 'report')),
       JSON_OBJECT('templates', JSON_ARRAY('sampling_record', 'raw_record', 'report')),
       'published', '用于沉淀环境方向采样、环境记录、原始数据和报告模板差异。', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_profile`
WHERE `domain_code` = 'ENVIRONMENT' AND `tenant_id` = 1 AND `deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_domain_pack` WHERE `pack_code` = 'ENVIRONMENT_ROUTINE_V1' AND `tenant_id` = 1 AND `deleted` = b'0'
  )
LIMIT 1;

INSERT INTO `lab_standard` (`standard_code`, `standard_name`, `standard_version`, `standard_type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'ISO_IEC_17025', 'ISO/IEC 17025', '2017', 'accreditation', 'active', '检测和校准实验室能力通用要求', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_standard` WHERE `standard_code` = 'ISO_IEC_17025' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_standard` (`standard_code`, `standard_name`, `standard_version`, `standard_type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'GB_T_27025', 'GB/T 27025', '2019', 'national', 'active', '等同采用 ISO/IEC 17025 的国家标准', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_standard` WHERE `standard_code` = 'GB_T_27025' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_standard` (`standard_code`, `standard_name`, `standard_version`, `standard_type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'CNAS_CL01', 'CNAS-CL01', '2018', 'cnas', 'active', '检测和校准实验室能力认可准则', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_standard` WHERE `standard_code` = 'CNAS_CL01' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_standard` (`standard_code`, `standard_name`, `standard_version`, `standard_type`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'CMA_2023', 'CMA 2023', '2023', 'cma', 'active', '检验检测机构资质认定评审要求', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_standard` WHERE `standard_code` = 'CMA_2023' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_evidence_type` (`evidence_code`, `evidence_name`, `source_object`, `clause_category`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'REPORT', '报告', 'lims_report', 'report', 'active', '已签发或待评审的检测报告', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_evidence_type` WHERE `evidence_code` = 'REPORT' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_evidence_type` (`evidence_code`, `evidence_name`, `source_object`, `clause_category`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'RAW_DATA', '原始数据', 'lims_raw_record', 'technical_record', 'active', '实验原始记录、采集数据和计算记录', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_evidence_type` WHERE `evidence_code` = 'RAW_DATA' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_evidence_type` (`evidence_code`, `evidence_name`, `source_object`, `clause_category`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'PERSON_AUTH', '人员授权', 'lab_person_authorization', 'personnel', 'active', '岗位、能力确认、授权和培训记录', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_evidence_type` WHERE `evidence_code` = 'PERSON_AUTH' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_evidence_type` (`evidence_code`, `evidence_name`, `source_object`, `clause_category`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'EQUIPMENT_CALIBRATION', '设备校准', 'lab_equipment_calibration', 'equipment', 'active', '设备检定、校准、期间核查和状态记录', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_evidence_type` WHERE `evidence_code` = 'EQUIPMENT_CALIBRATION' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_evidence_type` (`evidence_code`, `evidence_name`, `source_object`, `clause_category`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'ENVIRONMENT_RECORD', '环境记录', 'lab_environment_record', 'environment', 'active', '温湿度、洁净度、场地条件等环境监控记录', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_evidence_type` WHERE `evidence_code` = 'ENVIRONMENT_RECORD' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_evidence_type` (`evidence_code`, `evidence_name`, `source_object`, `clause_category`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'METHOD_VALIDATION', '方法验证', 'lab_method_validation', 'method', 'active', '方法确认、验证、偏离和适用性记录', 'admin', NOW(), '', NOW(), b'0', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_evidence_type` WHERE `evidence_code` = 'METHOD_VALIDATION' AND `tenant_id` = 1 AND `deleted` = b'0');

INSERT INTO `lab_standard_clause` (`standard_id`, `clause_code`, `clause_title`, `clause_category`, `requirement_text`, `evidence_type_codes`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT s.`id`, c.`clause_code`, c.`clause_title`, c.`clause_category`, c.`requirement_text`, c.`evidence_type_codes`, 'active', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_standard` s
JOIN (
  SELECT 'PERSONNEL' AS clause_code, '人员能力与授权' AS clause_title, 'personnel' AS clause_category, '人员应具备相应能力并获得授权，培训、监督和能力确认应形成记录。' AS requirement_text, JSON_ARRAY('PERSON_AUTH') AS evidence_type_codes
  UNION ALL SELECT 'EQUIPMENT', '设备计量溯源与状态控制', 'equipment', '影响结果的设备应被校准、维护、标识和状态控制。', JSON_ARRAY('EQUIPMENT_CALIBRATION')
  UNION ALL SELECT 'ENVIRONMENT', '设施与环境条件', 'environment', '实验室应监控、控制并记录影响检测结果的环境条件。', JSON_ARRAY('ENVIRONMENT_RECORD')
  UNION ALL SELECT 'METHOD', '方法选择、验证与确认', 'method', '检测方法应受控，非标或变更方法应完成验证或确认。', JSON_ARRAY('METHOD_VALIDATION')
  UNION ALL SELECT 'TECHNICAL_RECORD', '技术记录与原始数据', 'technical_record', '技术记录应包含足以复现检测活动的原始观察、数据和计算。', JSON_ARRAY('RAW_DATA')
  UNION ALL SELECT 'REPORT', '结果报告', 'report', '报告应准确、清晰、客观，并包含客户和标准要求的信息。', JSON_ARRAY('REPORT')
  UNION ALL SELECT 'NONCONFORMITY', '不符合项控制', 'nonconformity', '实验室应识别、记录、处置不符合工作并评价影响。', JSON_ARRAY('RAW_DATA', 'REPORT')
  UNION ALL SELECT 'AUDIT_REVIEW', '内审与管理评审', 'audit_review', '实验室应实施内审和管理评审，并跟踪纠正措施有效性。', JSON_ARRAY('REPORT', 'RAW_DATA')
) c
WHERE s.`standard_code` IN ('ISO_IEC_17025', 'GB_T_27025', 'CNAS_CL01', 'CMA_2023')
  AND s.`tenant_id` = 1 AND s.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_standard_clause`
    WHERE `standard_id` = s.`id` AND `clause_code` = c.`clause_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_template_version` (`domain_pack_id`, `template_code`, `template_name`, `template_version`, `template_type`, `template_status`, `section_schema`, `output_formats`, `data_source_schema`, `preview_schema`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, 'REPORT_BASIC_V1', '通用检测报告模板', '1.0', 'report',
       'published',
       JSON_ARRAY(
         JSON_OBJECT('sectionCode', 'cover', 'sectionName', '封面', 'sourceType', 'report', 'visible', true, 'sort', 10),
         JSON_OBJECT('sectionCode', 'sample', 'sectionName', '样品信息', 'sourceType', 'sample', 'visible', true, 'sort', 20),
         JSON_OBJECT('sectionCode', 'resultTable', 'sectionName', '检测结果', 'sourceType', 'result_values', 'visible', true, 'sort', 30),
         JSON_OBJECT('sectionCode', 'conclusion', 'sectionName', '结论', 'sourceType', 'report', 'visible', true, 'sort', 40),
         JSON_OBJECT('sectionCode', 'sign', 'sectionName', '签发', 'sourceType', 'approval', 'visible', true, 'sort', 50)
       ),
       JSON_ARRAY('WORD', 'PDF', 'EXCEL'),
       JSON_OBJECT(
         'report', '$.report',
         'sample', '$.sample',
         'resultValues', '$.resultValues',
         'evidenceObjects', '$.equipmentEvidenceSnapshots'
       ),
       JSON_OBJECT('layout', 'basic-report-preview', 'sections', JSON_ARRAY('cover', 'sample', 'result', 'sign')),
       'active', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack`
WHERE `pack_code` = 'FOOD_ROUTINE_V1' AND `tenant_id` = 1 AND `deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_template_version` WHERE `template_code` = 'REPORT_BASIC_V1' AND `template_version` = '1.0' AND `tenant_id` = 1 AND `deleted` = b'0'
  )
LIMIT 1;

UPDATE `lab_template_version`
SET `template_status` = 'published',
    `section_schema` = JSON_ARRAY(
      JSON_OBJECT('sectionCode', 'cover', 'sectionName', '封面', 'sourceType', 'report', 'visible', true, 'sort', 10),
      JSON_OBJECT('sectionCode', 'sample', 'sectionName', '样品信息', 'sourceType', 'sample', 'visible', true, 'sort', 20),
      JSON_OBJECT('sectionCode', 'resultTable', 'sectionName', '检测结果', 'sourceType', 'result_values', 'visible', true, 'sort', 30),
      JSON_OBJECT('sectionCode', 'conclusion', 'sectionName', '结论', 'sourceType', 'report', 'visible', true, 'sort', 40),
      JSON_OBJECT('sectionCode', 'sign', 'sectionName', '签发', 'sourceType', 'approval', 'visible', true, 'sort', 50)
    ),
    `output_formats` = JSON_ARRAY('WORD', 'PDF', 'EXCEL'),
    `data_source_schema` = JSON_OBJECT(
      'report', '$.report',
      'sample', '$.sample',
      'resultValues', '$.resultValues',
      'evidenceObjects', '$.equipmentEvidenceSnapshots'
    ),
    `status` = 'active'
WHERE `template_code` = 'REPORT_BASIC_V1' AND `template_version` = '1.0' AND `tenant_id` = 1 AND `deleted` = b'0';

INSERT INTO `lab_template_field_binding` (`template_id`, `field_code`, `field_name`, `source_type`, `source_path`, `required_flag`, `sort`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT t.`id`, f.`field_code`, f.`field_name`, f.`source_type`, f.`source_path`, f.`required_flag`, f.`sort`, 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_template_version` t
JOIN (
  SELECT 'REPORT_NO' AS field_code, '报告编号' AS field_name, 'report' AS source_type, '$.reportNo' AS source_path, b'1' AS required_flag, 10 AS sort
  UNION ALL SELECT 'SAMPLE_NAME', '样品名称', 'sample', '$.sample.name', b'1', 20
  UNION ALL SELECT 'TEST_RESULT', '检测结果', 'result', '$.result.items', b'1', 30
  UNION ALL SELECT 'APPROVER', '批准人', 'personnel', '$.approval.approverName', b'1', 40
) f
WHERE t.`template_code` = 'REPORT_BASIC_V1' AND t.`template_version` = '1.0' AND t.`tenant_id` = 1 AND t.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_template_field_binding`
    WHERE `template_id` = t.`id` AND `field_code` = f.`field_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_evidence_object` (`evidence_code`, `evidence_name`, `evidence_type`, `source_object`, `source_object_id`, `source_object_no`, `business_domain`, `file_url`, `file_name`, `file_format`, `evidence_hash`, `issued_by`, `issued_at`, `valid_from`, `valid_to`, `status`, `summary`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT CONCAT('OBJ-', e.`evidence_code`, '-', d.`source_object_no`), e.`evidence_name`, e.`evidence_code`, e.`source_object`, NULL,
       d.`source_object_no`, e.`clause_category`, NULL, NULL, NULL,
       SHA2(CONCAT(e.`evidence_code`, '|', d.`source_object_no`, '|RP-MVP-001'), 256),
       '系统种子', CURDATE(), CURDATE(), NULL, 'effective', d.`remark`, d.`remark`, 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_evidence_type` e
JOIN (
  SELECT 'REPORT' AS evidence_code, 'RPT-2026-MVP-001' AS source_object_no, '报告可作为报告条款证据' AS remark
  UNION ALL SELECT 'RAW_DATA', 'RAW-2026-MVP-001', '原始数据可作为技术记录证据'
  UNION ALL SELECT 'PERSON_AUTH', 'AUTH-TECH-001', '人员授权可作为人员能力证据'
  UNION ALL SELECT 'EQUIPMENT_CALIBRATION', 'CAL-EQ-001', '设备校准可作为设备溯源证据'
  UNION ALL SELECT 'ENVIRONMENT_RECORD', 'ENV-REC-001', '环境记录可作为设施环境证据'
  UNION ALL SELECT 'METHOD_VALIDATION', 'MV-001', '方法验证可作为方法确认/验证证据'
) d ON d.`evidence_code` = e.`evidence_code`
WHERE e.`tenant_id` = 1 AND e.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_evidence_object`
    WHERE `evidence_code` = CONCAT('OBJ-', e.`evidence_code`, '-', d.`source_object_no`)
      AND `tenant_id` = 1
      AND `deleted` = b'0'
  );

INSERT INTO `lab_evidence_link` (`evidence_object_id`, `evidence_code`, `evidence_name`, `evidence_url`, `evidence_hash`, `source_object`, `source_object_id`, `source_object_no`, `linked_biz_type`, `linked_biz_id`, `linked_biz_no`, `clause_id`, `clause_category`, `link_status`, `link_reason`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT o.`id`, o.`evidence_code`, o.`evidence_name`, o.`file_url`, o.`evidence_hash`, o.`source_object`, o.`source_object_id`, o.`source_object_no`,
       'review_package', NULL, 'RP-MVP-001', c.`id`, o.`business_domain`, 'linked', d.`remark`, d.`remark`, 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_evidence_object` o
JOIN (
  SELECT 'REPORT' AS evidence_type, 'RPT-2026-MVP-001' AS source_object_no, '报告可作为报告条款证据' AS remark
  UNION ALL SELECT 'RAW_DATA', 'RAW-2026-MVP-001', '原始数据可作为技术记录证据'
  UNION ALL SELECT 'PERSON_AUTH', 'AUTH-TECH-001', '人员授权可作为人员能力证据'
  UNION ALL SELECT 'EQUIPMENT_CALIBRATION', 'CAL-EQ-001', '设备校准可作为设备溯源证据'
  UNION ALL SELECT 'ENVIRONMENT_RECORD', 'ENV-REC-001', '环境记录可作为设施环境证据'
  UNION ALL SELECT 'METHOD_VALIDATION', 'MV-001', '方法验证可作为方法确认/验证证据'
) d ON d.`evidence_type` = o.`evidence_type` AND d.`source_object_no` = o.`source_object_no`
LEFT JOIN (
  SELECT `clause_category`, MIN(`id`) AS `id`
  FROM `lab_standard_clause`
  WHERE `tenant_id` = 1 AND `deleted` = b'0'
  GROUP BY `clause_category`
) c ON c.`clause_category` = o.`business_domain`
WHERE o.`tenant_id` = 1 AND o.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_evidence_link`
    WHERE `evidence_object_id` = o.`id`
      AND `linked_biz_no` = 'RP-MVP-001'
      AND `tenant_id` = 1
      AND `deleted` = b'0'
  );

INSERT INTO `lab_review_batch` (`batch_code`, `batch_name`, `domain_pack_id`, `review_type`, `standard_codes`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT 'RP-MVP-001', 'MVP 评审材料包', p.`id`, 'accreditation',
       JSON_ARRAY('ISO_IEC_17025', 'GB_T_27025', 'CNAS_CL01', 'CMA_2023'),
       'active', '用于验证检查表、证据清单、NC 清单、CAPA 清单的持久化导出。', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack` p
WHERE p.`pack_code` = 'FOOD_ROUTINE_V1' AND p.`tenant_id` = 1 AND p.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_review_batch` WHERE `batch_code` = 'RP-MVP-001' AND `tenant_id` = 1 AND `deleted` = b'0'
  )
LIMIT 1;

SET @lab_review_batch_id := (
  SELECT `id` FROM `lab_review_batch`
  WHERE `batch_code` = 'RP-MVP-001' AND `tenant_id` = 1 AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `lab_review_item` (`batch_id`, `item_type`, `standard_name`, `clause_code`, `clause_category`, `check_point`, `expected_evidence`, `owner_role`, `sort`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT @lab_review_batch_id, d.`item_type`, d.`standard_name`, d.`clause_code`, d.`clause_category`, d.`check_point`, d.`expected_evidence`, d.`owner_role`, d.`sort`, 'admin', NOW(), '', NOW(), b'0', 1
FROM (
  SELECT 'checklist' AS item_type, 'ISO/IEC 17025' AS standard_name, 'PERSONNEL' AS clause_code, '人员' AS clause_category, '确认人员能力、授权、培训和监督记录完整' AS check_point, '人员授权' AS expected_evidence, '技术负责人' AS owner_role, 10 AS sort
  UNION ALL SELECT 'checklist', 'ISO/IEC 17025', 'EQUIPMENT', '设备', '确认影响结果的设备完成校准、维护和状态标识', '设备校准', '设备管理员', 20
  UNION ALL SELECT 'checklist', 'CNAS-CL01', 'TECHNICAL_RECORD', '技术记录', '确认原始数据足以复现检测过程和计算过程', '原始数据', '检测员', 30
  UNION ALL SELECT 'checklist', 'CMA 2023', 'REPORT', '报告', '确认报告内容、签发、修改和归档满足要求', '报告', '授权签字人', 40
) d
WHERE @lab_review_batch_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM `lab_review_item`
    WHERE `batch_id` = @lab_review_batch_id AND `item_type` = d.`item_type` AND `clause_code` = d.`clause_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_review_item` (`batch_id`, `item_type`, `evidence_name`, `source_object`, `linked_object_no`, `clause_category`, `link_status`, `remark`, `sort`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT @lab_review_batch_id, 'evidence', d.`evidence_name`, d.`source_object`, d.`linked_object_no`, d.`clause_category`, d.`link_status`, d.`remark`, d.`sort`, 'admin', NOW(), '', NOW(), b'0', 1
FROM (
  SELECT '报告' AS evidence_name, 'lims_report' AS source_object, 'RPT-2026-MVP-001' AS linked_object_no, '报告' AS clause_category, '已关联' AS link_status, '报告预览和归档入口' AS remark, 10 AS sort
  UNION ALL SELECT '原始数据', 'lims_raw_record', 'RAW-2026-MVP-001', '技术记录', '已关联', '检测任务原始记录', 20
  UNION ALL SELECT '人员授权', 'lab_person_authorization', 'AUTH-TECH-001', '人员', '已关联', '人员能力与授权', 30
  UNION ALL SELECT '设备校准', 'lab_equipment_calibration', 'CAL-EQ-001', '设备', '已关联', '校准证书和期间核查', 40
  UNION ALL SELECT '环境记录', 'lab_environment_record', 'ENV-REC-001', '环境', '已关联', '温湿度和场地条件', 50
  UNION ALL SELECT '方法验证', 'lab_method_validation', 'MV-001', '方法', '已关联', '方法确认/验证记录', 60
) d
WHERE @lab_review_batch_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM `lab_review_item`
    WHERE `batch_id` = @lab_review_batch_id AND `item_type` = 'evidence' AND `linked_object_no` = d.`linked_object_no` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_review_item` (`batch_id`, `item_type`, `nc_no`, `clause_code`, `severity`, `description`, `owner`, `due_date`, `sort`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT @lab_review_batch_id, 'nc', d.`nc_no`, d.`clause_code`, d.`severity`, d.`description`, d.`owner`, d.`due_date`, d.`sort`, 'admin', NOW(), '', NOW(), b'0', 1
FROM (
  SELECT 'NC-MVP-001' AS nc_no, 'TECHNICAL_RECORD' AS clause_code, '一般' AS severity, '部分原始记录缺少复核人签名' AS description, '质量负责人' AS owner, '2026-07-15' AS due_date, 10 AS sort
  UNION ALL SELECT 'NC-MVP-002', 'EQUIPMENT', '观察项', '设备状态标签更新不及时', '设备管理员', '2026-07-20', 20
) d
WHERE @lab_review_batch_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM `lab_review_item`
    WHERE `batch_id` = @lab_review_batch_id AND `item_type` = 'nc' AND `nc_no` = d.`nc_no` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_review_item` (`batch_id`, `item_type`, `capa_no`, `nc_no`, `root_cause`, `action`, `owner`, `status`, `sort`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT @lab_review_batch_id, 'capa', d.`capa_no`, d.`nc_no`, d.`root_cause`, d.`action`, d.`owner`, d.`status`, d.`sort`, 'admin', NOW(), '', NOW(), b'0', 1
FROM (
  SELECT 'CAPA-MVP-001' AS capa_no, 'NC-MVP-001' AS nc_no, '原始记录复核流程缺少系统提醒' AS root_cause, '增加记录复核待办和签名完整性检查' AS action, '质量负责人' AS owner, '进行中' AS status, 10 AS sort
  UNION ALL SELECT 'CAPA-MVP-002', 'NC-MVP-002', '设备状态维护责任未绑定到周期任务', '建立设备状态周期检查任务', '设备管理员', '计划中', 20
) d
WHERE @lab_review_batch_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1 FROM `lab_review_item`
    WHERE `batch_id` = @lab_review_batch_id AND `item_type` = 'capa' AND `capa_no` = d.`capa_no` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_domain_pack` (`domain_id`, `pack_code`, `pack_name`, `pack_version`, `industry`, `application_scope`, `workflow_schema`, `template_schema`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT `id`, 'INDUSTRIAL_RELIABILITY_V1', '工业品可靠性检测方案包', '1.0', '工业品', '材料、零部件、可靠性与性能检测场景',
       JSON_OBJECT('stages', JSON_ARRAY('request', 'sample', 'task', 'equipment_trace', 'raw_record', 'review', 'report')),
       JSON_OBJECT('templates', JSON_ARRAY('equipment_usage', 'raw_record', 'report')),
       'published', '用于沉淀工业品方向的流程、设备、模板和数据采集差异。', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_profile`
WHERE `domain_code` = 'INDUSTRIAL' AND `tenant_id` = 1 AND `deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_domain_pack` WHERE `pack_code` = 'INDUSTRIAL_RELIABILITY_V1' AND `tenant_id` = 1 AND `deleted` = b'0'
  )
LIMIT 1;


-- Compliance update tables and menus.

CREATE TABLE IF NOT EXISTS `lab_clause_function_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `clause_id` bigint NULL DEFAULT NULL COMMENT 'clause_id',
  `module_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'module_code',
  `module_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'module_name',
  `function_code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'function_code',
  `function_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'function_name',
  `evidence_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'evidence_type',
  `evidence_table` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'evidence_table',
  `evidence_description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'evidence_description',
  `required_flag` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'required_flag',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_clause_function_mapping_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_compliance_check` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `check_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_no',
  `check_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_name',
  `check_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_type',
  `standard_id` bigint NULL DEFAULT NULL COMMENT 'standard_id',
  `check_scope` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_scope',
  `start_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'start_date',
  `end_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'end_date',
  `responsible_user_id` bigint NULL DEFAULT NULL COMMENT 'responsible_user_id',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'summary',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_compliance_check_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_compliance_check_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `check_id` bigint NULL DEFAULT NULL COMMENT 'check_id',
  `clause_id` bigint NULL DEFAULT NULL COMMENT 'clause_id',
  `check_result` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_result',
  `evidence_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'evidence_summary',
  `evidence_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'evidence_status',
  `finding_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'finding_description',
  `severity` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'severity',
  `responsible_dept_id` bigint NULL DEFAULT NULL COMMENT 'responsible_dept_id',
  `responsible_user_id` bigint NULL DEFAULT NULL COMMENT 'responsible_user_id',
  `due_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'due_date',
  `nonconformity_id` bigint NULL DEFAULT NULL COMMENT 'nonconformity_id',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_compliance_check_item_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_personnel_competence` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NULL DEFAULT NULL COMMENT 'user_id',
  `user_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'user_name',
  `competence_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'competence_type',
  `competence_item` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'competence_item',
  `related_method_id` bigint NULL DEFAULT NULL COMMENT 'related_method_id',
  `related_equipment_id` bigint NULL DEFAULT NULL COMMENT 'related_equipment_id',
  `certificate_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'certificate_no',
  `certificate_file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'certificate_file_url',
  `valid_from` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'valid_from',
  `valid_to` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'valid_to',
  `assessment_result` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'assessment_result',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'remark',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_personnel_competence_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_personnel_authorization` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NULL DEFAULT NULL COMMENT 'user_id',
  `auth_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'auth_type',
  `auth_scope` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'auth_scope',
  `method_id` bigint NULL DEFAULT NULL COMMENT 'method_id',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT 'equipment_id',
  `authorized_by` bigint NULL DEFAULT NULL COMMENT 'authorized_by',
  `authorized_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'authorized_time',
  `valid_from` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'valid_from',
  `valid_to` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'valid_to',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'file_url',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'remark',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_personnel_authorization_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_equipment_asset` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `equipment_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备编码',
  `equipment_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '设备名称',
  `equipment_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备类型',
  `manufacturer` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '制造商',
  `model` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '型号',
  `serial_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '序列号',
  `lab_area` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实验区域',
  `domain_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检测方向编码',
  `capability_scope` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '能力范围',
  `responsible_user_id` bigint NULL DEFAULT NULL COMMENT '责任人',
  `calibration_valid_until` date NULL DEFAULT NULL COMMENT '校准有效期',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'enabled' COMMENT '状态',
  `iot_enabled` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否启用物联',
  `iot_product_id` bigint NULL DEFAULT NULL COMMENT 'IoT产品编号',
  `iot_device_id` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'IoT设备编号',
  `data_source_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '数据来源类型',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lab_equipment_asset_code_tenant_deleted` (`equipment_code`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_equipment_asset_status` (`status`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_equipment_asset_domain` (`domain_code`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室设备主档';

CREATE TABLE IF NOT EXISTS `lab_equipment_traceability` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT 'equipment_id',
  `traceability_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'traceability_type',
  `certificate_no` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'certificate_no',
  `calibration_org` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'calibration_org',
  `calibration_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'calibration_date',
  `valid_to` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'valid_to',
  `result` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'result',
  `uncertainty` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'uncertainty',
  `traceability_chain` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'traceability_chain',
  `certificate_file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'certificate_file_url',
  `next_due_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'next_due_date',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_equipment_traceability_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lab_equipment_traceability_equipment` (`equipment_id`, `status`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_equipment_intermediate_check` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT 'equipment_id',
  `check_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_no',
  `check_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_date',
  `check_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_method',
  `check_result` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'check_result',
  `check_record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'check_record',
  `checker_id` bigint NULL DEFAULT NULL COMMENT 'checker_id',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT 'reviewer_id',
  `file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'file_url',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_equipment_intermediate_check_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_environment_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `area_id` bigint NULL DEFAULT NULL COMMENT 'area_id',
  `area_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'area_name',
  `record_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'record_time',
  `temperature` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'temperature',
  `humidity` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'humidity',
  `pressure` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'pressure',
  `cleanliness` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'cleanliness',
  `other_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'other_params',
  `data_source` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'data_source',
  `recorder_id` bigint NULL DEFAULT NULL COMMENT 'recorder_id',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `abnormal_description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'abnormal_description',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_environment_record_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_method_validation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `method_id` bigint NULL DEFAULT NULL COMMENT 'method_id',
  `validation_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'validation_no',
  `validation_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'validation_type',
  `validation_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'validation_date',
  `validation_items` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'validation_items',
  `conclusion` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'conclusion',
  `responsible_user_id` bigint NULL DEFAULT NULL COMMENT 'responsible_user_id',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT 'reviewer_id',
  `report_file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'report_file_url',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'remark',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_method_validation_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_nonconformity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `nc_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'nc_no',
  `source_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'source_type',
  `source_id` bigint NULL DEFAULT NULL COMMENT 'source_id',
  `clause_id` bigint NULL DEFAULT NULL COMMENT 'clause_id',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'title',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'description',
  `severity` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'severity',
  `responsible_dept_id` bigint NULL DEFAULT NULL COMMENT 'responsible_dept_id',
  `responsible_user_id` bigint NULL DEFAULT NULL COMMENT 'responsible_user_id',
  `discovered_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'discovered_date',
  `due_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'due_date',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_nonconformity_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_corrective_action` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `action_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'action_no',
  `nonconformity_id` bigint NULL DEFAULT NULL COMMENT 'nonconformity_id',
  `root_cause` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'root_cause',
  `correction` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'correction',
  `corrective_action` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'corrective_action',
  `preventive_action` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'preventive_action',
  `responsible_user_id` bigint NULL DEFAULT NULL COMMENT 'responsible_user_id',
  `planned_finish_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'planned_finish_date',
  `actual_finish_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'actual_finish_date',
  `verification_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'verification_result',
  `verifier_id` bigint NULL DEFAULT NULL COMMENT 'verifier_id',
  `verified_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'verified_time',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_corrective_action_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_internal_audit` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `audit_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'audit_no',
  `audit_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'audit_name',
  `audit_scope` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'audit_scope',
  `audit_criteria` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'audit_criteria',
  `audit_leader_id` bigint NULL DEFAULT NULL COMMENT 'audit_leader_id',
  `planned_start_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'planned_start_date',
  `planned_end_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'planned_end_date',
  `actual_start_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'actual_start_date',
  `actual_end_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'actual_end_date',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'summary',
  `report_file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'report_file_url',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_internal_audit_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

CREATE TABLE IF NOT EXISTS `lab_management_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `review_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'review_no',
  `review_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'review_name',
  `review_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'review_date',
  `host_user_id` bigint NULL DEFAULT NULL COMMENT 'host_user_id',
  `participants` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'participants',
  `input_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'input_summary',
  `output_decision` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'output_decision',
  `improvement_actions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'improvement_actions',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT 'status',
  `report_file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'report_file_url',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lab_management_review_tenant_deleted` (`tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '实验室符合性扩展表';

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款功能映射', 'lab:clause-mapping:query', 2, 32, @lab_config_menu_id, 'clause-mapping', 'ep:connection', 'lab/clause-mapping/index', 'LabClauseMapping', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:clause-mapping:query' AND `deleted` = b'0');
SET @lab_clause_mapping_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:clause-mapping:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款功能映射新增', 'lab:clause-mapping:create', 3, 1, @lab_clause_mapping_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:clause-mapping:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款功能映射修改', 'lab:clause-mapping:update', 3, 2, @lab_clause_mapping_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:clause-mapping:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款功能映射删除', 'lab:clause-mapping:delete', 3, 3, @lab_clause_mapping_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:clause-mapping:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '条款功能映射导出', 'lab:clause-mapping:export', 3, 4, @lab_clause_mapping_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:clause-mapping:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '符合性检查', 'lab:compliance-check:query', 2, 34, @lab_config_menu_id, 'compliance-check', 'ep:checked', 'lab/compliance-check/index', 'LabComplianceCheck', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check:query' AND `deleted` = b'0');
SET @lab_compliance_check_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:compliance-check:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '符合性检查新增', 'lab:compliance-check:create', 3, 1, @lab_compliance_check_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '符合性检查修改', 'lab:compliance-check:update', 3, 2, @lab_compliance_check_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '符合性检查删除', 'lab:compliance-check:delete', 3, 3, @lab_compliance_check_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '符合性检查导出', 'lab:compliance-check:export', 3, 4, @lab_compliance_check_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检查明细', 'lab:compliance-check-item:query', 2, 36, @lab_config_menu_id, 'compliance-check-item', 'ep:list', 'lab/compliance-check-item/index', 'LabComplianceCheckItem', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check-item:query' AND `deleted` = b'0');
SET @lab_compliance_check_item_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:compliance-check-item:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检查明细新增', 'lab:compliance-check-item:create', 3, 1, @lab_compliance_check_item_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check-item:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检查明细修改', 'lab:compliance-check-item:update', 3, 2, @lab_compliance_check_item_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check-item:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检查明细删除', 'lab:compliance-check-item:delete', 3, 3, @lab_compliance_check_item_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check-item:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检查明细导出', 'lab:compliance-check-item:export', 3, 4, @lab_compliance_check_item_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:compliance-check-item:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员能力', 'lab:personnel-competence:query', 2, 38, @lab_config_menu_id, 'personnel-competence', 'ep:user-filled', 'lab/personnel-competence/index', 'LabPersonnelCompetence', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-competence:query' AND `deleted` = b'0');
SET @lab_personnel_competence_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:personnel-competence:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员能力新增', 'lab:personnel-competence:create', 3, 1, @lab_personnel_competence_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-competence:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员能力修改', 'lab:personnel-competence:update', 3, 2, @lab_personnel_competence_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-competence:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员能力删除', 'lab:personnel-competence:delete', 3, 3, @lab_personnel_competence_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-competence:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员能力导出', 'lab:personnel-competence:export', 3, 4, @lab_personnel_competence_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-competence:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员授权', 'lab:personnel-authorization:query', 2, 40, @lab_config_menu_id, 'personnel-authorization', 'ep:stamp', 'lab/personnel-authorization/index', 'LabPersonnelAuthorization', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-authorization:query' AND `deleted` = b'0');
SET @lab_personnel_authorization_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:personnel-authorization:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员授权新增', 'lab:personnel-authorization:create', 3, 1, @lab_personnel_authorization_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-authorization:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员授权修改', 'lab:personnel-authorization:update', 3, 2, @lab_personnel_authorization_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-authorization:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员授权删除', 'lab:personnel-authorization:delete', 3, 3, @lab_personnel_authorization_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-authorization:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '人员授权导出', 'lab:personnel-authorization:export', 3, 4, @lab_personnel_authorization_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:personnel-authorization:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备主档', 'lab:equipment-asset:query', 2, 42, @lab_config_menu_id, 'equipment-asset', 'ep:cpu', 'lab/equipment-asset/index', 'LabEquipmentAsset', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-asset:query' AND `deleted` = b'0');
SET @lab_equipment_asset_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:equipment-asset:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备主档新增', 'lab:equipment-asset:create', 3, 1, @lab_equipment_asset_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-asset:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备主档修改', 'lab:equipment-asset:update', 3, 2, @lab_equipment_asset_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-asset:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备主档删除', 'lab:equipment-asset:delete', 3, 3, @lab_equipment_asset_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-asset:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备溯源', 'lab:equipment-traceability:query', 2, 42, @lab_config_menu_id, 'equipment-traceability', 'ep:odometer', 'lab/equipment-traceability/index', 'LabEquipmentTraceability', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-traceability:query' AND `deleted` = b'0');
SET @lab_equipment_traceability_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:equipment-traceability:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备溯源新增', 'lab:equipment-traceability:create', 3, 1, @lab_equipment_traceability_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-traceability:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备溯源修改', 'lab:equipment-traceability:update', 3, 2, @lab_equipment_traceability_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-traceability:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备溯源删除', 'lab:equipment-traceability:delete', 3, 3, @lab_equipment_traceability_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-traceability:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '设备溯源导出', 'lab:equipment-traceability:export', 3, 4, @lab_equipment_traceability_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-traceability:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '期间核查', 'lab:equipment-intermediate-check:query', 2, 44, @lab_config_menu_id, 'equipment-intermediate-check', 'ep:finished', 'lab/equipment-intermediate-check/index', 'LabEquipmentIntermediateCheck', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-intermediate-check:query' AND `deleted` = b'0');
SET @lab_equipment_intermediate_check_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:equipment-intermediate-check:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '期间核查新增', 'lab:equipment-intermediate-check:create', 3, 1, @lab_equipment_intermediate_check_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-intermediate-check:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '期间核查修改', 'lab:equipment-intermediate-check:update', 3, 2, @lab_equipment_intermediate_check_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-intermediate-check:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '期间核查删除', 'lab:equipment-intermediate-check:delete', 3, 3, @lab_equipment_intermediate_check_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-intermediate-check:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '期间核查导出', 'lab:equipment-intermediate-check:export', 3, 4, @lab_equipment_intermediate_check_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:equipment-intermediate-check:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '环境记录', 'lab:environment-record:query', 2, 46, @lab_config_menu_id, 'environment-record', 'ep:sunny', 'lab/environment-record/index', 'LabEnvironmentRecord', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:environment-record:query' AND `deleted` = b'0');
SET @lab_environment_record_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:environment-record:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '环境记录新增', 'lab:environment-record:create', 3, 1, @lab_environment_record_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:environment-record:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '环境记录修改', 'lab:environment-record:update', 3, 2, @lab_environment_record_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:environment-record:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '环境记录删除', 'lab:environment-record:delete', 3, 3, @lab_environment_record_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:environment-record:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '环境记录导出', 'lab:environment-record:export', 3, 4, @lab_environment_record_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:environment-record:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '方法验证', 'lab:method-validation:query', 2, 48, @lab_config_menu_id, 'method-validation', 'ep:document-checked', 'lab/method-validation/index', 'LabMethodValidation', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:method-validation:query' AND `deleted` = b'0');
SET @lab_method_validation_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:method-validation:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '方法验证新增', 'lab:method-validation:create', 3, 1, @lab_method_validation_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:method-validation:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '方法验证修改', 'lab:method-validation:update', 3, 2, @lab_method_validation_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:method-validation:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '方法验证删除', 'lab:method-validation:delete', 3, 3, @lab_method_validation_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:method-validation:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '方法验证导出', 'lab:method-validation:export', 3, 4, @lab_method_validation_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:method-validation:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '不符合项', 'lab:nonconformity:query', 2, 50, @lab_config_menu_id, 'nonconformity', 'ep:warning', 'lab/nonconformity/index', 'LabNonconformity', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:nonconformity:query' AND `deleted` = b'0');
SET @lab_nonconformity_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:nonconformity:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '不符合项新增', 'lab:nonconformity:create', 3, 1, @lab_nonconformity_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:nonconformity:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '不符合项修改', 'lab:nonconformity:update', 3, 2, @lab_nonconformity_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:nonconformity:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '不符合项删除', 'lab:nonconformity:delete', 3, 3, @lab_nonconformity_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:nonconformity:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '不符合项导出', 'lab:nonconformity:export', 3, 4, @lab_nonconformity_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:nonconformity:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '纠正措施', 'lab:corrective-action:query', 2, 52, @lab_config_menu_id, 'corrective-action', 'ep:circle-check', 'lab/corrective-action/index', 'LabCorrectiveAction', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:corrective-action:query' AND `deleted` = b'0');
SET @lab_corrective_action_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:corrective-action:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '纠正措施新增', 'lab:corrective-action:create', 3, 1, @lab_corrective_action_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:corrective-action:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '纠正措施修改', 'lab:corrective-action:update', 3, 2, @lab_corrective_action_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:corrective-action:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '纠正措施删除', 'lab:corrective-action:delete', 3, 3, @lab_corrective_action_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:corrective-action:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '纠正措施导出', 'lab:corrective-action:export', 3, 4, @lab_corrective_action_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:corrective-action:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '内部审核', 'lab:internal-audit:query', 2, 54, @lab_config_menu_id, 'internal-audit', 'ep:search', 'lab/internal-audit/index', 'LabInternalAudit', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:internal-audit:query' AND `deleted` = b'0');
SET @lab_internal_audit_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:internal-audit:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '内部审核新增', 'lab:internal-audit:create', 3, 1, @lab_internal_audit_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:internal-audit:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '内部审核修改', 'lab:internal-audit:update', 3, 2, @lab_internal_audit_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:internal-audit:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '内部审核删除', 'lab:internal-audit:delete', 3, 3, @lab_internal_audit_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:internal-audit:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '内部审核导出', 'lab:internal-audit:export', 3, 4, @lab_internal_audit_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:internal-audit:export' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '管理评审', 'lab:management-review:query', 2, 56, @lab_config_menu_id, 'management-review', 'ep:management', 'lab/management-review/index', 'LabManagementReview', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:management-review:query' AND `deleted` = b'0');
SET @lab_management_review_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lab:management-review:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '管理评审新增', 'lab:management-review:create', 3, 1, @lab_management_review_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:management-review:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '管理评审修改', 'lab:management-review:update', 3, 2, @lab_management_review_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:management-review:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '管理评审删除', 'lab:management-review:delete', 3, 3, @lab_management_review_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:management-review:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '管理评审导出', 'lab:management-review:export', 3, 4, @lab_management_review_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:management-review:export' AND `deleted` = b'0');

INSERT INTO `lab_compliance_check` (`check_no`, `check_name`, `check_type`, `standard_id`, `check_scope`, `status`, `summary`, `creator`, `tenant_id`)
SELECT 'CHK202606130001', 'CNAS符合性自查', 'cnas', (SELECT id FROM lab_standard WHERE standard_code='ISO_IEC_17025' AND deleted=b'0' ORDER BY id LIMIT 1), 'ISO/IEC 17025核心条款', 'draft', '用于端到端验收的符合性检查种子', 'admin', 1
WHERE NOT EXISTS (SELECT 1 FROM `lab_compliance_check` WHERE `check_no` = 'CHK202606130001' AND `deleted` = b'0');

INSERT INTO `lab_clause_function_mapping` (`clause_id`, `module_code`, `module_name`, `function_code`, `function_name`, `evidence_type`, `evidence_table`, `evidence_description`, `required_flag`, `creator`, `tenant_id`)
SELECT c.id, 'equipment_traceability', '设备计量溯源', 'calibration_certificate', '校准证书维护', 'equipment', 'lab_equipment_traceability', '设备条款需要校准/检定证书作为证据', '1', 'admin', 1
FROM lab_standard_clause c
WHERE (c.clause_code = '6.4' OR c.clause_category = 'equipment') AND c.deleted = b'0'
  AND NOT EXISTS (SELECT 1 FROM lab_clause_function_mapping m WHERE m.clause_id = c.id AND m.module_code = 'equipment_traceability' AND m.deleted = b'0')
ORDER BY CASE WHEN c.clause_code = '6.4' THEN 0 ELSE 1 END, c.id
LIMIT 1;

-- LIMS 最小业务闭环：检测需求 -> 样品 -> 任务 -> 结果 -> 报告
CREATE TABLE IF NOT EXISTS `lims_test_request` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `request_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测需求编号',
  `request_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测需求名称',
  `request_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'internal' COMMENT '需求类型',
  `request_source_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'INTERNAL_DEPARTMENT' COMMENT '需求来源类型',
  `customer_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '客户/部门',
  `requester_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '提出人',
  `requester_org_id` bigint NULL DEFAULT NULL COMMENT '内部委托组织编号',
  `cost_center_id` bigint NULL DEFAULT NULL COMMENT '成本中心编号',
  `commercial_order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '第三方订单编号',
  `internal_project_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '内部项目编号',
  `domain_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检测方向编码',
  `domain_pack_id` bigint NOT NULL COMMENT '检测场景方案包编号',
  `domain_pack_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检测场景方案包编码',
  `domain_pack_version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '检测场景方案包版本',
  `standard_id` bigint NULL DEFAULT NULL COMMENT '适用标准编号',
  `priority` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '优先级',
  `due_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '期望完成日期',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT '状态',
  `scenario_config` json NULL COMMENT '场景配置快照',
  `workflow_snapshot` json NULL COMMENT '方向包冻结快照',
  `workflow_snapshot_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方向包冻结快照哈希',
  `workflow_snapshot_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方向包冻结时间',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lims_test_request_no_tenant_deleted` (`request_no`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_test_request_source` (`request_source_type`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_test_request_pack` (`domain_pack_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测需求';

CREATE TABLE IF NOT EXISTS `lims_execution_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `workflow_snapshot_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方向包冻结快照哈希',
  `plan_json` json NULL COMMENT '执行计划 JSON',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'generated' COMMENT '状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_lims_execution_plan_request_tenant_deleted` (`request_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_execution_plan_hash` (`workflow_snapshot_hash`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS执行计划';

CREATE TABLE IF NOT EXISTS `lims_sample` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `request_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测需求单号',
  `sample_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '样品编号',
  `sample_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '样品名称',
  `sample_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '样品类型',
  `sample_spec` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '规格型号',
  `sample_qty` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '样品数量',
  `received_date` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '接收日期',
  `storage_condition` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '保存条件',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'received' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_sample_request` (`request_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS样品';

CREATE TABLE IF NOT EXISTS `lims_test_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `request_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测需求单号',
  `sample_id` bigint NOT NULL COMMENT '样品编号',
  `sample_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '样品单号',
  `task_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编号',
  `task_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务名称',
  `test_item` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测项目',
  `method_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法编码',
  `method_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方法名称',
  `standard_clause_id` bigint NULL DEFAULT NULL COMMENT '标准条款编号',
  `assigned_user_id` bigint NULL DEFAULT NULL COMMENT '执行人',
  `assigned_user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '执行人姓名',
  `personnel_snapshot` json NULL COMMENT '执行人员授权快照',
  `personnel_evidence_snapshot` json NULL COMMENT '执行人员证据快照',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '技术复核人',
  `duration_minutes` bigint NULL DEFAULT NULL COMMENT '预计耗时分钟',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT '设备主档编号',
  `equipment_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备编码',
  `equipment_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '设备名称',
  `equipment_snapshot` json NULL COMMENT '设备主档快照',
  `equipment_evidence_snapshot` json NULL COMMENT '设备证据快照',
  `planned_start_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '计划开始',
  `planned_end_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '计划结束',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'assigned' COMMENT '状态',
  `task_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'generated' COMMENT '任务生命周期状态',
  `schedule_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'unscheduled' COMMENT '排程状态',
  `actual_start_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实际开始时间',
  `actual_end_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '实际结束时间',
  `method_snapshot` json NULL COMMENT '方法配置快照',
  `readiness_snapshot` json NULL COMMENT '任务就绪检查快照',
  `qc_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'none' COMMENT '质控状态',
  `review_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'none' COMMENT '复核状态',
  `report_eligible` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否可进入报告',
  `block_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '阻断原因',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_task_request` (`request_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_sample` (`sample_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_equipment` (`equipment_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_lifecycle` (`task_status`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_schedule_status` (`schedule_status`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_assignee_window` (`assigned_user_id`, `planned_start_time`, `planned_end_time`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测任务';

CREATE TABLE IF NOT EXISTS `lims_task_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `sample_id` bigint NOT NULL COMMENT '样品编号',
  `equipment_id` bigint NULL DEFAULT NULL COMMENT '设备编号',
  `assigned_user_id` bigint NULL DEFAULT NULL COMMENT '执行人',
  `planned_start_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划开始时间',
  `planned_end_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '计划结束时间',
  `schedule_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'scheduled' COMMENT '排程状态',
  `conflict_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '冲突原因',
  `locked` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否锁定',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_task_schedule_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_schedule_equipment` (`equipment_id`, `planned_start_time`, `planned_end_time`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_schedule_user` (`assigned_user_id`, `planned_start_time`, `planned_end_time`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测任务排程';

CREATE TABLE IF NOT EXISTS `lims_task_raw_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `task_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编号',
  `record_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始记录类型',
  `record_json` json NULL COMMENT '原始记录内容',
  `attachment_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '附件地址',
  `version_no` bigint NOT NULL DEFAULT 1 COMMENT '版本号',
  `submitted_by` bigint NULL DEFAULT NULL COMMENT '提交人',
  `submitted_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '提交时间',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'submitted' COMMENT '记录状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_task_raw_record_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测任务原始记录';

CREATE TABLE IF NOT EXISTS `lims_task_qc_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `task_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编号',
  `qc_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '质控类型',
  `qc_rule_snapshot` json NULL COMMENT '质控规则快照',
  `qc_data_json` json NULL COMMENT '质控数据',
  `qc_result` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '质控结果',
  `review_comment` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '质控意见',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_task_qc_record_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测任务质控记录';

CREATE TABLE IF NOT EXISTS `lims_task_review` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `task_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编号',
  `review_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '复核类型',
  `review_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '复核结果',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '复核人',
  `review_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '复核时间',
  `comment` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '复核意见',
  `snapshot_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '快照哈希',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_task_review_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_review_status` (`review_status`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测任务技术复核';

CREATE TABLE IF NOT EXISTS `lims_task_event_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `task_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '任务编号',
  `event_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件类型',
  `from_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原状态',
  `to_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '目标状态',
  `operator_id` bigint NULL DEFAULT NULL COMMENT '操作人',
  `event_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '事件时间',
  `reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '原因',
  `payload_json` json NULL COMMENT '事件载荷',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_task_event_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_task_event_type` (`event_type`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测任务事件日志';

CREATE TABLE IF NOT EXISTS `lims_test_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `request_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测需求单号',
  `sample_id` bigint NOT NULL COMMENT '样品编号',
  `sample_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '样品单号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `task_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测任务单号',
  `result_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '结果编号',
  `test_item` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测项目',
  `result_value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '结果值',
  `result_unit` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位',
  `result_conclusion` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单项结论',
  `raw_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '原始数据 JSON',
  `reviewer_id` bigint NULL DEFAULT NULL COMMENT '审核人',
  `reviewed_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '审核时间',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'recorded' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_result_request` (`request_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_result_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测结果';

CREATE TABLE IF NOT EXISTS `lims_test_result_value` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `result_id` bigint NOT NULL COMMENT '结果主表编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `request_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测需求单号',
  `sample_id` bigint NOT NULL COMMENT '样品编号',
  `sample_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '样品单号',
  `task_id` bigint NOT NULL COMMENT '检测任务编号',
  `task_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测任务单号',
  `test_item` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测项目',
  `field_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段编码',
  `field_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '字段名称',
  `field_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字段类型',
  `field_value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字段值',
  `display_value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '展示值',
  `unit` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '单位',
  `conclusion` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '字段判定',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'recorded' COMMENT '状态',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_result_value_request` (`request_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_result_value_result` (`result_id`, `tenant_id`, `deleted`) USING BTREE,
  KEY `idx_lims_result_value_task` (`task_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测结果字段实例';

CREATE TABLE IF NOT EXISTS `lims_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `request_id` bigint NOT NULL COMMENT '检测需求编号',
  `request_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检测需求单号',
  `report_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '报告编号',
  `report_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '报告名称',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板编号',
  `template_version` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '模板版本',
  `report_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '报告内容 JSON',
  `data_snapshot` json NULL COMMENT '报告数据快照',
  `data_snapshot_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '报告数据快照哈希',
  `workflow_snapshot_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '方向包冻结快照哈希',
  `conclusion` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '报告结论',
  `file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '文件地址',
  `report_output` json NULL COMMENT '报告输出清单',
  `issued_time` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '签发时间',
  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'draft' COMMENT '状态',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_lims_report_request` (`request_id`, `tenant_id`, `deleted`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'LIMS检测报告';

UPDATE `lab_domain_pack`
SET `workflow_schema` = JSON_OBJECT(
      'stages', JSON_ARRAY('request', 'sample', 'task', 'raw_record', 'review', 'report'),
      'testItems', JSON_ARRAY(
        JSON_OBJECT('itemName', '感官检查', 'methodCode', 'FOOD-SENSE', 'methodName', '食品感官检查', 'resultUnit', ''),
        JSON_OBJECT('itemName', '水分', 'methodCode', 'GB5009.3', 'methodName', '食品中水分测定', 'resultUnit', '%')
      )
    ),
    `template_schema` = JSON_OBJECT(
      'templates', JSON_ARRAY('sample_label', 'raw_record', 'report'),
      'resultFields', JSON_ARRAY('resultValue', 'resultUnit', 'resultConclusion'),
      'reportSections', JSON_ARRAY('basicInfo', 'sampleInfo', 'resultTable', 'conclusion')
    ),
    `status` = 'published'
WHERE `pack_code` = 'FOOD_ROUTINE_V1' AND `tenant_id` = 1 AND `deleted` = b'0';

UPDATE `lab_domain_pack`
SET `workflow_schema` = JSON_OBJECT(
      'stages', JSON_ARRAY('request', 'sampling', 'sample', 'task', 'environment_trace', 'raw_record', 'review', 'report'),
      'testItems', JSON_ARRAY(
        JSON_OBJECT('itemName', 'pH', 'methodCode', 'HJ-1147', 'methodName', '水质 pH 测定', 'resultUnit', ''),
        JSON_OBJECT('itemName', 'COD', 'methodCode', 'HJ-828', 'methodName', '化学需氧量测定', 'resultUnit', 'mg/L')
      )
    ),
    `template_schema` = JSON_OBJECT(
      'templates', JSON_ARRAY('sampling_record', 'raw_record', 'report'),
      'resultFields', JSON_ARRAY('samplingPoint', 'resultValue', 'resultUnit', 'resultConclusion'),
      'reportSections', JSON_ARRAY('samplingInfo', 'environmentTrace', 'resultTable', 'conclusion')
    ),
    `status` = 'published'
WHERE `pack_code` = 'ENVIRONMENT_ROUTINE_V1' AND `tenant_id` = 1 AND `deleted` = b'0';

UPDATE `lab_domain_pack`
SET `workflow_schema` = JSON_OBJECT(
      'stages', JSON_ARRAY('request', 'sample', 'task', 'equipment_trace', 'raw_record', 'review', 'report'),
      'testItems', JSON_ARRAY(
        JSON_OBJECT('itemName', '尺寸检查', 'methodCode', 'DIM', 'methodName', '尺寸测量', 'resultUnit', 'mm'),
        JSON_OBJECT('itemName', '可靠性试验', 'methodCode', 'REL', 'methodName', '可靠性试验方法', 'resultUnit', '')
      )
    ),
    `template_schema` = JSON_OBJECT(
      'templates', JSON_ARRAY('equipment_usage', 'raw_record', 'report'),
      'resultFields', JSON_ARRAY('equipmentId', 'resultValue', 'resultUnit', 'resultConclusion'),
      'reportSections', JSON_ARRAY('equipmentTrace', 'resultTable', 'deviation', 'conclusion')
    ),
    `status` = 'published'
WHERE `pack_code` = 'INDUSTRIAL_RELIABILITY_V1' AND `tenant_id` = 1 AND `deleted` = b'0';

INSERT INTO `lab_pack_workflow_node` (`domain_pack_id`, `node_code`, `node_name`, `role_name`, `required_flag`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT p.`id`, d.`node_code`, d.`node_name`, d.`role_name`, d.`required_flag`, d.`sort`, 'active', '方向包发布版结构化流程节点', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack` p
JOIN (
  SELECT 'FOOD_ROUTINE_V1' AS `pack_code`, 'request_accept' AS `node_code`, '需求受理' AS `node_name`, '业务受理' AS `role_name`, b'1' AS `required_flag`, 10 AS `sort`
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'sample_receive', '样品接收', '样品管理员', b'1', 20
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'task_execute', '任务执行', '检测员', b'1', 30
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'technical_review', '技术复核', '技术负责人', b'1', 40
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'report_issue', '报告签发', '授权签字人', b'1', 50
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'request_accept', '需求受理', '业务受理', b'1', 10
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'field_sampling', '现场采样', '采样员', b'1', 20
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'task_execute', '任务执行', '检测员', b'1', 30
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'technical_review', '技术复核', '技术负责人', b'1', 40
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'report_issue', '报告签发', '授权签字人', b'1', 50
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'request_accept', '需求受理', '业务受理', b'1', 10
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'sample_receive', '样品接收', '样品管理员', b'1', 20
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'equipment_prepare', '设备准备', '设备管理员', b'1', 30
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'task_execute', '任务执行', '检测员', b'1', 40
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'report_issue', '报告签发', '授权签字人', b'1', 50
) d ON d.`pack_code` = p.`pack_code`
WHERE p.`tenant_id` = 1 AND p.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_pack_workflow_node`
    WHERE `domain_pack_id` = p.`id` AND `node_code` = d.`node_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_pack_sample_requirement` (`domain_pack_id`, `requirement_code`, `requirement_name`, `requirement_type`, `requirement_text`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT p.`id`, d.`requirement_code`, d.`requirement_name`, d.`requirement_type`, d.`requirement_text`, d.`sort`, 'active', '方向包发布版样品要求', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack` p
JOIN (
  SELECT 'FOOD_ROUTINE_V1' AS `pack_code`, 'FOOD_SAMPLE_QTY' AS `requirement_code`, '样品量' AS `requirement_name`, 'quantity' AS `requirement_type`, '不少于 500g，预包装食品保留原包装。' AS `requirement_text`, 10 AS `sort`
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'FOOD_STORAGE', '储存条件', 'storage', '按样品标签或客户要求冷藏/常温保存。', 20
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_POINT_INFO', '采样点位', 'sampling', '记录采样点位、采样时间和现场环境条件。', 10
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_CONTAINER', '采样容器', 'container', '按检测项目选择洁净容器并完成现场固定。', 20
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_SAMPLE_SPEC', '样品规格', 'specification', '记录规格型号、批次、数量和外观状态。', 10
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_PRECONDITION', '预处理条件', 'precondition', '按方法要求完成恒温、老化或状态调节。', 20
) d ON d.`pack_code` = p.`pack_code`
WHERE p.`tenant_id` = 1 AND p.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_pack_sample_requirement`
    WHERE `domain_pack_id` = p.`id` AND `requirement_code` = d.`requirement_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_pack_test_item` (`domain_pack_id`, `item_code`, `item_name`, `method_code`, `method_name`, `standard_code`, `result_unit`, `demo_value`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT p.`id`, d.`item_code`, d.`item_name`, d.`method_code`, d.`method_name`, d.`standard_code`, d.`result_unit`, d.`demo_value`, d.`sort`, 'active', '方向包发布版检测项目', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack` p
JOIN (
  SELECT 'FOOD_ROUTINE_V1' AS `pack_code`, 'FOOD_SENSE' AS `item_code`, '感官检查' AS `item_name`, 'FOOD-SENSE' AS `method_code`, '食品感官检查' AS `method_name`, 'GB 5009.237' AS `standard_code`, '' AS `result_unit`, '符合' AS `demo_value`, 10 AS `sort`
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'FOOD_MOISTURE', '水分', 'GB5009.3', '食品中水分测定', 'GB 5009.3', '%', '12.5', 20
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_PH', 'pH', 'HJ-1147', '水质 pH 测定', 'HJ 1147', '', '7.2', 10
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_COD', 'COD', 'HJ-828', '化学需氧量测定', 'HJ 828', 'mg/L', '24', 20
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_DIM', '尺寸检查', 'DIM', '尺寸测量', 'GB/T 3177', 'mm', '10.02', 10
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_REL', '可靠性试验', 'REL', '可靠性试验方法', 'GB/T 2423', '', '通过', 20
) d ON d.`pack_code` = p.`pack_code`
WHERE p.`tenant_id` = 1 AND p.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_pack_test_item`
    WHERE `domain_pack_id` = p.`id` AND `item_code` = d.`item_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_pack_result_field` (`domain_pack_id`, `item_code`, `field_code`, `field_name`, `field_type`, `unit`, `required_flag`, `min_value`, `max_value`, `enum_options`, `demo_value`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT p.`id`, d.`item_code`, d.`field_code`, d.`field_name`, d.`field_type`, d.`unit`, d.`required_flag`, d.`min_value`, d.`max_value`, d.`enum_options`, d.`demo_value`, d.`sort`, 'active', '方向包发布版结果字段', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack` p
JOIN (
  SELECT 'FOOD_ROUTINE_V1' AS `pack_code`, 'FOOD_SENSE' AS `item_code`, 'SENSE_RESULT' AS `field_code`, '感官结论' AS `field_name`, 'text' AS `field_type`, '' AS `unit`, b'1' AS `required_flag`, NULL AS `min_value`, NULL AS `max_value`, NULL AS `enum_options`, '符合' AS `demo_value`, 10 AS `sort`
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'FOOD_MOISTURE', 'MOISTURE_VALUE', '水分含量', 'number', '%', b'1', '0', '100', NULL, '12.5', 20
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_PH', 'PH_VALUE', 'pH 值', 'number', '', b'1', '0', '14', NULL, '7.2', 10
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_COD', 'COD_VALUE', 'COD', 'number', 'mg/L', b'1', '0', NULL, NULL, '24', 20
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_DIM', 'DIM_VALUE', '尺寸测量值', 'number', 'mm', b'1', '0', NULL, NULL, '10.02', 10
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_REL', 'REL_RESULT', '试验结论', 'text', '', b'1', NULL, NULL, JSON_ARRAY('通过', '不通过'), '通过', 20
) d ON d.`pack_code` = p.`pack_code`
WHERE p.`tenant_id` = 1 AND p.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_pack_result_field`
    WHERE `domain_pack_id` = p.`id` AND `field_code` = d.`field_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_pack_qc_rule` (`domain_pack_id`, `rule_code`, `rule_name`, `rule_type`, `rule_expression`, `acceptance_criteria`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT p.`id`, d.`rule_code`, d.`rule_name`, d.`rule_type`, d.`rule_expression`, d.`acceptance_criteria`, d.`sort`, 'active', '方向包发布版质控规则', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack` p
JOIN (
  SELECT 'FOOD_ROUTINE_V1' AS `pack_code`, 'FOOD_BATCH_QC' AS `rule_code`, '食品批次质控' AS `rule_name`, 'batch' AS `rule_type`, 'qcResult == approved' AS `rule_expression`, '每个任务提交通过的批次质控记录。' AS `acceptance_criteria`, 10 AS `sort`
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_FIELD_BLANK', '现场空白/平行样质控', 'batch', 'qcResult == approved', '环境检测批次应记录并通过现场质控。', 10
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_EQUIPMENT_CHECK', '设备状态确认', 'equipment', 'qcResult == approved', '试验前确认设备状态、工装和条件满足方法要求。', 10
) d ON d.`pack_code` = p.`pack_code`
WHERE p.`tenant_id` = 1 AND p.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_pack_qc_rule`
    WHERE `domain_pack_id` = p.`id` AND `rule_code` = d.`rule_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_pack_report_section` (`domain_pack_id`, `section_code`, `section_name`, `source_type`, `visible_flag`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT p.`id`, d.`section_code`, d.`section_name`, d.`source_type`, d.`visible_flag`, d.`sort`, 'active', '方向包发布版报告章节', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack` p
JOIN (
  SELECT 'FOOD_ROUTINE_V1' AS `pack_code`, 'basicInfo' AS `section_code`, '基本信息' AS `section_name`, 'request' AS `source_type`, b'1' AS `visible_flag`, 10 AS `sort`
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'sampleInfo', '样品信息', 'sample', b'1', 20
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'resultTable', '检测结果', 'result_values', b'1', 30
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'equipmentTrace', '设备溯源', 'equipment_evidence', b'1', 40
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'conclusion', '结论', 'report', b'1', 50
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'samplingInfo', '采样信息', 'sample', b'1', 10
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'environmentTrace', '环境记录', 'environment_record', b'1', 20
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'resultTable', '检测结果', 'result_values', b'1', 30
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'equipmentTrace', '设备溯源', 'equipment_evidence', b'1', 40
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'conclusion', '结论', 'report', b'1', 50
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'equipmentTrace', '设备溯源', 'equipment_evidence', b'1', 10
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'resultTable', '检测结果', 'result_values', b'1', 20
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'deviation', '偏离说明', 'review', b'1', 30
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'conclusion', '结论', 'report', b'1', 40
) d ON d.`pack_code` = p.`pack_code`
WHERE p.`tenant_id` = 1 AND p.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_pack_report_section`
    WHERE `domain_pack_id` = p.`id` AND `section_code` = d.`section_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_pack_evidence_requirement` (`domain_pack_id`, `requirement_code`, `requirement_name`, `evidence_type`, `source_type`, `clause_category`, `required_flag`, `sort`, `status`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT p.`id`, d.`requirement_code`, d.`requirement_name`, d.`evidence_type`, d.`source_type`, d.`clause_category`, d.`required_flag`, d.`sort`, 'active', '方向包发布版证据要求', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_domain_pack` p
JOIN (
  SELECT 'FOOD_ROUTINE_V1' AS `pack_code`, 'FOOD_RAW_RECORD' AS `requirement_code`, '原始记录' AS `requirement_name`, 'RAW_DATA' AS `evidence_type`, 'raw_record' AS `source_type`, 'technical_record' AS `clause_category`, b'1' AS `required_flag`, 10 AS `sort`
  UNION ALL SELECT 'FOOD_ROUTINE_V1', 'FOOD_EQUIPMENT_CAL', '设备校准证据', 'EQUIPMENT_CALIBRATION', 'equipment', 'equipment', b'1', 20
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_RAW_RECORD', '原始记录', 'RAW_DATA', 'raw_record', 'technical_record', b'1', 10
  UNION ALL SELECT 'ENVIRONMENT_ROUTINE_V1', 'ENV_EQUIPMENT_CAL', '设备校准证据', 'EQUIPMENT_CALIBRATION', 'equipment', 'equipment', b'1', 20
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_RAW_RECORD', '原始记录', 'RAW_DATA', 'raw_record', 'technical_record', b'1', 10
  UNION ALL SELECT 'INDUSTRIAL_RELIABILITY_V1', 'IND_EQUIPMENT_CAL', '设备校准证据', 'EQUIPMENT_CALIBRATION', 'equipment', 'equipment', b'1', 20
) d ON d.`pack_code` = p.`pack_code`
WHERE p.`tenant_id` = 1 AND p.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_pack_evidence_requirement`
    WHERE `domain_pack_id` = p.`id` AND `requirement_code` = d.`requirement_code` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_equipment_asset` (`equipment_code`, `equipment_name`, `equipment_type`, `manufacturer`, `model`, `serial_no`, `lab_area`, `domain_code`, `capability_scope`, `responsible_user_id`, `calibration_valid_until`, `status`, `iot_enabled`, `iot_product_id`, `iot_device_id`, `data_source_type`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT d.`equipment_code`, d.`equipment_name`, d.`equipment_type`, d.`manufacturer`, d.`model`, d.`serial_no`, d.`lab_area`, d.`domain_code`, d.`capability_scope`, d.`responsible_user_id`, d.`calibration_valid_until`, 'enabled', d.`iot_enabled`, d.`iot_product_id`, d.`iot_device_id`, d.`data_source_type`, d.`remark`, 'admin', NOW(), '', NOW(), b'0', 1
FROM (
  SELECT 'FOOD-PH-001' AS `equipment_code`, '食品理化酸度计' AS `equipment_name`, 'instrument' AS `equipment_type`, 'SeedLab' AS `manufacturer`, 'PH-900' AS `model`, 'SN-FOOD-PH-001' AS `serial_no`, '食品理化实验室' AS `lab_area`, 'FOOD' AS `domain_code`, 'FOOD,感官检查,水分,pH,食品理化' AS `capability_scope`, 1 AS `responsible_user_id`, DATE_ADD(CURDATE(), INTERVAL 365 DAY) AS `calibration_valid_until`, b'0' AS `iot_enabled`, NULL AS `iot_product_id`, NULL AS `iot_device_id`, 'manual' AS `data_source_type`, '一期设备主档种子：用于 LIMS 任务设备绑定和设备证据链验证。' AS `remark`
) d
WHERE NOT EXISTS (
  SELECT 1 FROM `lab_equipment_asset`
  WHERE `equipment_code` = d.`equipment_code` AND `tenant_id` = 1 AND `deleted` = b'0'
);

INSERT INTO `lab_equipment_traceability` (`equipment_id`, `traceability_type`, `certificate_no`, `calibration_org`, `calibration_date`, `valid_to`, `result`, `uncertainty`, `traceability_chain`, `certificate_file_url`, `next_due_date`, `status`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT e.`id`, 'calibration', 'CERT-FOOD-PH-001', '系统种子计量机构', DATE_FORMAT(CURDATE(), '%Y-%m-%d'), DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 365 DAY), '%Y-%m-%d'), '合格', 'U=0.02pH', '国家计量基准 -> 省级计量机构 -> 实验室设备', '/lab/certificates/CERT-FOOD-PH-001.pdf', DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 330 DAY), '%Y-%m-%d'), 'valid', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_equipment_asset` e
WHERE e.`equipment_code` = 'FOOD-PH-001' AND e.`tenant_id` = 1 AND e.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_equipment_traceability`
    WHERE `equipment_id` = e.`id` AND `certificate_no` = 'CERT-FOOD-PH-001' AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_evidence_object` (`evidence_code`, `evidence_name`, `evidence_type`, `source_object`, `source_object_id`, `source_object_no`, `business_domain`, `file_url`, `file_name`, `file_format`, `evidence_hash`, `issued_by`, `issued_at`, `valid_from`, `valid_to`, `status`, `summary`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT CONCAT('OBJ-EQUIPMENT-CAL-', e.`equipment_code`), CONCAT('设备校准证书-', e.`equipment_code`), 'EQUIPMENT_CERTIFICATE', 'lab_equipment_traceability', t.`id`, t.`certificate_no`, 'equipment', t.`certificate_file_url`, t.`certificate_no`, 'pdf', SHA2(CONCAT(e.`equipment_code`, '|', t.`certificate_no`, '|', t.`valid_to`), 256), t.`calibration_org`, STR_TO_DATE(t.`calibration_date`, '%Y-%m-%d'), STR_TO_DATE(t.`calibration_date`, '%Y-%m-%d'), STR_TO_DATE(t.`valid_to`, '%Y-%m-%d'), 'effective', '设备校准证书支撑设备计量溯源要求', '由设备主档种子自动生成', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_equipment_asset` e
JOIN `lab_equipment_traceability` t ON t.`equipment_id` = e.`id` AND t.`certificate_no` = 'CERT-FOOD-PH-001' AND t.`tenant_id` = 1 AND t.`deleted` = b'0'
WHERE e.`equipment_code` = 'FOOD-PH-001' AND e.`tenant_id` = 1 AND e.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_evidence_object`
    WHERE `evidence_code` = CONCAT('OBJ-EQUIPMENT-CAL-', e.`equipment_code`) AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `lab_evidence_link` (`evidence_object_id`, `evidence_code`, `evidence_name`, `evidence_url`, `evidence_hash`, `source_object`, `source_object_id`, `source_object_no`, `linked_biz_type`, `linked_biz_id`, `linked_biz_no`, `clause_id`, `clause_category`, `link_status`, `link_reason`, `remark`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT o.`id`, o.`evidence_code`, o.`evidence_name`, o.`file_url`, o.`evidence_hash`, o.`source_object`, o.`source_object_id`, o.`source_object_no`, 'equipment_asset', e.`id`, e.`equipment_code`, c.`id`, 'equipment', 'linked', '设备校准证书支撑 CNAS/CMA 设备溯源条款', '设备证据链最小闭环种子', 'admin', NOW(), '', NOW(), b'0', 1
FROM `lab_evidence_object` o
JOIN `lab_equipment_asset` e ON e.`equipment_code` = 'FOOD-PH-001' AND e.`tenant_id` = 1 AND e.`deleted` = b'0'
LEFT JOIN (
  SELECT MIN(`id`) AS `id`
  FROM `lab_standard_clause`
  WHERE `clause_category` = 'equipment' AND `tenant_id` = 1 AND `deleted` = b'0'
) c ON 1 = 1
WHERE o.`evidence_code` = CONCAT('OBJ-EQUIPMENT-CAL-', e.`equipment_code`) AND o.`tenant_id` = 1 AND o.`deleted` = b'0'
  AND NOT EXISTS (
    SELECT 1 FROM `lab_evidence_link`
    WHERE `evidence_object_id` = o.`id` AND `linked_biz_type` = 'equipment_asset' AND `linked_biz_id` = e.`id` AND `tenant_id` = 1 AND `deleted` = b'0'
  );

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 'LIMS 执行闭环', '', 1, 10, @lab_root_menu_id, 'business', 'ep:operation', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `path` = 'business' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
);

SET @lims_business_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `path` = 'business' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测需求', 'lims:request:query', 2, 10, @lims_business_menu_id, 'request', 'ep:document-add', 'lims/request/index', 'LimsRequest', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:request:query' AND `deleted` = b'0');
SET @lims_request_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lims:request:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测需求新增', 'lims:request:create', 3, 1, @lims_request_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:request:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测需求修改', 'lims:request:update', 3, 2, @lims_request_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:request:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测需求删除', 'lims:request:delete', 3, 3, @lims_request_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:request:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '样品管理', 'lims:sample:query', 2, 20, @lims_business_menu_id, 'sample', 'ep:box', 'lims/sample/index', 'LimsSample', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:sample:query' AND `deleted` = b'0');
SET @lims_sample_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lims:sample:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '样品新增', 'lims:sample:create', 3, 1, @lims_sample_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:sample:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '样品修改', 'lims:sample:update', 3, 2, @lims_sample_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:sample:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '样品删除', 'lims:sample:delete', 3, 3, @lims_sample_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:sample:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务', 'lims:task:query', 2, 30, @lims_business_menu_id, 'task', 'ep:checked', 'lims/task/index', 'LimsTask', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:query' AND `deleted` = b'0');
SET @lims_task_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lims:task:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务新增', 'lims:task:create', 3, 1, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务修改', 'lims:task:update', 3, 2, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务删除', 'lims:task:delete', 3, 3, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:delete' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务排程', 'lims:task:schedule', 3, 4, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:schedule' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务分派', 'lims:task:assign', 3, 5, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:assign' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务就绪检查', 'lims:task:readiness', 3, 6, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:readiness' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务记录', 'lims:task:record', 3, 7, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:record' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务复核', 'lims:task:review', 3, 8, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:review' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测任务暂停', 'lims:task:hold', 3, 9, @lims_task_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:task:hold' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测结果', 'lims:result:query', 2, 40, @lims_business_menu_id, 'result', 'ep:edit-pen', 'lims/result/index', 'LimsResult', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:result:query' AND `deleted` = b'0');
SET @lims_result_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lims:result:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测结果新增', 'lims:result:create', 3, 1, @lims_result_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:result:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测结果修改', 'lims:result:update', 3, 2, @lims_result_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:result:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测结果删除', 'lims:result:delete', 3, 3, @lims_result_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:result:delete' AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测报告', 'lims:report:query', 2, 50, @lims_business_menu_id, 'report', 'ep:document', 'lims/report/index', 'LimsReport', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:report:query' AND `deleted` = b'0');
SET @lims_report_menu_id := (SELECT `id` FROM `system_menu` WHERE `permission` = 'lims:report:query' AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测报告新增', 'lims:report:create', 3, 1, @lims_report_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:report:create' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测报告修改', 'lims:report:update', 3, 2, @lims_report_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:report:update' AND `deleted` = b'0');
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测报告删除', 'lims:report:delete', 3, 3, @lims_report_menu_id, '', '', '', NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:report:delete' AND `deleted` = b'0');

-- 检测方向包设计器：可视化配置中心 + 执行工作台
SET @lab_root_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `name` = '实验室平台' AND `path` = '/lab' AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

SET @lab_config_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `path` = 'config' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

UPDATE `system_menu`
SET `name` = '检测方向包设计器',
    `permission` = 'lab:pack-designer:query',
    `path` = 'pack-designer',
    `icon` = 'ep:operation',
    `component` = 'lab/pack-designer/index',
    `component_name` = 'LabPackDesigner',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` = 'lab:medical-plugin:query'
  AND `parent_id` = @lab_config_menu_id
  AND `deleted` = b'0';

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测方向包设计器', 'lab:pack-designer:query', 2, 18, @lab_config_menu_id, 'pack-designer', 'ep:operation', 'lab/pack-designer/index', 'LabPackDesigner', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:pack-designer:query' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0'
);

-- 配置编制菜单按业务域归类，避免检测方向、标准、资源、质量记录全部平铺。
INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '配置总览', '', 1, 5, @lab_config_menu_id, 'overview', 'ep:data-analysis', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '配置总览' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '检测方向配置', '', 1, 10, @lab_config_menu_id, 'domain-config', 'ep:operation', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '检测方向配置' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '标准与模板', '', 1, 20, @lab_config_menu_id, 'standard-template', 'ep:collection', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '标准与模板' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '评审与证据', '', 1, 30, @lab_config_menu_id, 'review-evidence', 'ep:document-checked', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '评审与证据' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '资源与环境', '', 1, 40, @lab_config_menu_id, 'resource-environment', 'ep:office-building', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '资源与环境' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0');

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '质量改进', '', 1, 50, @lab_config_menu_id, 'quality-improvement', 'ep:circle-check', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (SELECT 1 FROM `system_menu` WHERE `name` = '质量改进' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0');

SET @lab_config_overview_menu_id := (SELECT `id` FROM `system_menu` WHERE `name` = '配置总览' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
SET @lab_domain_config_menu_id := (SELECT `id` FROM `system_menu` WHERE `name` = '检测方向配置' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
SET @lab_standard_template_menu_id := (SELECT `id` FROM `system_menu` WHERE `name` = '标准与模板' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
SET @lab_review_evidence_menu_id := (SELECT `id` FROM `system_menu` WHERE `name` = '评审与证据' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
SET @lab_resource_environment_menu_id := (SELECT `id` FROM `system_menu` WHERE `name` = '资源与环境' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);
SET @lab_quality_improvement_menu_id := (SELECT `id` FROM `system_menu` WHERE `name` = '质量改进' AND `parent_id` = @lab_config_menu_id AND `deleted` = b'0' ORDER BY `id` ASC LIMIT 1);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 'AI 标准与解读中心', 'lims:ai-assist:query', 2, 20, @lab_config_overview_menu_id, 'ai-assist', 'ep:magic-stick', 'lims/ai-assist/index', 'LimsAiAssist', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:ai-assist:query' AND `deleted` = b'0'
);

UPDATE `system_menu`
SET `parent_id` = @lab_config_overview_menu_id,
    `sort` = 10,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` = 'lab:dashboard:query'
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = @lab_domain_config_menu_id,
    `sort` = CASE `permission`
      WHEN 'lab:domain:query' THEN 10
      WHEN 'lab:domain-pack:query' THEN 20
      WHEN 'lab:pack-designer:query' THEN 30
      ELSE `sort`
    END,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN ('lab:domain:query', 'lab:domain-pack:query', 'lab:pack-designer:query')
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = @lab_standard_template_menu_id,
    `sort` = CASE `permission`
      WHEN 'lab:standard:query' THEN 10
      WHEN 'lab:standard-clause:query' THEN 20
      WHEN 'lab:clause-mapping:query' THEN 30
      WHEN 'lab:template:query' THEN 40
      WHEN 'lab:method-validation:query' THEN 50
      ELSE `sort`
    END,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN ('lab:standard:query', 'lab:standard-clause:query', 'lab:clause-mapping:query', 'lab:template:query', 'lab:method-validation:query')
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = @lab_review_evidence_menu_id,
    `sort` = CASE `permission`
      WHEN 'lab:evidence-link:query' THEN 10
      WHEN 'lab:review-package:query' THEN 20
      WHEN 'lab:compliance-check:query' THEN 30
      WHEN 'lab:compliance-check-item:query' THEN 40
      ELSE `sort`
    END,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN ('lab:evidence-link:query', 'lab:review-package:query', 'lab:compliance-check:query', 'lab:compliance-check-item:query')
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = @lab_resource_environment_menu_id,
    `sort` = CASE `permission`
      WHEN 'lab:personnel-competence:query' THEN 10
      WHEN 'lab:personnel-authorization:query' THEN 20
      WHEN 'lab:equipment-asset:query' THEN 30
      WHEN 'lab:equipment-traceability:query' THEN 40
      WHEN 'lab:equipment-intermediate-check:query' THEN 50
      WHEN 'lab:environment-record:query' THEN 60
      ELSE `sort`
    END,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN ('lab:personnel-competence:query', 'lab:personnel-authorization:query', 'lab:equipment-asset:query', 'lab:equipment-traceability:query', 'lab:equipment-intermediate-check:query', 'lab:environment-record:query')
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = @lab_quality_improvement_menu_id,
    `sort` = CASE `permission`
      WHEN 'lab:nonconformity:query' THEN 10
      WHEN 'lab:corrective-action:query' THEN 20
      WHEN 'lab:internal-audit:query' THEN 30
      WHEN 'lab:management-review:query' THEN 40
      ELSE `sort`
    END,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN ('lab:nonconformity:query', 'lab:corrective-action:query', 'lab:internal-audit:query', 'lab:management-review:query')
  AND `deleted` = b'0';

-- TIC 六层产品导航归一化：把前端一级菜单对齐产品架构，保留现有页面组件和权限。
SET @lab_root_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `name` = '实验室平台' AND `path` = '/lab' AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

UPDATE `system_menu`
SET `name` = 'LIMS 执行闭环',
    `sort` = 10,
    `icon` = 'ep:operation',
    `visible` = b'1',
    `always_show` = b'1',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `path` = 'business'
  AND `parent_id` = @lab_root_menu_id
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `name` = '检测方向包配置',
    `sort` = 20,
    `icon` = 'ep:box',
    `visible` = b'1',
    `always_show` = b'1',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `path` = 'config'
  AND `parent_id` = @lab_root_menu_id
  AND `deleted` = b'0';

SET @lims_business_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `path` = 'business' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

SET @lab_config_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `path` = 'config' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 'CNAS/CMA 合规中心', '', 1, 30, @lab_root_menu_id, 'cnas-compliance', 'ep:medal', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `path` = 'cnas-compliance' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '证据链中心', '', 1, 40, @lab_root_menu_id, 'evidence-chain', 'ep:connection', NULL, NULL, 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `path` = 'evidence-chain' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
);

SET @tic_cnas_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `path` = 'cnas-compliance' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

SET @tic_evidence_menu_id := (
  SELECT `id` FROM `system_menu`
  WHERE `path` = 'evidence-chain' AND `parent_id` = @lab_root_menu_id AND `deleted` = b'0'
  ORDER BY `id` ASC LIMIT 1
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT 'AI 标准与解读中心', 'lims:ai-assist:query', 2, 50, @lab_root_menu_id, 'ai-assist', 'ep:magic-stick', 'lims/ai-assist/index', 'LimsAiAssist', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lims:ai-assist:query' AND `deleted` = b'0'
);

INSERT INTO `system_menu` (`name`, `permission`, `type`, `sort`, `parent_id`, `path`, `icon`, `component`, `component_name`, `status`, `visible`, `keep_alive`, `always_show`, `creator`, `create_time`, `updater`, `update_time`, `deleted`)
SELECT '评审与运营看板', 'lab:dashboard:query', 2, 60, @lab_root_menu_id, 'dashboard', 'ep:data-analysis', 'lab/dashboard/index', 'LabDashboard', 0, b'1', b'1', b'1', 'admin', NOW(), '', NOW(), b'0'
WHERE NOT EXISTS (
  SELECT 1 FROM `system_menu` WHERE `permission` = 'lab:dashboard:query' AND `deleted` = b'0'
);

UPDATE `system_menu`
SET `parent_id` = @lims_business_menu_id,
    `sort` = CASE `permission`
      WHEN 'lims:request:query' THEN 10
      WHEN 'lims:sample:query' THEN 20
      WHEN 'lims:task:query' THEN 30
      WHEN 'lims:result:query' THEN 40
      WHEN 'lims:report:query' THEN 50
      ELSE `sort`
    END,
    `visible` = b'1',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN ('lims:request:query', 'lims:sample:query', 'lims:task:query', 'lims:result:query', 'lims:report:query')
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = @lab_config_menu_id,
    `sort` = CASE `permission`
      WHEN 'lab:domain:query' THEN 10
      WHEN 'lab:domain-pack:query' THEN 20
      WHEN 'lab:pack-designer:query' THEN 30
      WHEN 'lab:template:query' THEN 40
      ELSE `sort`
    END,
    `visible` = b'1',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN ('lab:domain:query', 'lab:domain-pack:query', 'lab:pack-designer:query', 'lab:template:query')
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = @tic_cnas_menu_id,
    `sort` = CASE `permission`
      WHEN 'lab:standard:query' THEN 10
      WHEN 'lab:standard-clause:query' THEN 20
      WHEN 'lab:clause-mapping:query' THEN 30
      WHEN 'lab:compliance-check:query' THEN 40
      WHEN 'lab:compliance-check-item:query' THEN 50
      WHEN 'lab:personnel-competence:query' THEN 60
      WHEN 'lab:personnel-authorization:query' THEN 70
      WHEN 'lab:equipment-asset:query' THEN 80
      WHEN 'lab:equipment-traceability:query' THEN 90
      WHEN 'lab:equipment-intermediate-check:query' THEN 100
      WHEN 'lab:environment-record:query' THEN 110
      WHEN 'lab:method-validation:query' THEN 120
      WHEN 'lab:nonconformity:query' THEN 130
      WHEN 'lab:corrective-action:query' THEN 140
      WHEN 'lab:internal-audit:query' THEN 150
      WHEN 'lab:management-review:query' THEN 160
      ELSE `sort`
    END,
    `visible` = b'1',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN (
    'lab:standard:query', 'lab:standard-clause:query', 'lab:clause-mapping:query',
    'lab:compliance-check:query', 'lab:compliance-check-item:query',
    'lab:personnel-competence:query', 'lab:personnel-authorization:query',
    'lab:equipment-asset:query', 'lab:equipment-traceability:query', 'lab:equipment-intermediate-check:query',
    'lab:environment-record:query', 'lab:method-validation:query',
    'lab:nonconformity:query', 'lab:corrective-action:query', 'lab:internal-audit:query', 'lab:management-review:query'
  )
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `parent_id` = @tic_evidence_menu_id,
    `sort` = CASE `permission`
      WHEN 'lab:evidence-object:query' THEN 10
      WHEN 'lab:evidence-link:query' THEN 20
      WHEN 'lab:review-package:query' THEN 30
      ELSE `sort`
    END,
    `visible` = b'1',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` IN ('lab:evidence-object:query', 'lab:evidence-link:query', 'lab:review-package:query')
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `name` = 'AI 标准与解读中心',
    `parent_id` = @lab_root_menu_id,
    `path` = 'ai-assist',
    `sort` = 50,
    `icon` = 'ep:magic-stick',
    `component` = 'lims/ai-assist/index',
    `component_name` = 'LimsAiAssist',
    `visible` = b'1',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` = 'lims:ai-assist:query'
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `name` = '评审与运营看板',
    `parent_id` = @lab_root_menu_id,
    `path` = 'dashboard',
    `sort` = 60,
    `icon` = 'ep:data-analysis',
    `component` = 'lab/dashboard/index',
    `component_name` = 'LabDashboard',
    `visible` = b'1',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `permission` = 'lab:dashboard:query'
  AND `deleted` = b'0';

UPDATE `system_menu`
SET `visible` = b'0',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `parent_id` = @lab_config_menu_id
  AND `name` IN ('配置总览', '检测方向配置', '标准与模板', '评审与证据', '资源与环境', '质量改进')
  AND `deleted` = b'0';

-- BPM 管理表：Flowable ACT_* 引擎表由模块启动自动维护，这里补齐芋道后台页面直接查询的自有表。
CREATE TABLE IF NOT EXISTS `bpm_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类编号',
  `name` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名',
  `code` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类标志',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '分类描述',
  `status` tinyint NOT NULL COMMENT '分类状态',
  `sort` int NOT NULL COMMENT '分类排序',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bpm_category_code` (`code`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程分类';

CREATE TABLE IF NOT EXISTS `bpm_form` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单名',
  `status` tinyint NOT NULL COMMENT '状态',
  `conf` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单配置',
  `fields` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表单项数组',
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 工作流表单定义';

CREATE TABLE IF NOT EXISTS `bpm_user_group` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '组名',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '描述',
  `status` tinyint NOT NULL COMMENT '状态',
  `user_ids` longtext COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '成员用户编号数组',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 用户组';

CREATE TABLE IF NOT EXISTS `bpm_process_definition_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `process_definition_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程定义编号',
  `model_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程模型编号',
  `model_type` tinyint DEFAULT NULL COMMENT '流程模型类型',
  `category` varchar(63) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程分类编码',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `description` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `form_type` tinyint DEFAULT NULL COMMENT '表单类型',
  `form_id` bigint DEFAULT NULL COMMENT '动态表单编号',
  `form_conf` longtext COLLATE utf8mb4_unicode_ci COMMENT '表单配置',
  `form_fields` longtext COLLATE utf8mb4_unicode_ci COMMENT '表单项数组',
  `form_custom_create_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义表单提交路径',
  `form_custom_view_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自定义表单查看路径',
  `simple_model` longtext COLLATE utf8mb4_unicode_ci COMMENT 'SIMPLE 设计器模型数据',
  `visible` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否可见',
  `sort` bigint NOT NULL DEFAULT 0 COMMENT '排序值',
  `start_user_ids` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可发起用户编号数组',
  `start_dept_ids` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可发起部门编号数组',
  `manager_user_ids` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '可管理用户编号数组',
  `allow_cancel_running_process` bit(1) NOT NULL DEFAULT b'1' COMMENT '允许撤销审批中的申请',
  `allow_withdraw_task` bit(1) NOT NULL DEFAULT b'1' COMMENT '允许审批人撤回任务',
  `process_id_rule` longtext COLLATE utf8mb4_unicode_ci COMMENT '流程 ID 规则',
  `auto_approval_type` tinyint DEFAULT NULL COMMENT '自动去重类型',
  `title_setting` longtext COLLATE utf8mb4_unicode_ci COMMENT '标题设置',
  `summary_setting` longtext COLLATE utf8mb4_unicode_ci COMMENT '摘要设置',
  `process_before_trigger_setting` longtext COLLATE utf8mb4_unicode_ci COMMENT '流程前置通知设置',
  `process_after_trigger_setting` longtext COLLATE utf8mb4_unicode_ci COMMENT '流程后置通知设置',
  `task_before_trigger_setting` longtext COLLATE utf8mb4_unicode_ci COMMENT '任务前置通知设置',
  `task_after_trigger_setting` longtext COLLATE utf8mb4_unicode_ci COMMENT '任务后置通知设置',
  `print_template_setting` longtext COLLATE utf8mb4_unicode_ci COMMENT '自定义打印模板设置',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_bpm_process_definition_info_model_id` (`model_id`),
  KEY `idx_bpm_process_definition_info_process_definition_id` (`process_definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程定义扩展信息';

CREATE TABLE IF NOT EXISTS `bpm_process_listener` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '监听器名字',
  `status` tinyint NOT NULL COMMENT '状态',
  `type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '监听类型',
  `event` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '监听事件',
  `value_type` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '值类型',
  `value` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '值',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程监听器';

CREATE TABLE IF NOT EXISTS `bpm_process_expression` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(63) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表达式名字',
  `status` tinyint NOT NULL COMMENT '状态',
  `expression` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '表达式',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程表达式';

CREATE TABLE IF NOT EXISTS `bpm_process_instance_copy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `start_user_id` bigint NOT NULL COMMENT '发起人编号',
  `process_instance_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程名',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程实例编号',
  `process_definition_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '流程定义编号',
  `category` varchar(63) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程分类',
  `activity_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程活动编号',
  `activity_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程活动名字',
  `task_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程任务编号',
  `user_id` bigint NOT NULL COMMENT '被抄送用户编号',
  `reason` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '抄送意见',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`),
  KEY `idx_bpm_process_instance_copy_user_id` (`user_id`),
  KEY `idx_bpm_process_instance_copy_process_instance_id` (`process_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM 流程抄送';

CREATE TABLE IF NOT EXISTS `bpm_oa_leave` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '请假表单主键',
  `user_id` bigint NOT NULL COMMENT '申请人用户编号',
  `type` tinyint NOT NULL COMMENT '请假类型',
  `reason` varchar(512) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原因',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `day` bigint NOT NULL COMMENT '请假天数',
  `status` tinyint NOT NULL COMMENT '审批结果',
  `process_instance_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程编号',
  `creator` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='BPM OA 请假申请';

-- LIMS 专用导航：保留系统底座与实验室平台，隐藏 RuoYi-Vue-Pro 默认示例/无关业务主菜单。
UPDATE `system_menu`
SET `visible` = b'0',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `parent_id` = 0
  AND `deleted` = b'0'
  AND `name` NOT IN ('系统管理', '基础设施', '工作流程', '实验室平台', '报表管理');

UPDATE `system_menu`
SET `visible` = b'1',
    `status` = 0,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `parent_id` = 0
  AND `deleted` = b'0'
  AND `name` IN ('系统管理', '基础设施', '工作流程', '实验室平台', '报表管理');

-- 报表能力保留内置积木报表/仪表盘；隐藏依赖外部 GoView 前端的入口，避免误打开本机 3000 端口上的其它项目。
UPDATE `system_menu`
SET `visible` = b'0',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `deleted` = b'0'
  AND (`name` = '大屏设计器' OR `permission` LIKE 'report:go-view-%');

UPDATE `system_menu`
SET `name` = '大屏/仪表盘设计器',
    `visible` = b'1',
    `status` = 0,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `deleted` = b'0'
  AND `component` = 'report/jmreport/bi';

UPDATE `system_menu`
SET `visible` = b'1',
    `status` = 0,
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `deleted` = b'0'
  AND `component` = 'report/jmreport/index';

-- 工作流程保留流程引擎与审批中心，隐藏 RuoYi 默认 OA 请假示例。
UPDATE `system_menu`
SET `visible` = b'0',
    `updater` = 'admin',
    `update_time` = NOW()
WHERE `deleted` = b'0'
  AND (`name` = 'OA 示例' OR `component` = 'bpm/oa/leave/index');

-- 授权超级管理员操作实验室平台菜单，保证全量 SQL 导入后可直接验证 LAB/LIMS 闭环。
INSERT INTO `system_role_menu` (`role_id`, `menu_id`, `creator`, `create_time`, `updater`, `update_time`, `deleted`, `tenant_id`)
SELECT DISTINCT r.`id`, m.`id`, 'admin', NOW(), '', NOW(), b'0', r.`tenant_id`
FROM `system_role` r
JOIN `system_menu` root ON root.`name` = '实验室平台' AND root.`path` = '/lab' AND root.`deleted` = b'0'
JOIN `system_menu` m ON m.`deleted` = b'0'
LEFT JOIN `system_menu` p1 ON p1.`id` = m.`parent_id` AND p1.`deleted` = b'0'
LEFT JOIN `system_menu` p2 ON p2.`id` = p1.`parent_id` AND p2.`deleted` = b'0'
LEFT JOIN `system_menu` p3 ON p3.`id` = p2.`parent_id` AND p3.`deleted` = b'0'
LEFT JOIN `system_menu` p4 ON p4.`id` = p3.`parent_id` AND p4.`deleted` = b'0'
WHERE r.`code` = 'super_admin'
  AND r.`tenant_id` = 1
  AND r.`deleted` = b'0'
  AND (
    m.`id` = root.`id`
    OR p1.`id` = root.`id`
    OR p2.`id` = root.`id`
    OR p3.`id` = root.`id`
    OR p4.`id` = root.`id`
    OR m.`permission` LIKE 'lab:%'
    OR m.`permission` LIKE 'lims:%'
  )
  AND NOT EXISTS (
    SELECT 1 FROM `system_role_menu`
    WHERE `role_id` = r.`id` AND `menu_id` = m.`id` AND `tenant_id` = r.`tenant_id` AND `deleted` = b'0'
  );
