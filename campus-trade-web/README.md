# 校园二手交易平台前端

这是校园二手交易平台的前端项目，基于 Vue 3 + Vite 开发，通过 Axios 调用 Spring Boot 后端接口。

## 技术栈

- Vue 3
- Vue Router
- Axios
- Vite
- JavaScript
- CSS

## 页面功能

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

## 前端说明

- 使用 Vue Router 管理页面路由。
- 使用 Axios 封装请求对象，统一设置后端地址。
- 登录成功后将 Token 和用户信息保存到 `localStorage`。
- 请求拦截器会自动携带 `Authorization` 请求头。
- 响应拦截器会在登录状态失效时清除本地登录信息并跳转登录页。
- 使用 Toast 组件展示部分操作成功提示。

## 启动方式

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

## 打包

```bash
npm run build
```

打包产物会生成在 `dist` 目录。
