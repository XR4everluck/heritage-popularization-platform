-- ============================================================
-- 非遗知识教学平台 —— 阶段3-1 存量库升级脚本
-- ============================================================
-- 适用场景：数据库是按旧版脚本初始化的（heritage_info 无 4 个结构化字段、
--           无 heritage_history 表），执行本脚本原地升级，保留已有数据。
-- 新装环境无需执行本脚本（heritage_teaching_platform.sql 已包含全部结构）。
-- 幂等性：heritage_history 使用 CREATE TABLE IF NOT EXISTS；
--         ALTER TABLE ADD COLUMN 在字段已存在时会报 1060 重复列错误，可忽略继续执行。
-- ============================================================

USE `heritage_teaching`;

-- 1. heritage_info 新增 4 个结构化知识字段
ALTER TABLE `heritage_info`
  ADD COLUMN `origin_age`           VARCHAR(50)  DEFAULT NULL COMMENT '起源年代（如"唐代""1906年"）' AFTER `cover_image`,
  ADD COLUMN `distribution_area`    VARCHAR(255) DEFAULT NULL COMMENT '分布地区（当前流布范围）' AFTER `origin_age`,
  ADD COLUMN `representative_works` VARCHAR(500) DEFAULT NULL COMMENT '代表作品（名称/曲目/剧目等）' AFTER `distribution_area`,
  ADD COLUMN `endanger_level`       VARCHAR(20)  DEFAULT NULL COMMENT '濒危程度：濒危/急需保护/脆弱/状况良好' AFTER `representative_works`;

-- 2. 新增非遗历史节点表
CREATE TABLE IF NOT EXISTS `heritage_history` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '历史节点ID，主键自增',
  `heritage_id` BIGINT       NOT NULL                COMMENT '非遗项目ID（业务层关联 heritage_info.id）',
  `year`        VARCHAR(30)  DEFAULT NULL            COMMENT '年代（如"唐代""1955年"，古代项目可填时期名称）',
  `event`       VARCHAR(200) NOT NULL                COMMENT '事件标题',
  `description` VARCHAR(500) DEFAULT NULL            COMMENT '事件描述',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_heritage_id` (`heritage_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '非遗历史节点表';
