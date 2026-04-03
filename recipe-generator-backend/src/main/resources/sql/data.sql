-- 初始数据
USE recipe_generator;

-- 管理员账号 (密码: admin123)
INSERT INTO `user` (`username`, `password`, `age`, `gender`, `phone`, `create_time`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z3SK', 30, '男', '13800000000', NOW());

-- 测试用户 (密码: 123456)
INSERT INTO `user` (`username`, `password`, `age`, `gender`, `phone`, `create_time`)
VALUES ('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z3SK', 25, '女', '13800000001', NOW());

-- 测试用户偏好
INSERT INTO `user_preference` (`user_id`, `health_goal`, `diet_limit`, `taste_preference`, `cooking_level`)
VALUES (2, '减脂', '无', '清淡', '新手');
