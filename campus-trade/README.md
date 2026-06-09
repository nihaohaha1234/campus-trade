# 校园二手交易平台

## 项目介绍

校园二手交易平台是一个前后端分离的校园闲置物品交易系统，面向校内用户的二手商品发布、审核、浏览、收藏、下单和交易管理场景。

项目支持普通用户和管理员两类角色。普通用户可以注册登录、发布商品、浏览商品、收藏商品、发起交易、管理自己的商品和订单；管理员可以审核商品、管理用户、查看全平台商品和订单。

本项目重点练习 Spring Boot 后端开发、MyBatis-Plus 数据访问、JWT 登录认证、Redis 缓存与热门排行、前后端分离接口设计，以及 Vue 基础页面开发。

## 技术栈

### 后端

- Java 17
- Spring Boot
- Spring MVC
- MyBatis-Plus
- MySQL
- Redis
- JWT
- BCrypt
- Maven

### 前端

- Vue 3
- Vue Router
- Axios
- Vite
- HTML / CSS / JavaScript

## 功能模块

### 用户模块

- 用户注册、登录
- JWT 登录认证
- BCrypt 密码加密
- Redis 登录失败次数限制
- 用户禁用后禁止继续访问受保护接口

### 商品模块

- 发布商品
- 上传商品图片
- 修改商品
- 下架商品
- 查看公开商品列表
- 查看商品详情
- 商品关键词搜索
- 我的发布按状态筛选
- Redis 商品详情缓存
- Redis ZSet 热门商品榜

### 收藏模块

- 收藏商品
- 取消收藏
- 我的收藏列表
- 商品详情页显示收藏状态

### 订单模块

- 发起交易
- 买家订单列表
- 卖家订单列表
- 订单详情
- 卖家确认订单
- 买家或卖家完成订单
- 买家或卖家取消订单
- 订单按状态筛选

### 管理员模块

- 管理员控制台
- 商品审核通过
- 商品审核拒绝
- 查看全部商品
- 查看待审核商品
- 用户禁用和启用
- 查看全平台订单
- 管理员订单按状态筛选

## 核心业务流程

1. 用户注册并登录系统。
2. 用户发布商品并上传商品图片。
3. 商品进入待审核状态。
4. 管理员审核商品，通过后商品进入公开列表。
5. 其他用户浏览商品，可以收藏商品或发起交易。
6. 发起交易后商品进入锁定状态，避免重复交易。
7. 卖家确认订单后，订单进入已确认状态。
8. 买家或卖家确认完成后，订单完成，商品变为已售出。
9. 管理员可以查看用户、商品和订单数据。

## 项目亮点

- 使用 JWT + 拦截器实现登录认证，对商品发布、收藏、订单、后台管理等接口进行保护。
- 使用 BCrypt 对密码进行加密存储，避免明文密码风险。
- 使用统一返回对象 `Result<T>` 和全局异常处理，保证接口返回结构一致。
- 使用 MyBatis-Plus 实现分页查询、条件查询和基础 CRUD。
- 使用枚举管理商品状态、订单状态、用户角色和用户状态，减少魔法数字。
- 使用 Redis 记录登录失败次数，对短时间多次登录失败的账号进行临时限制。
- 使用 Redis 缓存商品详情，降低热门商品详情接口对 MySQL 的重复查询。
- 使用 Redis ZSet 统计商品访问热度，实现热门商品榜。
- 使用 MultipartFile 实现图片上传，并通过静态资源映射返回可访问的图片地址。
- 在创建订单、取消订单、完成订单等涉及多表修改的业务中使用事务，保证数据一致性。
- 前端使用 Axios 拦截器统一携带 Token，并在登录状态失效时自动跳转登录页。
- 前端使用 Toast 组件替代部分浏览器 `alert`，优化操作反馈体验。

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

## 主要接口

### 认证接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/auth/register` | 用户注册 |
| POST | `/auth/login` | 用户登录 |

### 商品接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/products` | 发布商品 |
| GET | `/products` | 查询上架商品 |
| GET | `/products/search` | 搜索商品 |
| GET | `/products/hot` | 热门商品榜 |
| GET | `/products/{id}` | 商品详情 |
| GET | `/products/my` | 我的发布 |
| GET | `/products/my/{id}` | 我的商品详情 |
| PUT | `/products/{productId}` | 修改商品 |
| PUT | `/products/{productId}/off` | 下架商品 |

### 收藏接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/favorites` | 我的收藏 |
| POST | `/favorites/{productId}` | 收藏商品 |
| DELETE | `/favorites/{productId}` | 取消收藏 |
| GET | `/favorites/{productId}/isFavorite` | 判断是否已收藏 |

### 订单接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/orders/{productId}` | 创建订单 |
| GET | `/orders/buyer` | 买家订单列表 |
| GET | `/orders/seller` | 卖家订单列表 |
| GET | `/orders/{orderId}` | 订单详情 |
| PUT | `/orders/{orderId}/confirm` | 卖家确认订单 |
| PUT | `/orders/{orderId}/finish` | 完成订单 |
| PUT | `/orders/{orderId}/cancel` | 取消订单 |

### 管理员接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/products` | 查看全部商品 |
| GET | `/admin/products/search` | 管理员搜索商品 |
| GET | `/admin/products/pending` | 查看待审核商品 |
| GET | `/admin/products/{productId}` | 管理员查看商品详情 |
| PUT | `/admin/products/{productId}/approve` | 审核通过 |
| PUT | `/admin/products/{productId}/reject` | 审核拒绝 |
| GET | `/admin/users` | 查看用户列表 |
| PUT | `/admin/users/{userId}/disable` | 禁用用户 |
| PUT | `/admin/users/{userId}/enable` | 启用用户 |
| GET | `/admin/orders` | 管理员查看订单列表 |

### 文件接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/files/upload` | 上传图片 |

## 前端页面

- 登录页
- 注册页
- 商品列表页
- 商品详情页
- 发布商品页
- 修改商品页
- 我的商品页
- 收藏列表页
- 我的订单页
- 订单详情页
- 管理员首页
- 管理员商品列表页
- 管理员商品审核页
- 管理员用户管理页
- 管理员订单管理页

## 启动方式

### 1. 准备环境

- JDK 17
- Maven
- MySQL
- Redis
- Node.js

### 2. 初始化数据库

创建数据库：

```sql
CREATE DATABASE campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

然后创建项目所需表，包括用户表、商品表、收藏表和订单表。

### 3. 修改后端配置

修改后端 `src/main/resources/application.properties` 中的数据库账号和密码：

```properties
spring.datasource.username=root
spring.datasource.password=你的密码
```

确认 Redis 配置：

```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

### 4. 启动 Redis

```bash
redis-server
```

### 5. 启动后端

在后端项目目录执行：

```bash
mvn spring-boot:run
```

后端默认运行在：

```text
http://localhost:8080
```

### 6. 启动前端

在前端项目目录执行：

```bash
npm install
npm run dev
```

前端默认运行在：

```text
http://localhost:5173
```

## 图片上传说明

商品图片上传流程：

1. 前端调用 `/files/upload` 上传图片文件。
2. 后端使用 UUID 重命名文件并保存到本地目录。
3. 后端返回 `/images/xxx.jpg` 格式的图片访问路径。
4. 发布或修改商品时，将该地址作为 `imageUrl` 传入。
5. 查询商品时返回 `imageUrl`，前端通过 `http://localhost:8080/images/xxx.jpg` 展示图片。

## Redis 使用说明

本项目 Redis 主要用于以下场景：

- 登录失败次数限制：记录用户短时间内登录失败次数，超过阈值后临时限制登录。
- 商品详情缓存：缓存公开商品详情，减少重复查询 MySQL。
- 热门商品排行：使用 Redis ZSet 记录商品访问热度，返回热门商品列表。

## 后续优化

- 整理 Apifox 接口文档。
- 增加项目页面截图。
- 抽取分页组件、商品卡片组件和通用状态显示方法。
- 使用更完善的全局消息提示机制。
- 支持商品多图和用户头像。
- 学习并尝试部署到云服务器。
