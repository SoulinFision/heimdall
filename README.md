# Heimdall

&emsp;&emsp;**Heimdall** Java Web 认证授权框架，类似于Spring Security 和shiro,实现认证和授权功能. 相比之下,Heimdall框架更加简单，也更容易上手。 支持Session Cookie 认证方式，支持普通Url权限和Restful资源权限， 支持内存缓存和redis缓存。 同时提供了spring-boot-starter， 开箱即用。


## 技术选型

##### 基础环境
*  Maven 3
*  Git 2
*  JDK 1.8

##### 核心依赖
* aspectj 1.7.30
* servlet-api 4.0.1
* guava 30.0-jre
* Lombok  1.18.16
* slf4j

##### 可选依赖

* Caffeine 2.8.8
* Spring Boot Data Redis
* Spring Boot 2.x


## 项目结构

```shell
├── heimdall-cache-caffeine//caffeine缓存模块
├── heimdall-core//框架认证授权核心模块
├── samples//示例
│   ├── sample-common//示例用到的公共模块
│   ├── spring-boot-redis-restful//基于 redis 缓存的 restful 资源的例子
│   └── spring-boot-simple-cache//基于内存缓存和精确路由 url 的例子
├── heimdall-starter-data-redis//redis 缓存模块
└── heimdall-starter-spring-boot//Spring Boot Starter 模块，实现 Spring自动化配置、权限拦截器、请求参数解析等功能适配，并且封装了一些常用公共功能，比如异常处理，统一返回消息等，便于快速开发。
```

## 主要功能

* **认证**：是谁？
* **授权**：能做什么?
* **认证授权信息缓存**，支持内存缓存(如:Map、Caffeine)和 Redis 缓存
* **Session 会话管理**，支持无状态 Session.支持 Session 过期自动处理
* **Cookie管理**,支持开启和关闭Cookie
* **在线用户管理**,Redis 缓存支持分页获取在线用户
* **注解授权**，是否登录、单角色、多角色、单权限、多权限授权
* **基于拦截器的的资源授权**，支持restful资源授权
* **Session 自动续签**(Redis 缓存下避免频繁update缓存，可设置Session续签规则)
* **重复登录限制** 登录用户强制下线功能
* **登录错误重试限制** 支持登录重试次数限制，超过限制后，账户锁定一定时长。
* **脚手架**: spring-boot 2x 的starter脚手架，便于快速集成
* **示例项目**:基于spring boot 2x 实现的传统url权限与restful权限示例项目，稍加配置即可应用。
* *更多功能持续完善中......*

- - -



## 开发
1. 从 Gitee 或者 github clone 代码到本地
1. 修改根pom.xml中properties节点下dest.path为自己本地打包文件输出目录
1. 在根pom.xml所在目录执行 mvn clean package



## 交流反馈,参与贡献
- Github

<a target="_blank" href="https://github.com/luterc/heimdall">**Heimdall 框架**</a>

<a target="_blank" href="https://github.com/luterc/heimdall-admin">**Heimdall-admin  权限管理系统** </a>

<a target="_blank" href="https://github.com/luterc/heimdall-admin-ui">**Heimdall-admin-ui  管理系统前端 UI** </a>

**技术交流QQ群:	554290469**

欢迎fork，star，欢迎提需求，欢迎吐槽，支持共建！

*如果你感觉好用的话，支持刷火箭 !*




## License
Apache License, Version 2.0

