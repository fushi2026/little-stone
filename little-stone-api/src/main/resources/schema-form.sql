CREATE TABLE IF NOT EXISTS `form_definition` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(255) NOT NULL COMMENT '表单名称',
  `code` VARCHAR(100) NOT NULL COMMENT '表单编码',
  `description` TEXT COMMENT '表单描述',
  `fields` TEXT COMMENT '表单字段配置(JSON)',
  `layout_config` TEXT COMMENT '布局配置(JSON)',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
  `creator_id` BIGINT COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单定义表';

CREATE TABLE IF NOT EXISTS `form_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `form_definition_id` BIGINT NOT NULL COMMENT '表单定义ID',
  `form_data` TEXT COMMENT '表单数据(JSON)',
  `business_key` VARCHAR(255) COMMENT '业务主键',
  `process_instance_id` BIGINT COMMENT '流程实例ID',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0草稿,1已提交,2已完成)',
  `submitter_id` BIGINT COMMENT '提交人ID',
  `submit_time` DATETIME COMMENT '提交时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_form_definition_id` (`form_definition_id`),
  INDEX `idx_process_instance_id` (`process_instance_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表单实例表';

CREATE TABLE IF NOT EXISTS `process_definition` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(255) NOT NULL COMMENT '流程名称',
  `code` VARCHAR(100) NOT NULL COMMENT '流程编码',
  `description` TEXT COMMENT '流程描述',
  `flow_config` TEXT COMMENT '流程节点配置(JSON)',
  `form_definition_id` BIGINT COMMENT '关联表单定义ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
  `creator_id` BIGINT COMMENT '创建人ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_process_code` (`code`),
  INDEX `idx_form_definition_id` (`form_definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程定义表';

CREATE TABLE IF NOT EXISTS `process_instance` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_definition_id` BIGINT NOT NULL COMMENT '流程定义ID',
  `business_key` VARCHAR(255) COMMENT '业务主键',
  `status` TINYINT DEFAULT 1 COMMENT '状态(0结束,1运行中)',
  `current_node` VARCHAR(255) COMMENT '当前节点',
  `initiator_id` BIGINT COMMENT '发起人ID',
  `start_time` DATETIME COMMENT '开始时间',
  `end_time` DATETIME COMMENT '结束时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_process_definition_id` (`process_definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程实例表';

CREATE TABLE IF NOT EXISTS `task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `process_instance_id` BIGINT NOT NULL COMMENT '流程实例ID',
  `node_name` VARCHAR(255) NOT NULL COMMENT '节点名称',
  `node_type` VARCHAR(50) COMMENT '节点类型(start,approve,end,cc)',
  `assignee_id` BIGINT COMMENT '处理人ID',
  `creator_id` BIGINT COMMENT '创建人ID',
  `status` TINYINT DEFAULT 0 COMMENT '状态(0待处理,1已通过,2已拒绝)',
  `comment` TEXT COMMENT '处理意见',
  `action` VARCHAR(50) COMMENT '操作类型(approve,reject)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `complete_time` DATETIME COMMENT '完成时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_process_instance_id` (`process_instance_id`),
  INDEX `idx_assignee_id` (`assignee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';