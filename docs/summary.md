# 认证授权概述

## 认证(是谁?)

&emsp;&emsp;**Authentication**  指的是当前用户的身份。也就是俗称的:登录。认证解决 “是谁？”的问题。

&emsp;&emsp;一般情况下，系统认证的大致步骤如下：

1.  用户提交认证凭证，如:用户名+密码、手机号+密码等到系统
1.  系统通过数据库或者其他存储查找用户，查找到后进行凭证校验(密码对不对?)
1.  凭证校验通过，对认证行为状态进行记录并且返回给用户访问凭证,如 Session 、Cookie、JWT 等
1.  后续用户携带访问凭证访问系统资源，可对通过缓存对用户身份进行核查

==说明: 本框架暂时不涉及 用户凭证的校验功能，框架目前只实现步骤：3、4。也就是对认证状态进行管理和维护，不参与凭据校验和用户身份合法性检查。也就是说你自己判断用户合法后，将相关信息提交给框架，框架对认证授权状态进行管理和维护。==

### 认证信息

系统提供认证信息实体:UserDetails ,具体信息如下:

```java
public interface UserDetails extends Serializable {
    /**
     * 用这个数据判断用户是否登录,可以是用户id,手机号、用户名等等，只要能全局唯一标识这个用户即可
     * <p>
     * eg:
     * 1、 租户ID+用户名称: 这样，同一个用户名称不同租户的用户可以各自登录不受影响
     * <p>
     * 2、设备类型+用户ID: 同一个用户，可从不同终端登录，而不至于被踢下线
     *
     * @return the principal
     */
    String getPrincipal();

    /**
     * 用这个判断用户是否启用
     *
     * @return the boolean
     */
    boolean enabled();

    /**
     * 此用户拥有的角色
     *
     * @return the roles
     */
    List<String> getRoles();

    /**
     * 此用户具备的权限标识符
     *
     * @return the permission
     * @see MethodAndUrlGrantedAuthority
     * @see SimpleGrantedAuthority
     */
    List<? extends GrantedAuthority> getAuthorities();
}

```

用户合法性校验通过后，构造此接口的实现类，实现用户 principal、permissions、roles 接口。

### 缓存支持
&emsp;&emsp; 用户认证和授权操作是一个比较频繁的操作，每次请求访问系统都会需要对认证状态和权限进行判断，需要频繁读写，所以系统使用缓存对已认证用户的信息和权限进行存储。当发生认证授权行为的时候，首先会从缓存获取，如果缓存中没有，则从数据库获取然后写入缓存。
&emsp;&emsp;框架内目前缓存了如下几种信息：
- 认证用户基本信息，也就是将UserDetails实现类信息
- 授权拦截规则信息，是一个有序 Map<String,String>;

框架支持两种形式的缓存：

- 单机 内存缓存，如: Map、 ehcache 、Caffeine等
- 分布式 Redis 缓存


### 认证凭据

&emsp;&emsp; 通过认证后，框架会生成认证凭据，以 Session+Cookie 形式实现。当用户提交认证后，框架会按规则(SessionIdGenerator) 产生 Session ，返回给调用方，如果开启了 Cookie，同时还会像客户端请求中  设置 Cookie 。

&emsp;&emsp;客户端认证后发起其他请求，需要携带生成的 SessionId 一并提交到后台(如果开启 Cookie，会自动提交)。SessionId 在Cookie、 Header 、Query中均可，框架会按顺序依次解析，找到即停止。

> 关于 SessionId 和 Token。
	Session: 俗称会话 (: ;  Token :俗称令牌 (:
  	其实，本质上来说，都是一个一段时间内唯一的字符串，用以标识来访者的合法性。在本框架内，禁用 Cookie 后， SessionID 就相当于 Token。认证成功后，将 SessionId 发给客户端，客户端将其存储在本地 LocalStorage 里，每次请求的时候一并发送至后端。这在 APP后台、者小程序后台、前后端分离等无 Cookie 的场景下比较适合。

###关于 JWT (探讨)
&emsp;&emsp;Heimdall框架暂时未对 JWT 进行支持。
&emsp;&emsp;Heimdall框架主要侧重:认证授权状态后台管理，这种模式下，统一由后台进行维护,后台服务需要确切知道，谁?要做什么?能做不能做?结果是什么？不想让他访问系统了怎么办？JWT所有信息都由使用者持有，后台服务不持有任何信息，也就无法对认证授权信息进行管理。当然了，可以把颁发出去的 jwt 令牌信息在后台保存一份，进行失效处理，这不就是 Session 了么？所以，窃以为，JWT 更适合服务间授权或者临时授权的场景，也就是那种短期内，用一次就失效的场景。
这两种模式，其实与是否 PC 端还是移动端没任何关系。

** 举个栗子：**
 &emsp;&emsp;你小区进门需要门禁卡，你有一张，你这是永久的，门禁系统维护着你这张门禁卡的信息，这就相当于 Session.
 &emsp;&emsp;外卖员送餐给你,要进小区，你不能给他门禁卡啊，给个临时许可(临时卡啥的)，有效期写清楚，比如就 2 个小时，外卖员这2 个小时可以进出小区，嫌不安全，还可以更短时间，这就相当于 JWT。这个临时卡外卖员自己拿着，他在合法时间内进小区，你管不了，他把临时卡给别人，别人进小区，你也管不了。所以愚见以为，JWT 用在充分信任临时授权的场景下更合适。

### 主要功能

-  **过期机制**：可设置 Session 生命周期，长时间不活动，自动清理。
-  **Session 事件**：Session 在创建销毁等状态下会发出事件，便于调用者做其他操作。
-  **续签机制**：指的是对 Session 的生命时长自动延期的操作，不至于一直在访问还过期了。。
 	- 内存缓存：这种情况下，Session 主要依靠lastAccessTime与当前时间来判断是否过期，每次合法访问系统资源，lastAccessTime都会被更新到访问当时的时间。如果长时间不操作，自动清理任务会自动判断并且清理过期 Session。
 	- Redis 缓存：Redis 缓存自身具备过期自动删除的功能。当 Session 的 TTL到 0 后，Redis 会自动清理。这种情况下，续签其实就是将 Session Key 的 TTL 恢复到全局过期时间。框架提供 Redis 缓存的 Session 续签功能，可设置是否续签，以及时间低于全局超时多少比例了续签，这样能避免频繁更新缓存。同时框架还对Redis g 的过期、删除等事件进行了监听，当发生过期和删除操作的时候，做其他一些操作，比如清理在线用户列表缓存等。
- **在线用户列表**
	- 内存缓存：在线用户列表不支持分页查询。适用于同时在线用户比较少的情况。
	- Redis 缓存: 支持分页查询在线用户。在线用户通过ZSet 进行了存储。
- **在线用户踢出**
	支持通过 SessionId 或者 Principal踢出用户，同时支持踢出事件通知功能。
- **重复登录判定**
  重复登录分两种情况：
	-   用户在提交登录认证请求的时候，请求中携带了合法的未过期的认证凭据(Session Or Cookie)
	     框架会判断提交的认证凭据合法性，合法就直接返回，不再执行认证逻辑。
	-   用户在提交登录认证请求的时候，未携带任何认证凭据
        首先根据提交的认证实体中的 UserDetails.getPrincipal()信息，在缓存中查找是否存在，如果存在则返回已经存在的 Session 信息，否则执行新的认证流程。


- - -



## 授权 (能做什么?)

&emsp;&emsp;**Authorization**，也就是授权操作，指的是对来访用户是否具备某操作权限进行判断进而采取对应措施。授权解决“能做什么?”的问题。
### 路由授权

#### 说明
路由授权通过拦截器实现。路由权限分为两种类型：
* 精确路由路径鉴权
  精确路由路径指的是系统内 url 都具有唯一性，不区分  Method
  如：
  - 列表: /pet/cat/list
  - 新增: /pet/cat/save
  - 修改: /pet/cat/update
  - 删除: /pet/cat/delete

    这种模式，系统提供了：SimpleGrantedAuthority 授权实体，通过唯一 perm 标识一个路由资源
    首先根据系统拦截规则对 url 进行拦截，然后通过需要的 perm 标识与用户具有的 perm 标识进行比对判定是否授权。

    缓存的拦截规则采用 Map&lt;String,String&gt; 存储，格式如下所示：

    |  key   | value  |
    |  ----  | ----  |
    | /pet/cat/list  | catList |
    | /pet/cat/save  | catSave |

    **key**:*url*

    **value**: *perm 权限唯一标识符，不为空且唯一*
* Restful资源授权
  对，就你熟悉的那个 Restful。这种模式下 url 不再唯一，需要 method+url 定位路由
  如：
  - 列表: GET /pet/cat
  - 新增: POST /pet/cat
  - 修改: PUT /pet/cat
  - 删除: DELETE /pet/cat/{id}

    Restful资源授权，系统提供了MethodAndUrlGrantedAuthority授权实体，通过 method+url 的形式组合权限。
    首先根据系统拦截规则对，对请求 url 进行拦截，然后遍历用户具有的权限，如果有 url 相同，且 method 与当前请求
    method 相同或者 method = "all"的权限，则授权通过。

    缓存的拦截规则采用 Map&lt;String,String&gt; 存储，格式如下所示：

    |  key   | value  |
    |  ----  | ----  |
    | /pet/cat  | "" |
    | /pet/cat  | "" |

    **key**:*url*

    **value**: *无实际意义，不会用到。*

    这种模式的系统权限，其实就是一个拦截器匹配规则，需要使用有序 Map，保证拦截顺序


#### 授权逻辑
0. 当用户请求某个路由资源，如: GET /pet/cat
1. 查找系统权限拦截规则,先从缓存获取,没有则从数据库获取然后存入缓存，返回
2. 遍历系统权限，以 url 进行 Ant 风格匹配判断，如果匹配，则拦截授权开始，不匹配则通过
3. 获取当前登录用户具有的权限列表，遍历匹配
4. 判断当前系统支持的授权形式，如果是:MethodAndUrlGrantedAuthority，进入 Restful 风格资源鉴权，
   如果是：SimpleGrantedAuthority 进入精确路由路径模式鉴权。
5. Restful资源授权
   5.1 拿到当前request 的 request.method 和 request.url,遍历用户具备的所有权限
   5.2 如果存在与 user.url = request.url ,则继续判断 method，否则，不具备权限。
   5.3 如果user.method ==request.method ||"all".equals(user.method)，则具备权限。
6. 精确路由路径鉴权
   6.1 通过 request.url 从系统权限拦截规则（Map）中获取到对应规则
   6.2 遍历用户权限，以 url查找，找到继续判断用户 perm （SimpleGrantedAuthority.getAuthority()）与拦截规则.perm 是否相等? 相等，则授权通过,否则授权不通过。

### 注解授权

系统实现了如下几类注解权限：
```javascript
//认证状态
@RequiresUser //  是否登录

//角色授权
@RequiresRole //是否具备单个角色 如: @RequiresRole("admin")
@RequiresRoles //是否具备多个角色其一或者全部，如:@RequiresRoles(value={"admin","user"},mode=Mod.Any)

//权限标识
@RequiresPermission //是否具备某个权限标识符，如: @RequiresPermission("catList")
@RequiresPermissions //是否具备多个权限标识符其一或者全部，如:@RequiresPermissions(value={"catList","catSave"},mode=Mod.ALL)

```

同时，系统提供获取当前认证用户信息注解: @CurrentUser SimpleSession user

使用示例：


```java
@RequestMapping("/current")
    public ResponseEntity<ResponseVO<SysUserDTO>>
    currentUser(@CurrentUser SimpleSession user) {
        final PcUserDetails details = (PcUserDetails) user.getDetails();
        return ResponseUtils.ok(details.getUser());
    }

```