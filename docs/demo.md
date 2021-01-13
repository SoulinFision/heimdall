# 示例说明

步骤：
以在 SpringBoot2.4.1 为例:

1. 实现拦截规则数据提供服务，即：告诉框架你对哪些资源进行什么方式的授权?
1. 实现认证实体提供接口，即：告诉框架，你要管理的用户认证信息都是什么，具有什么操作权限和角色？以什么方式进行授权？
1. 配置 SpringBoot ，实现认证和授权管理 Bean。
没了.....



**实现系统权限拦截规则数据提供服务：AuthorizationMetaDataService**

```java
/**
 * 系统权限数据提供服务
 */
@Service
@Slf4j
public class AuthorizationMetaDataServiceImpl implements AuthorizationMetaDataService {
    @Override
    public Map<String, String> loadAuthorities() {
        final List<SysResourceDTO> resources = DataUtil.getSimpleResourceList();
        //需要保证顺序
        Map<String, String> perms = new LinkedHashMap<>(resources.size());
        for (SysResourceDTO sysResourceDTO : resources) {
            //url +perm 构造拦截器链Map
            perms.put(sysResourceDTO.getUrl(), sysResourceDTO.getPerm());
        }
        return perms;

    }
}
```
**定义自己的用户认证实体，实现用户认证详情接口：UserDetails**

```java

/**
 * 自定义用户详情实体
 *
 * @see UserDetails
 * @see DefaultSimpleUserDetails
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@Accessors(chain = true)
@AllArgsConstructor
public class PcUserDetails implements UserDetails {
    /**
     * 携带的信息
     */
    private SysUserDTO user;
    /**
     * 唯一标识
     */
    private String principal;
    /**
     * 具有的权限
     */
    private List< \\? extends GrantedAuthority> authorities;
    /**
     * 具有的角色
     */
    private List<String> roles;

    /**
     * Instantiates a new Default user detail.
     *
     * @param user the user
     */
    public PcUserDetails(SysUserDTO user) {
        this.user = user;
    }

    @Override
    public String getPrincipal() {
        return "PC:" + user.getId();
    }

    @Override
    public boolean enabled() {
        return user.getEnabled();
    }


    @Override
    public List<String> getRoles() {
        return user.getRoles();
    }

    @Override
    public List<\\? extends GrantedAuthority> getAuthorities() {
        //构造精确url授权权限信息载体
        return user.getResources().stream().map(d -> new SimpleGrantedAuthority(d.getPerm())).collect(Collectors.toList());
    }

    ........
}
```



**完成配置**

```java
/**
 * 基于内存缓存和常规url形式授权的配置
 */
@Configuration
@Slf4j
public class CachedSecurityConfig {
    /**
     * 密码加密解密实现
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cookie服务
     *
     * @return the cookie service
     */
    @Bean
    public CookieService cookieService(ServletHolder servletHolder) {
        return new SessionCookieServiceImpl(servletHolder);
    }

    /**
     * Session缓存Dao
     *
     * @param cookieService the cookie service
     * @return the session dao
     */
    @Bean
    public SessionDAO sessionDAO(CookieService cookieService, ServletHolder servletHolder) {
        log.warn("初始化 SessionDAO");
        SimpleCache<String, SimpleSession> mapSimpleCache = new MapCache<>(Maps.newConcurrentMap());
        final CachedSessionDaoImpl cachedSessionDao = new CachedSessionDaoImpl(mapSimpleCache, servletHolder, cookieService);
        //Session事件监听
        List<SessionEventListener> listeners = new ArrayList<>();
        listeners.add(new SessionEventListener() {
            @Override
            public void afterCreated(SimpleSession session) {
                log.warn("Session 事件 : Session 成功创建:{}", session.getId());
            }

            @Override
            public void afterRead(SimpleSession session) {
                log.warn("Session 事件 : afterRead :{}", session.getId());
            }

            @Override
            public void afterUpdated(SimpleSession session) {
                log.warn("Session 事件 : afterUpdated :{}", session.getId());
            }

            @Override
            public void afterDeleted(SimpleSession session) {
                log.warn("Session 事件 : afterDeleted :{}", session.getId());
            }

            @Override
            public void afterSessionValidScheduled() {
                log.warn("Session 事件 : afterSessionValidScheduled");
            }
        });
        cachedSessionDao.setListeners(listeners);
        return cachedSessionDao;
    }

    /**
     * 认证管理器，实现用户登录注销等功能
     *
     * @param sessionDAO Session缓存Dao
     * @return the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(SessionDAO sessionDAO) {
        log.warn("初始化 认证管理器");
        final AuthenticationManager authenticationManager = new AuthenticationManager(sessionDAO);
        List<AuthenticationEventListener> listeners = new ArrayList<>();
        listeners.add(new AuthenticationEventListener() {
            @Override
            public void onLogin(int code, SimpleSession session) {
                log.warn("认证 事件: 用户:[{}] {}"
                        , session.getDetails().getPrincipal()
                        , 1 == code ? "重复登录" : 2 == code ? "登录" : "");

            }

            @Override
            public void onLogout(SimpleSession session) {
                log.warn("认证 事件:用户:[{}] 注销啦", session.getDetails().getPrincipal());
            }

        });
        authenticationManager.setListeners(listeners);
        return authenticationManager;
    }


    /**
     * 系统授权缓存Dao
     * <p>
     * 用户首次访问保护资源的时候，系统会自动从缓存加载需要授权规则，
     * <p>
     * 如果缓存中没有，则会调用AuthorizationMetaDataService加载。
     *
     * @return the authorization meta data cache dao
     */
    @Bean
    public AuthorizationMetaDataCacheDao authorizationMetaDataCacheDao() {
        log.warn("初始化 系统授权数据 MetaDataDao");
//        Cache<String, Map<String, String>> caffeineCache = Caffeine.newBuilder()
//                .expireAfterAccess(Duration.ofHours(1))
//                .recordStats()
//                .build();
//        Caffeine缓存
//        SimpleCache<String, Map<String, String>> caffeineSimpleCache = new CaffeineCache<>(caffeineCache);
//        Map 缓存
        SimpleCache<String, Map<String, String>> mapSimpleCache = new MapCache<>(Maps.newConcurrentMap());
        return new CachedAuthorizationMetaDataDao(mapSimpleCache);
    }

    /**
     * 授权管理器
     *
     * @param authenticationManager 认证管理器
     * @return the authorization manager
     */
    @Bean
    public AuthorizationManager authorizationManager(AuthenticationManager authenticationManager,
                                                     AuthorizationMetaDataService authorizationMetaDataService,
                                                     AuthorizationMetaDataCacheDao authorizationMetaDataCacheDao) {
        log.warn("初始化 授权管理器");
        return new AuthorizationManager(authorizationMetaDataService, authorizationMetaDataCacheDao, authenticationManager);
    }

    /**
     * 授权校验
     */
    @Bean
    public AuthorizationFilterHandler securityFilterHandler(AuthenticationManager authenticationManager) {
        log.warn("初始化 授权过滤器");
        return new DefaultAuthorizationFilterHandler(authenticationManager);
    }

    /**
     * 开启注解授权验证
     *
     * @return the security annotation aspect handler
     */
    @Bean
    public AuthorizationAnnotationAspect securityAspect(AuthorizationFilterHandler authorizationFilterHandler) {
        log.warn("初始化 注解授权");
        return new AuthorizationAnnotationAspect(authorizationFilterHandler);
    }

    /**
     * 开启过期session清理任务
     *
     * @param sessionDAO Session缓存Dao
     * @return the default invalid session clear scheduler
     */
    @Bean
    public DefaultInvalidSessionClearScheduler defaultInvalidSessionClearScheduler(SessionDAO sessionDAO) {
        log.warn("初始化 Session 自动清理任务");
        return new DefaultInvalidSessionClearScheduler(sessionDAO);
    }


}

```

==*更多使用方式请查看示例程序，示例程序中详细演示了各种功能*==
