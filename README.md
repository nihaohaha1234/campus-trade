# 校园二手交易平台

本仓库包含校园二手交易平台的后端、前端、数据库脚本、项目文档和页面截图。项目采用前后端分离架构，覆盖用户发布商品、浏览商品、收藏、订单交易、管理员审核与后台管理等核心流程，并接入 Redis 和大模型能力，用于提升项目完整度和简历展示效果。

## 目录结构

```text
campus-trade
├── campus-trade        后端 Spring Boot 项目
├── campus-trade-web    前端 Vue 项目
├── docs                项目演示流程文档
├── photos              页面截图
├── campus_trade_schema.sql  数据库表结构
├── campus_trade_data.sql    示例数据
└── README.md           项目总说明
```

## 项目简介

校园二手交易平台面向校园闲置物品交易场景，支持普通用户和管理员两类角色。

普通用户可以注册登录、发布商品、上传图片、浏览商品、搜索商品、收藏商品、发起交易、管理自己的商品和订单。管理员可以审核商品、管理用户、查看全平台商品、订单和 AI 审核日志。

项目重点练习 Spring Boot 后端开发、MyBatis-Plus 数据访问、JWT 登录认证、Redis 缓存与排行榜、Vue 前端页面开发、Railway 云部署，以及大模型接入业务流程。

## 技术栈

后端：

- Java 17
- Spring Boot
- Spring MVC
- MyBatis-Plus
- MySQL
- Redis
- JWT
- BCrypt
- DeepSeek API
- Maven

前端：

- Vue 3
- Vue Router
- Axios
- Vite
- JavaScript
- CSS

部署：

- Railway
- Railway MySQL
- Railway Redis
- GitHub 自动部署

## 核心功能

- 用户注册、登录、JWT 鉴权
- BCrypt 密码加密
- Redis 登录失败次数限制
- 商品发布、修改、下架、审核
- 商品图片上传与静态资源访问
- 商品列表、搜索、分页、详情缓存
- Redis ZSet 热门商品榜
- 基于用户浏览记录的个性化推荐
- 商品收藏、取消收藏、收藏状态展示
- 发起交易、订单确认、订单完成、订单取消
- 管理员用户管理、商品管理、订单管理
- AI 商品文案优化
- AI 商品发布前审核
- 管理员查看 AI 审核日志
- 前端 Toast 消息提示
- Railway 云端部署

## 项目亮点

- 使用 JWT + 拦截器实现登录认证，并支持公开接口的可选登录识别。
- 使用 BCrypt 加密密码，避免明文密码存储。
- 使用统一返回对象 `Result<T>` 和全局异常处理，保证接口响应格式一致。
- 使用 MyBatis-Plus 分页、条件查询和基础 CRUD，提高开发效率。
- 使用枚举管理商品状态、订单状态、用户角色和用户状态，减少魔法数字。
- 使用 Redis 缓存商品详情，降低热门商品详情接口对 MySQL 的重复查询。
- 使用 Redis ZSet 统计商品访问热度，实现热门商品榜。
- 使用 Redis List 记录用户浏览商品 ID，结合商品标题和描述实现轻量级个性化推荐。
- 创建订单、取消订单、完成订单等涉及订单表和商品表的操作使用事务保证一致性。
- 接入 DeepSeek API，实现商品标题/描述优化和发布前 AI 审核。
- AI 审核结果落库，管理员可以查看审核建议、原因和时间，便于复盘。
- 前端使用 Axios 请求拦截器自动携带 Token，响应拦截器处理登录失效。
- 使用 Railway 部署后端、前端、MySQL 和 Redis，形成线上可访问闭环。

## 状态说明

### 商品状态

| 状态值 | 含义 |
|---|---|
| 0 | 待审核 |
| 1 | 已上架 |
| 2 | 已下架 / 审核拒绝 |
| 3 | 已售出 |
| 4 | 已锁定 |

### 订单状态

| 状态值 | 含义 |
|---|---|
| 0 | 待确认 |
| 1 | 已确认 |
| 2 | 已完成 |
| 3 | 已取消 |

### 用户角色

| 状态值 | 含义 |
|---|---|
| 0 | 普通用户 |
| 1 | 管理员 |

### 用户状态

| 状态值 | 含义 |
|---|---|
| 0 | 禁用 |
| 1 | 正常 |

## 启动说明

### 后端

1. 创建 MySQL 数据库：

```sql
CREATE DATABASE campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入数据库脚本：

```bash
mysql -u root -p campus_trade < campus_trade_schema.sql
mysql -u root -p campus_trade < campus_trade_data.sql
```

3. 复制配置文件：

```text
campus-trade/src/main/resources/application-example.properties
```

复制为：

```text
campus-trade/src/main/resources/application.properties
```

然后修改本地 MySQL、Redis 和 DeepSeek 配置。

4. 启动 Redis。

5. 启动后端：

```bash
cd campus-trade
mvn spring-boot:run
```

后端默认运行在：

```text
http://localhost:8080
```

### 前端

```bash
cd campus-trade-web
npm install
npm run dev
```

前端默认运行在：

```text
http://localhost:5173
```

本地前端默认请求：

```text
http://localhost:8080
```

如果需要连接线上后端，可以在前端环境变量中配置：

```text
VITE_API_BASE_URL=https://你的后端域名
```

## 部署说明

项目已验证可以部署到 Railway：

1. 后端服务连接 GitHub 仓库，Root Directory 设置为 `campus-trade`。
2. 添加 Railway MySQL 和 Redis 服务。
3. 后端配置 MySQL、Redis、DeepSeek 等环境变量。
4. 后端服务生成 8080 端口的公网域名。
5. 前端服务 Root Directory 设置为 `campus-trade-web`。
6. 前端配置 `VITE_API_BASE_URL` 指向后端公网域名。
7. 前端生成公网域名后即可访问页面。

注意：Railway 上的图片上传目前保存到容器临时目录 `/tmp/campus-trade/images/`，适合演示使用。生产环境更推荐接入对象存储。

## 相关文档

- [后端项目说明](./campus-trade/README.md)
- [前端项目说明](./campus-trade-web/README.md)
- [项目演示流程](./docs/demo-flow.md)

## GitHub 上传说明

`.gitignore` 已排除以下内容：

- 后端构建产物 `target`
- 前端依赖 `node_modules`
- 前端打包产物 `dist`
- IDE 配置 `.idea`、`.vscode`
- 本地真实配置 `application.properties`
- 运行时上传图片目录

上传 GitHub 前请确认不要提交真实数据库密码、Redis 密码和 DeepSeek API Key。
