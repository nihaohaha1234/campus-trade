# 校园二手交易平台

本仓库包含校园二手交易平台的后端、前端、项目文档和页面截图。

## 目录结构

```text
campus-trade
├── campus-trade       后端 Spring Boot 项目
├── campus-trade-web   前端 Vue 项目
├── docs               项目演示流程文档
├── photos             页面截图
└── README.md          项目总说明
```

## 项目说明

校园二手交易平台是一个前后端分离项目，面向校园闲置物品交易场景，支持用户注册登录、商品发布与审核、商品浏览与收藏、订单交易、管理员后台管理等功能。

详细说明见：

- [后端项目说明](./campus-trade/README.md)
- [前端项目说明](./campus-trade-web/README.md)
- [项目演示流程](./docs/demo-flow.md)

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
- Maven

前端：

- Vue 3
- Vue Router
- Axios
- Vite
- JavaScript
- CSS

## 核心功能

- 用户注册登录
- JWT 登录认证
- BCrypt 密码加密
- Redis 登录失败限制
- 商品发布、修改、下架
- 商品图片上传
- 管理员商品审核
- 商品列表、搜索、详情和热门榜
- 收藏商品、取消收藏、收藏状态显示
- 发起交易、订单确认、订单完成、订单取消
- 管理员用户管理、商品管理和订单管理
- 分页查询和状态筛选

## 启动说明

### 后端

1. 创建 MySQL 数据库：

```sql
CREATE DATABASE campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 复制配置文件：

```text
campus-trade/src/main/resources/application-example.properties
```

复制为：

```text
campus-trade/src/main/resources/application.properties
```

然后把数据库密码改成本机 MySQL 密码。

3. 启动 Redis。

4. 在后端目录启动项目：

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

## GitHub 上传说明

本仓库的 `.gitignore` 已排除以下内容：

- 后端构建产物 `target`
- 前端依赖 `node_modules`
- 前端打包产物 `dist`
- IDE 配置 `.idea`、`.vscode`
- 本地真实配置 `application.properties`
- 运行时上传图片目录

上传 GitHub 前请确认不要提交真实数据库密码。
