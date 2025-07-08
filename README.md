# 项目简介
项目名称：ws-cloud

## 后端框架
### 使用Java语言，Spring Cloud Alibaba微服务架构
JDK17  
Spring 6.2.6  
Spring Boot 3.4.5  
Spring Cloud 2024.0.1  
Spring Cloud Alibaba 2023.0.3.2  
持久层：Mybatis Plus 3.5.12  
权限框架：SaToen 1.42.0  
定时任务：XXL-JOB 3.1.0  
网关：Gateway  
远程服务调用：Open Feign  
接口文档：Knife4j 4.5.0（OpenApi3文档标准）  
配置中心、注册中心：Nacos 2.2及以上  
缓存：Redis 7.x  
数据库：Mysql 8.x  
对象存储：腾讯云COS  
日志收集分析：腾讯云日志

## API文档
API文档工具：Apifox  
项目地址：https://app.apifox.com/project/6407466

推荐安装Idea插件生成文档到Apifox项目中，不需要手动在Apifox中维护文档  
Idea插件帮助文档：https://docs.apifox.com/5743620m0

## 服务划分

### 定时任务调度器服务
XXL-JOB定时任务调度器  
服务名：job-admin-server

### 网关服务
微服务请求统一入口，服务分发  
服务名：gateway-server

### 系统服务
登录、用户、角色、菜单等相关功能  
服务名：system-server  
网关请求路径前缀：/sys

### 运营服务
PO订单、DF订单等运营相关功能  
服务名：operation-server  
网关请求路径前缀：/op

### 供应链服务
产品、报价、仓储等供应链相关功能  
服务名：supply-server  
网关请求路径前缀：/supply

### 三方集成服务
与第三方平台交互的接口都放在这个服务，统一封装第三方接口，方便对接、统一处理、接口复用  
服务名：integration-server
