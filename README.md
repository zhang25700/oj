# Online Judge

基于 `Spring Boot + MyBatis-Plus + MySQL + Redis + RabbitMQ + Vue 3` 的在线判题系统，当前仓库已经包含后端骨架和前端页面。

## 已实现能力

- 用户注册、登录、获取当前用户
- 题目新增、详情、按关键词 / 标签 / 难度分页检索
- 在线代码提交、异步消息投递、判题结果查询
- Redis 热门题目缓存
- Redis ZSet 实时排行榜
- Vue 首页、登录页、题目详情页、提交记录页、排行榜页
- Docker 沙箱执行接口抽象

## 后端启动

1. 创建 MySQL 数据库 `oj`
2. 执行 [schema.sql](/d:/code/java/oj/src/main/resources/schema.sql)
3. 启动本地 MySQL、Redis、RabbitMQ
4. 修改 [application.yml](/d:/code/java/oj/src/main/resources/application.yml) 中连接配置
5. 启动后端

```bash
mvn spring-boot:run
```

也可以直接使用 [docker-compose.yml](/d:/code/java/oj/docker-compose.yml) 启动依赖服务。

## 前端启动

前端项目位于 [frontend/package.json](/d:/code/java/oj/frontend/package.json)。

```bash
cd frontend
npm install
npm run dev
```

默认访问地址是 `http://localhost:5173`，接口请求指向 `http://localhost:8101`。

## 核心接口

- `POST /user/register` 用户注册
- `POST /user/login` 用户登录，返回 token
- `GET /user/me` 获取当前登录用户，Header 携带 `Authorization`
- `POST /question/add` 新增题目
- `POST /question/page` 题目分页检索
- `GET /question/{id}` 题目详情
- `POST /submit` 提交代码
- `GET /submit/my/page` 我的提交记录
- `GET /rank/top` 排行榜 Top N

## 判题流程

1. 用户提交代码后写入 `question_submit`
2. 主服务将提交 id 投递到 RabbitMQ
3. 消费者接收消息并调用 `JudgeService`
4. `JudgeService` 读取题目测试用例并调用 `CodeSandbox`
5. 判题完成后更新提交状态、题目统计、用户解题数和 Redis 排行榜

## 当前说明

- 当前 [DockerCodeSandbox.java](/d:/code/java/oj/src/main/java/com/lenovo/oj/service/impl/DockerCodeSandbox.java) 是统一执行入口的示例实现，默认返回 mock 结果
- 登录鉴权使用 Redis Token 方案，适合前后端分离项目快速接入
- 如果继续扩展，建议下一步优先补真实 Docker 编译运行和管理员题目管理
