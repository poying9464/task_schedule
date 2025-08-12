/*
 * 任务执行结果详情表
 * groupName和jobName联合作为唯一主键
 */

CREATE TABLE `task_result_detail` (
  `group_name` varchar(100) NOT NULL COMMENT '任务组名',
  `job_name` varchar(100) NOT NULL COMMENT '任务名称',
  `success_count` bigint(20) DEFAULT '0' COMMENT '任务执行成功次数',
  `fail_count` bigint(20) DEFAULT '0' COMMENT '任务执行失败次数',
  `total_count` bigint(20) DEFAULT '0' COMMENT '任务执行总次数',
  `last_execute_time` datetime DEFAULT NULL COMMENT '最后执行时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`group_name`, `job_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务执行结果详情表';