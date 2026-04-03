# 基于大模型的个性化食谱生成系统 - 待办事项

## 待完成任务

### 1. 数据库相关
- [ ] 创建数据库脚本：生成 MySQL 数据库表结构和初始数据
- [ ] 配置数据库连接：确保生产环境的数据库连接配置正确
- [ ] 数据库索引优化：为常用查询添加适当的索引

### 2. 后端功能
- [ ] 实现用户认证和授权：完善 Spring Security 配置
- [ ] 实现 AI API 集成：连接真实的大模型 API，实现食材识别和食谱生成
- [ ] 完善异常处理：添加全局异常处理机制
- [ ] 实现日志记录：添加系统日志和业务日志
- [ ] 性能优化：优化数据库查询和 API 响应速度

### 3. 前端功能
- [ ] 实现食材图像上传：支持用户上传食材图片进行识别
- [ ] 实现食谱详情页面：展示食谱的详细步骤和营养信息
- [ ] 实现用户登录和注册：完善用户认证功能
- [ ] 实现食谱分享：支持用户分享食谱到社交媒体
- [ ] 优化响应式设计：确保在不同设备上的良好显示效果

### 4. 测试和部署
- [ ] 编写单元测试：为核心功能编写单元测试
- [ ] 编写集成测试：测试前后端集成和 API 调用
- [ ] 性能测试：测试系统在高并发情况下的性能
- [ ] 安全测试：测试系统的安全性，防止常见的安全漏洞
- [ ] 部署文档：编写系统部署和运维文档

## 缺少的配置

### 1. 环境变量
- [ ] API 密钥：将 AI 模型 API 密钥存储到环境变量中
- [ ] 数据库密码：将数据库密码存储到环境变量中
- [ ] 生产环境配置：配置生产环境的数据库连接、端口等参数

### 2. 依赖配置
- [ ] 前端依赖：确保所有前端依赖都已正确安装
- [ ] 后端依赖：确保所有后端依赖都已正确配置
- [ ] 版本管理：锁定依赖版本，确保系统的稳定性

### 3. 安全配置
- [ ] HTTPS 配置：配置 SSL 证书，启用 HTTPS
- [ ] CORS 配置：配置跨域资源共享
- [ ] 输入验证：完善前端和后端的输入验证

## 需要改进的地方

### 1. 代码质量
- [ ] 代码注释：为核心代码添加详细的注释
- [ ] 代码规范：遵循统一的代码规范和命名约定
- [ ] 代码重构：优化代码结构，提高代码可读性和可维护性

### 2. 用户体验
- [ ] 界面设计：优化界面布局和视觉效果
- [ ] 交互体验：改进用户交互流程，提高用户操作的便捷性
- [ ] 加载速度：优化页面加载速度和 API 响应时间

### 3. 功能扩展
- [ ] 食材库存管理：添加食材库存跟踪和提醒功能
- [ ] 食谱分类：添加食谱分类和标签功能
- [ ] 社区互动：添加用户评论、评分和收藏功能
- [ ] 多语言支持：添加对英文、日文等多语言的支持

### 4. 系统架构
- [ ] 微服务拆分：将核心功能拆分为独立的微服务
- [ ] 缓存策略：添加 Redis 缓存，提高系统性能
- [ ] 监控系统：添加系统监控和告警机制
- [ ] 日志系统：集成 ELK 等日志管理系统

## 操作指引

### 1. 本地开发环境搭建
1. **后端开发环境**：
   - 安装 JDK 17+
   - 安装 Maven 3.9+
   - 安装 MySQL 8.0+
   - 安装 Redis 6.0+

2. **前端开发环境**：
   - 安装 Node.js 18+
   - 安装 npm 10+

3. **项目启动**：
   - 后端：`mvn spring-boot:run`
   - 前端：`npm run dev`

### 2. 数据库初始化
1. **创建数据库**：
   - 执行 SQL 脚本：`CREATE DATABASE recipe_generator DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;`

2. **创建表结构**：
   - 执行 SQL 脚本：`recipe-generator-backend/src/main/resources/sql/schema.sql`

3. **插入初始数据**：
   - 执行 SQL 脚本：`recipe-generator-backend/src/main/resources/sql/data.sql`

### 3. 部署到生产环境
1. **构建项目**：
   - 后端：`mvn clean package`
   - 前端：`npm run build`

2. **部署后端**：
   - 将 `recipe-generator-backend/target/recipe-generator-backend-1.0.0.jar` 部署到服务器
   - 启动服务：`java -jar recipe-generator-backend-1.0.0.jar --spring.profiles.active=prod`

3. **部署前端**：
   - 将 `recipe-generator-frontend/dist` 目录部署到 Nginx 或其他 Web 服务器
   - 配置 Nginx 反向代理，将 API 请求转发到后端服务

4. **配置环境变量**：
   - 设置 API 密钥：`export AI_API_KEY=your_api_key`
   - 设置数据库密码：`export DB_PASSWORD=your_db_password`

### 4. 系统维护
1. **日志查看**：
   - 后端日志：`tail -f recipe-generator-backend/logs/application.log`
   - 前端日志：浏览器开发者工具控制台

2. **性能监控**：
   - 使用 Spring Boot Actuator 监控系统状态
   - 使用 Prometheus 和 Grafana 监控系统性能

3. **故障排查**：
   - 检查数据库连接：`mysql -u root -p recipe_generator`
   - 检查 Redis 连接：`redis-cli ping`
   - 检查 API 响应：`curl http://localhost:8080/api/health`

## 技术支持

### 1. 常见问题
- **后端服务启动失败**：检查数据库连接和 Redis 连接
- **前端页面加载失败**：检查网络连接和 API 地址配置
- **食材识别失败**：检查 AI API 密钥和网络连接
- **食谱生成失败**：检查食材信息和用户画像数据

### 2. 联系信息
- **开发团队**：bsfood 技术团队
- **邮箱**：tech@bsfood.com
- **电话**：13800138000

### 3. 文档资源
- **项目文档**：`docs/recipe-generator/`
- **API 文档**：`http://localhost:8080/api/doc`
- **前端文档**：`recipe-generator-frontend/README.md`
- **后端文档**：`recipe-generator-backend/README.md`
