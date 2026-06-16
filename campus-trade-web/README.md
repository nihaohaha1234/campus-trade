# 校园二手交易平台前端

这是校园二手交易平台的前端项目，基于 Vue 3 + Vite 开发，通过 Axios 调用 Spring Boot 后端接口，实现普通用户端和管理员后台页面。

## 技术栈

- Vue 3
- Vue Router
- Axios
- Vite
- JavaScript
- CSS

## 页面功能

### 普通用户页面

- 登录页
- 注册页
- 商品列表页
- 商品详情页
- 发布商品页
- 修改商品页
- 我的商品页
- 我的商品详情页
- 收藏列表页
- 我的订单页
- 订单详情页

### 管理员页面

- 管理员首页
- 管理员商品列表页
- 管理员商品详情页
- 管理员商品审核页
- 管理员用户管理页
- 管理员订单管理页
- AI 审核日志页

## 前端说明

- 使用 Vue Router 管理页面路由。
- 使用 Axios 封装统一请求对象。
- 登录成功后将 Token 和用户信息保存到 `localStorage`。
- 请求拦截器会自动携带 `Authorization` 请求头。
- 响应拦截器会在登录状态失效时清除本地登录信息并跳转登录页。
- 管理员页面使用前端路由守卫做基础访问限制。
- 商品列表页支持普通商品列表、搜索、分页、热门商品榜和登录后的推荐商品。
- 商品详情页支持收藏状态展示、收藏/取消收藏、发起交易。
- 发布和修改商品页支持图片上传和 AI 商品文案优化。
- 管理员后台支持商品审核、用户管理、订单管理和 AI 审核日志查看。
- 使用 Toast 组件替代部分浏览器 `alert`，优化操作反馈体验。

## 本地启动

安装依赖：

```bash
npm install
```

启动开发服务器：

```bash
npm run dev
```

默认访问地址：

```text
http://localhost:5173
```

本地默认请求后端：

```text
http://localhost:8080
```

## 环境变量

前端请求地址在 `src/api/config.js` 中配置：

```js
export const API_BASE_URL =
    import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
```

本地如果没有 `.env` 文件，就会请求本地后端 `http://localhost:8080`。

如果需要请求线上后端，可以在前端根目录创建 `.env`：

```text
VITE_API_BASE_URL=https://你的后端域名
```

Railway 部署前端时，也需要配置同名环境变量。

## 打包

```bash
npm run build
```

打包产物会生成在：

```text
dist
```

## 部署说明

Railway 部署前端时：

1. 服务 Root Directory 设置为 `campus-trade-web`。
2. 配置 `VITE_API_BASE_URL` 为后端 Railway 域名。
3. 生成前端服务域名。
4. 如果使用 Vite Preview，需要在 `vite.config.js` 中允许 Railway 域名访问。

## 后续优化方向

- 抽取分页组件。
- 抽取商品卡片组件。
- 统一所有页面的 Toast 消息提示。
- 优化移动端适配。
- 增加商品多图和用户头像展示。
