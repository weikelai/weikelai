-- 婚介匹配系统数据库初始化脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS bookdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bookdb;

-- 创建用户表
DROP TABLE IF EXISTS dating_users;

CREATE TABLE dating_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender CHAR(1) NOT NULL COMMENT '性别：M-男，F-女',
    age INT NOT NULL COMMENT '年龄',
    height INT COMMENT '身高（cm）',
    city VARCHAR(50) COMMENT '城市',
    education VARCHAR(20) COMMENT '学历',
    occupation VARCHAR(50) COMMENT '职业',
    interests TEXT COMMENT '兴趣爱好（JSON格式）',
    description TEXT COMMENT '个人简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_gender (gender),
    INDEX idx_city (city),
    INDEX idx_age (age)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- 创建匹配历史表（可选）
DROP TABLE IF EXISTS dating_matches;

CREATE TABLE dating_matches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '匹配记录ID',
    user1_id BIGINT NOT NULL COMMENT '用户1 ID',
    user2_id BIGINT NOT NULL COMMENT '用户2 ID',
    compatibility_score DECIMAL(5,2) COMMENT '兼容性分数',
    match_reason TEXT COMMENT '匹配原因',
    matched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '匹配时间',
    FOREIGN KEY (user1_id) REFERENCES dating_users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES dating_users(id) ON DELETE CASCADE,
    INDEX idx_user1 (user1_id),
    INDEX idx_user2 (user2_id),
    INDEX idx_score (compatibility_score)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='匹配历史表';
