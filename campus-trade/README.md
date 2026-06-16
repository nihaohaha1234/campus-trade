# 校园二手交易平台后端

这是校园二手交易平台的后端项目，基于 Spring Boot 开发，提供用户认证、商品、收藏、订单、管理员后台、文件上传、Redis 缓存、AI 文案优化和 AI 审核日志等接口。

## 技术栈

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

## 模块结构

```text
src/main/java/com/example/campustrade
├── common        通用返回、异常、上下文、Redis Key、分页校验
├── component     业务组件，例如管理员权限校验
├── config        MyBatis-Plus、密码加密、跨域、静态资源配置
├── controller    接口层
├── convert       DO/DTO/VO 转换
├── dto           前端请求参数对象
├── entity        数据库实体对象
├── enums         状态枚举
├── interceptor   登录拦截器
├── mapper        MyBatis-Plus Mapper
├── service       业务接口
├── service/impl  业务实现
├── utils         JWT、订单号等工具类
└── vo            后端返回给前端的数据对象
```

## 核心功能

### 用户与认证

- 用户注册、登录
- BCrypt 密码加密
- JWT Token 签发和解析
- 登录拦截器保护需要登录的接口
- 用户禁用后，旧 Token 也不能继续访问受保护接口
- Redis 记录登录失败次数，短时间失败过多时限制登录

### 商品

- 发布商品
- 修改商品
- 下架商品
- 查询公开商品列表
- 查询商品详情
- 商品关键词搜索
- 我的商品列表和状态筛选
- 商品图片上传
- 商品详情 Redis 缓存
- 热门商品榜
- 基于浏览记录的个性化推荐

### 收藏

- 收藏商品
- 取消收藏
- 查询我的收藏
- 判断商品是否已收藏
- 数据库使用用户 ID + 商品 ID 联合唯一索引，避免重复收藏

### 订单

- 发起交易
- 买家订单列表
- 卖家订单列表
- 订单详情
- 卖家确认订单
- 买家或卖家完成订单
- 买家或卖家取消订单
- 管理员查看全平台订单
- 订单创建、取消、完成会同步修改商品状态，并使用事务保证一致性

### 管理员

- 查看用户列表
- 禁用和启用普通用户
- 查看全部商品
- 搜索全部商品
- 查看待审核商品
- 审核通过商品
- 审核拒绝商品
- 查看全平台订单
- 查看 AI 审核日志

### AI 能力

- 商品标题和描述智能优化
- 商品发布前 AI 审核
- AI 审核结果落库
- 管理员查看 AI 审核建议、审核原因和创建时间

## Redis 使用场景

| 场景 | 数据结构 | 说明 |
|---|---|---|
| 登录失败限制 | String | 记录用户名失败次数，设置过期时间 |
| 商品详情缓存 | String | 缓存商品详情 JSON，减少 MySQL 查询 |
| 热门商品榜 | ZSet | 记录商品访问热度分数 |
| 用户浏览记录 | List | 记录用户最近浏览的商品 ID，用于推荐 |

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
| GET | `/products` | 查询已上架商品 |
| GET | `/products/recommend` | 查询个性化推荐商品 |
| GET | `/products/search` | 搜索商品 |
| GET | `/products/hot` | 热门商品榜 |
| GET | `/products/{id}` | 商品详情 |
| GET | `/products/my` | 我的商品 |
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
| GET | `/orders/buyer` | 买家订单 |
| GET | `/orders/seller` | 卖家订单 |
| GET | `/orders/{orderId}` | 订单详情 |
| PUT | `/orders/{orderId}/confirm` | 卖家确认订单 |
| PUT | `/orders/{orderId}/finish` | 完成订单 |
| PUT | `/orders/{orderId}/cancel` | 取消订单 |

### 管理员接口

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/admin/users` | 用户列表 |
| PUT | `/admin/users/{userId}/disable` | 禁用用户 |
| PUT | `/admin/users/{userId}/enable` | 启用用户 |
| GET | `/admin/products` | 全部商品 |
| GET | `/admin/products/search` | 管理员搜索商品 |
| GET | `/admin/products/pending` | 待审核商品 |
| GET | `/admin/products/{productId}` | 管理员商品详情 |
| PUT | `/admin/products/{productId}/approve` | 审核通过 |
| PUT | `/admin/products/{productId}/reject` | 审核拒绝 |
| GET | `/admin/orders` | 全平台订单 |
| GET | `/admin/ai-review-logs` | AI 审核日志 |

### AI 接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/ai/products/optimize` | AI 优化商品标题和描述 |

### 文件接口

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/files/upload` | 上传商品图片 |

## 本地启动

### 1. 准备环境

- JDK 17
- Maven
- MySQL
- Redis

### 2. 初始化数据库

```sql
CREATE DATABASE campus_trade DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

导入项目根目录下的 SQL：

```bash
mysql -u root -p campus_trade < ../campus_trade_schema.sql
mysql -u root -p campus_trade < ../campus_trade_data.sql
```

### 3. 配置后端

复制配置文件：

```text
src/main/resources/application-example.properties
```

复制为：

```text
src/main/resources/application.properties
```

配置 MySQL、Redis 和 DeepSeek：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_trade?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

spring.data.redis.host=localhost
spring.data.redis.port=6379

deepseek.base-url=https://api.deepseek.com/chat/completions
deepseek.api-key=你的 DeepSeek API Key
```

### 4. 启动 Redis

```bash
redis-server
```

### 5. 启动后端

```bash
mvn spring-boot:run
```

默认访问地址：

```text
http://localhost:8080
```

## 部署说明

Railway 部署时需要配置以下环境变量：

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SPRING_DATASOURCE_DRIVER_CLASS_NAME

SPRING_DATA_REDIS_HOST
SPRING_DATA_REDIS_PORT
SPRING_DATA_REDIS_PASSWORD

DEEPSEEK_BASE_URL
DEEPSEEK_API_KEY
```

图片上传在线上环境保存到：

```text
/tmp/campus-trade/images/
```

该目录适合演示使用。如果要长期保存图片，建议后续接入对象存储。
