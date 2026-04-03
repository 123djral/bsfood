-- 基于大模型的个性化食谱生成系统 数据库初始化脚本
-- Database: recipe_generator

CREATE DATABASE IF NOT EXISTS recipe_generator DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE recipe_generator;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '用户唯一标识',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '加密存储',
    `age` INT(3) DEFAULT NULL COMMENT '用户年龄',
    `gender` CHAR(2) DEFAULT NULL COMMENT '用户性别',
    `phone` VARCHAR(11) DEFAULT NULL COMMENT '手机号码',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户偏好表
CREATE TABLE IF NOT EXISTS `user_preference` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '偏好唯一标识',
    `user_id` INT(11) NOT NULL COMMENT '关联用户',
    `health_goal` VARCHAR(50) NOT NULL COMMENT '健康目标（减脂/控糖等）',
    `diet_limit` VARCHAR(100) DEFAULT NULL COMMENT '饮食限制（过敏原/素食等）',
    `taste_preference` VARCHAR(50) NOT NULL COMMENT '口味倾向（辣/淡等）',
    `cooking_level` VARCHAR(20) NOT NULL COMMENT '烹饪熟练度（新手/进阶等）',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    CONSTRAINT `fk_preference_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户偏好表';

-- 营养数据表
CREATE TABLE IF NOT EXISTS `nutrition` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '营养唯一标识',
    `food_id` INT(11) DEFAULT NULL COMMENT '关联食材',
    `calorie` DECIMAL(10,2) NOT NULL COMMENT '热量（大卡/100g）',
    `protein` DECIMAL(10,2) NOT NULL COMMENT '蛋白质（g/100g）',
    `fat` DECIMAL(10,2) NOT NULL COMMENT '脂肪（g/100g）',
    `carbohydrate` DECIMAL(10,2) NOT NULL COMMENT '碳水化合物（g/100g）',
    `vitamin` VARCHAR(100) DEFAULT NULL COMMENT '维生素含量',
    `mineral` VARCHAR(100) DEFAULT NULL COMMENT '矿物质含量',
    PRIMARY KEY (`id`),
    KEY `idx_food_id` (`food_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营养数据表';

-- 食材表
CREATE TABLE IF NOT EXISTS `food_material` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '食材唯一标识',
    `name` VARCHAR(50) NOT NULL COMMENT '食材名称',
    `type` VARCHAR(20) NOT NULL COMMENT '食材类别（蔬菜/肉类等）',
    `quantity` DECIMAL(10,2) NOT NULL COMMENT '食材数量',
    `shelf_life` INT(5) DEFAULT NULL COMMENT '保质期（天）',
    `nutrition_id` INT(11) DEFAULT NULL COMMENT '关联营养数据',
    `user_id` INT(11) DEFAULT NULL COMMENT '所属用户',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_nutrition_id` (`nutrition_id`),
    CONSTRAINT `fk_food_nutrition` FOREIGN KEY (`nutrition_id`) REFERENCES `nutrition` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食材表';

-- 食谱表
CREATE TABLE IF NOT EXISTS `recipe` (
    `id` INT(11) NOT NULL AUTO_INCREMENT COMMENT '食谱唯一标识',
    `name` VARCHAR(100) NOT NULL COMMENT '食谱名称',
    `user_id` INT(11) NOT NULL COMMENT '关联用户',
    `cooking_time` INT(5) NOT NULL COMMENT '烹饪耗时（分钟）',
    `difficulty_level` VARCHAR(20) NOT NULL COMMENT '难度等级（简单/中等/困难）',
    `steps` TEXT NOT NULL COMMENT '烹饪步骤',
    `food_ids` VARCHAR(255) DEFAULT NULL COMMENT '食材ID列表',
    `collect_count` INT(11) NOT NULL DEFAULT 0 COMMENT '收藏次数',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_difficulty` (`difficulty_level`),
    CONSTRAINT `fk_recipe_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食谱表';
