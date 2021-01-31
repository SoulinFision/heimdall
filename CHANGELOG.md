#Changelog

## v1.0.5(20210129)-在线用户总数逻辑优化
* 调整获取在线用户总数的方法，改成通过实际 SessionId 缓存获取

## v1.0.4(20210129)-调整SpringBoot Starter默认实现

* 调整 SpringBoot Starter ，去除特定业务依赖，只保留 Heimdall 框架相关功能。
* 移除全局异常处理实现，由 Sample 示例程序实现
* 移除全局异常 Controller实现，由 Sample 示例程序实现
* resolver 依赖由AutoWired调整为构造函数注入
* 移除 Heimdall 配置参数日志输出
    

## v1.0.3(20210124) -优化默认配置

* 取消CurrentUserRequestArgumentResolver自动注册，改为手动注册
* 优化 SessionDao 构造方式，去掉 CookieService
* 修复用户权限缓存重复清理两次的 bug
* 将 Cookie功能 默认开启 修改为: 默认关闭


##v1.0.2 (20210120) -新增和优化功能
* 本地 MapCache 缓存，不支持过期策略，废弃
* 实现 Caffeine Session 缓存和权限缓存
* 实现用户权限单独缓存,便于动态授权
* 对已登录用户的 Cookie 写入机制进行优化,解决刷新页面后 Cookie 丢失问题
* 功能演示示例修改，实现三种认证授权方式和数据处理逻辑
* 配置参数修改，将系统权限缓存配置独立成单独的配置文件。见:AuthorityProperty
* 新增Session配置参数:concurrentLogin，用以设置重复登录的处理行为。
* 修改登录事件监听逻辑，新增对重复登录不同状态的处理。0:踢出前者 1:拒绝后者 2:正常登录
* 修改 redis 缓存配置参数，去掉事务支持。
* 错误页配置参数修正:可通过server.error.name或者error.name指定错误页面的名称，默认:error
* com.luter.heimdall.cache.caffeine包名错误修正，原包:....caffeinel
* 实现认证错误重试次数限定功能
