/*
 *    Copyright 2020-2021 Luter.me
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.luter.heimdall.cache.redis.session;


import com.luter.heimdall.core.authorization.authority.GrantedAuthority;
import com.luter.heimdall.core.config.Config;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.config.property.AuthorityProperty;
import com.luter.heimdall.core.cookie.CookieService;
import com.luter.heimdall.core.details.UserDetails;
import com.luter.heimdall.core.exception.*;
import com.luter.heimdall.core.servlet.ServletHolder;
import com.luter.heimdall.core.session.Page;
import com.luter.heimdall.core.session.SimpleSession;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.session.generator.SessionIdGenerator;
import com.luter.heimdall.core.session.generator.UUIDSessionIdGeneratorImpl;
import com.luter.heimdall.core.session.listener.AbstractSessionEvent;
import com.luter.heimdall.core.utils.StrUtils;
import com.luter.heimdall.core.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Redis session dao.
 *
 * @author Luter
 */
@Slf4j
public class RedisSessionDaoImpl extends AbstractSessionEvent implements SessionDAO {

    /**
     * The Session cache.
     */
    private final RedisTemplate<String, SimpleSession> sessionCache;
    /**
     * The User auth cache.
     */
    private final RedisTemplate<String, List<? extends GrantedAuthority>> userAuthCache;

    /**
     * The Session id generator.
     */
    private SessionIdGenerator sessionIdGenerator;
    /**
     * The Active user cache.
     */
    private final StringRedisTemplate activeUserCache;
    /**
     * The Servlet holder.
     */
    private ServletHolder servletHolder;
    /**
     * The Cookie provider.
     */
    private CookieService cookieProvider;


    /**
     * Instantiates a new Redis session dao.
     *
     * @param sessionCache    the session cache
     * @param activeUserCache the active user cache
     * @param userAuthCache   the user auth cache
     * @param servletHolder   the servlet holder
     * @param cookieProvider  the cookie provider
     */
    public RedisSessionDaoImpl(RedisTemplate<String, SimpleSession> sessionCache, StringRedisTemplate activeUserCache,
                               RedisTemplate<String, List<? extends GrantedAuthority>> userAuthCache,
                               ServletHolder servletHolder, CookieService cookieProvider) {
        if (null == sessionCache) {
            throw new SessionException("sessionCache 不能为空");
        }
        if (null == activeUserCache) {
            throw new SessionException("activeUserCache 不能为空");
        }
        if (null == userAuthCache) {
            throw new SessionException("userAuthCache 不能为空");
        }
        final Config config = ConfigManager.getConfig();
        if (config.getCookie().getEnabled()) {
            if (null == servletHolder || null == cookieProvider) {
                throw new CookieException("请实现或者Set ServletHolder、cookieProvider实现类,或者关闭Cookie功能");
            }
        }
        this.sessionCache = sessionCache;
        this.userAuthCache = userAuthCache;
        this.sessionIdGenerator = new UUIDSessionIdGeneratorImpl();
        this.activeUserCache = activeUserCache;
        this.servletHolder = servletHolder;
        this.cookieProvider = cookieProvider;
    }

    /**
     * Instantiates a new Redis session dao.
     *
     * @param sessionCache       the session cache
     * @param sessionIdGenerator the session id generator
     * @param activeUserCache    the active user cache
     * @param userAuthCache      the user auth cache
     * @param servletHolder      the servlet holder
     * @param cookieProvider     the cookie provider
     */
    public RedisSessionDaoImpl(RedisTemplate<String, SimpleSession> sessionCache, SessionIdGenerator sessionIdGenerator,
                               StringRedisTemplate activeUserCache, RedisTemplate<String, List<? extends GrantedAuthority>> userAuthCache,
                               ServletHolder servletHolder, CookieService cookieProvider) {
        if (null == sessionCache) {
            throw new SessionException("sessionCache 不能为空");
        }
        if (null == activeUserCache) {
            throw new SessionException("activeUserCache 不能为空");
        }
        if (null == userAuthCache) {
            throw new SessionException("userAuthCache 不能为空");
        }
        final Config config = ConfigManager.getConfig();
        if (config.getCookie().getEnabled()) {
            if (null == servletHolder || null == cookieProvider) {
                throw new CookieException("请实现或者Set ServletHolder、cookieProvider实现类,或者关闭Cookie功能");
            }
        }
        this.sessionCache = sessionCache;
        this.userAuthCache = userAuthCache;
        this.sessionIdGenerator = null == sessionIdGenerator ? new UUIDSessionIdGeneratorImpl() : sessionIdGenerator;
        this.activeUserCache = activeUserCache;
        this.servletHolder = servletHolder;
        this.cookieProvider = cookieProvider;
    }

    @Override
    public SimpleSession create(UserDetails userDetails) {
        if (null == sessionCache) {
            throw new CacheException("cache must not be null");
        }
        if (null == sessionIdGenerator) {
            throw new HeimdallException("sessionIdGenerator  must not be null");
        }
        final Config config = ConfigManager.getConfig();
        if (null == config) {
            throw new CacheException("Config  must not be null");
        }
        final String sessionId = sessionIdGenerator.generate();
        SimpleSession session = new SimpleSession();
        session.setId(sessionId);
        if (null == userDetails) {
            throw new AccountException(" userDetail must not be null ");
        }
        if (!userDetails.enabled()) {
            throw new NonEnabledAccountException();
        }
        session.setDetails(userDetails);
        //根据 principal 判断用户是否登录
        final SimpleSession sessionIdByUid = getByPrincipal(userDetails.getPrincipal());
        if (null != sessionIdByUid) {
            log.debug("这家伙已经登录过了:{}", session.getDetails().getPrincipal());
            //更新一下 Session
            update(sessionIdByUid);
            return sessionIdByUid;
        }
        //拿远端IP
        if (null != servletHolder) {
            session.setHost(WebUtils.getRemoteIp(servletHolder.getRequest()));
        }
        long globalSessionTimeout = getGlobalSessionTimeout();
        session.setTimeout(globalSessionTimeout);
        //存redis
        sessionCache.opsForValue().set(getSessionIdPrefix() + sessionId, session, globalSessionTimeout, TimeUnit.SECONDS);
        if (null != activeUserCache) {
            LocalDateTime localDateTime = LocalDateTime.now();
            //把当天日期作为 Score
            final String score = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            activeUserCache.opsForZSet().add(getActiveSessionCacheKey(), session.getId(), Long.parseLong(score));
            activeUserCache.opsForHash().put(getActiveUserCacheKey(), session.getDetails().getPrincipal(), session.getId());
        }
        //写入cookie
        if (config.getCookie().getEnabled()) {
            if (null != cookieProvider) {
                cookieProvider.addCookie(sessionId);
            } else {
                throw new CookieException("Cookie Provider must not be null");
            }
        } else {
            log.warn("Cookie 功能未开启");
        }
        //发布事件
        afterCreated(session);
        return session;
    }


    @Override
    public SimpleSession readSession(String sessionId) throws InvalidSessionException {
        final SimpleSession session = sessionCache.opsForValue().get(getSessionIdPrefix() + sessionId);
        //发布事件
        afterRead(session);
        return session;
    }

    @Override
    public SimpleSession update(SimpleSession session) throws InvalidSessionException {
        //拿到全局配置
        final Config config = ConfigManager.getConfig();
        //全局过期时间
        final long globalSessionTimeout = getGlobalSessionTimeout();
        //组合缓存 Session Key
        String sessionKey = getSessionIdPrefix() + session.getId();
        //写入缓存，不改变过期时间,这个方式有可能出问题，如果 Session 长度与原来不一致，就有偏差了
        //sessionCache.opsForValue().set(SessionKey, session, 0);
        //先拿到当前的 ttl,然后带着修改数据后的 Session 一起写入
        Long expire = sessionCache.getExpire(sessionKey);
        if (null != expire) {
            //没有这个 SessionKey. 这基本不可能，但是还是需要处理一下
            if (-2 == expire) {
                log.error(" 修改 Session 数据失败. Redis 中不存在 key :{} ,请检查数据是否正确? ", sessionKey);
                throw new InvalidSessionException();
            }
            //可能是错误修改 key 值或其他原因，反正就是导致 key 的 ttl 变成-1 了，也就是没限制了
            //直接重置到默认全局时长
            if (-1 == expire) {
                log.warn("请注意: 修改 Session 时发现 Session :{} 的 TTL = -1，重置 TTL 至 :{}", session.getId(), globalSessionTimeout);
                expire = globalSessionTimeout;
            }
            //如果开启了续签，计算是否需要续签
            //如果expire=globalSessionTimeout那等于已经续签了,直接过
            if (expire != globalSessionTimeout && config.getSession().isRenew()) {
                log.debug("续签 Session ，设置续签比例:{}", config.getSession().getRatio());
                //小于0.1按0.1对待
                double radio = Math.max(config.getSession().getRatio(), 0.1);
                //大于0.9按0.9对待
                radio = Math.min(radio, 0.9);
                log.debug("续签 Session ，实际续签比例:{}", radio);
                // 当前剩余时间占比
                final double v = expire.doubleValue() / globalSessionTimeout;
                if (v < radio) {
                    log.info("续签 Session [key:{}],当前剩余时间:[{}]秒,全局过期时间:[{}]秒，占比:[{}],低于设置值:[{}],续签至全局时长:[{}] 秒",
                            sessionKey, expire.doubleValue(), globalSessionTimeout,
                            v, radio, globalSessionTimeout);
                    //把过期时间重置为全局过期时间，也就是续签
                    expire = globalSessionTimeout;
                } else {
                    log.info("续签 Session [key:{}],当前剩余时间:[{}]秒,全局过期时间:[{}]秒，占比:[{}],高于设置值:[{}],不做处理",
                            sessionKey, expire.doubleValue(), globalSessionTimeout,
                            v, radio);
                }
            }
            //设置最后访问时间
            session.setLastAccessTime(new Date());
            //执行更新操作
            sessionCache.opsForValue().set(sessionKey, session, expire, TimeUnit.SECONDS);
            //发布事件
            afterUpdated(session);
            return sessionCache.opsForValue().get(sessionKey);
        } else {
            log.error("续签 Session [key:{}] ,获取过期时间失败", sessionKey);
        }
        //原session 返回
        log.warn("Session 更新操作失败。未做更新.Key: [{}]", sessionKey);
        return session;
    }

    @Override
    public void delete(SimpleSession session) {
        final Config config = ConfigManager.getConfig();
        //清理用户权限缓存
        clearUserAuthorities(session.getId());
        //删除用户 Session 缓存
        sessionCache.delete(getSessionIdPrefix() + session.getId());
        if (null != activeUserCache) {
            activeUserCache.opsForZSet().remove(getActiveSessionCacheKey(), session.getId());
            activeUserCache.opsForHash().delete(getActiveUserCacheKey(), session.getDetails().getPrincipal());
        }
        //删除cookie
        if (config.getCookie().getEnabled()) {
            if (null != cookieProvider) {
                cookieProvider.delCookie();
            } else {
                throw new CookieException("Cookie Provider must not be null");
            }
        }
        //发布事件
        afterDeleted(session);
    }

    @Override
    public Collection<SimpleSession> getActiveSessions() {
        final Set<String> keys = sessionCache.keys(getSessionIdPrefix() + "*");
        if (null != keys && !keys.isEmpty()) {
            return sessionCache.opsForValue().multiGet(keys);
        }
        return null;
    }

    @Override
    public Page<SimpleSession> getActiveSessions(int pageNo, int pageSize) {
        final Config config = ConfigManager.getConfig();
        //先从zset分页拿SessionId
        final Set<String> range = activeUserCache.opsForZSet().range(config.getSession().getActiveSessionCacheKey(),
                getStart(pageNo, pageSize), getEnd(pageNo, pageSize));
        //再从所有Session中获取这批SessionId对应的数据
        if (null != range && !range.isEmpty()) {
            ////管道获取
            final List<Object> activeSessions = sessionCache.executePipelined((RedisCallback<Object>) connection -> {
                if (!CollectionUtils.isEmpty(range)) {
                    for (String key : range) {
                        //组合出Session缓存key
                        connection.get((getSessionIdPrefix() + key).getBytes());
                    }
                }
                return null;
            });
            if (!activeSessions.isEmpty()) {
                final List<SimpleSession> records = activeSessions.stream().map(d -> {
                    if (null != d) {
                        return (SimpleSession) d;
                    }
                    return null;
                }).collect(Collectors.toList());
                Long count = activeUserCache.opsForZSet().count(config.getSession().getActiveSessionCacheKey(), 0, 100000000);
                if (null == count) {
                    count = 0L;
                }
                log.debug("分页获取在线用户，在线用户总数:{},本页数据总数:{}", count, records.size());
                return new Page<>(pageNo, pageSize, count, records);
            } else {
                log.debug("分页获取在线用户，online zSet中存在，但是真实token 不存在");
            }
            /////mget获取
            //要查找的SimpleSession，组合前缀和SessionId作为key
//            List<String> sessionToGet = new ArrayList<>();
//            for (String s : range) {
//                if (!StrUtils.isNotBlank(s)) {
//                    continue;
//                }
//                String key = getSessionIdPrefix() + s;
//                log.warn("在线Session，key:{}", key);
//                sessionToGet.add(key);
//            }
//            if (!sessionToGet.isEmpty()) {
//                //multiGet 方式获取
//                //这里面需要注意，由于ZSet清理的不及时或者有错误,导致已经失效的Session没有及时清理
//                // 这样在mget的时候，就会导致返回的列表list中可能存在空数据null
//                //调用的时候，需要对空值进行判断处理，免得前端处理出现错误
//
//                final List<SimpleSession> list = sessionCache.opsForValue().multiGet(sessionToGet);
//                if (null != list && !list.isEmpty()) {
//                    Long count = activeUserCache.opsForZSet().count(config.getSession().getActiveSessionCacheKey(), 0, 100000000);
//                    if (null == count) {
//                        count = 0L;
//                    }
//                    log.warn("分页获取在线用户，在线用户总数:{},本页数据总数:{}", count, list.size());
//                    return new Page<>(pageNo, pageSize, count, list);
//                } else {
//                    log.warn("分页获取在线用户，online zSet中存在，但是真实token 不存在");
//                }
//            }

        }
        return new Page<>(pageNo, pageSize, 0, null);
    }


    @Override
    public SimpleSession getByPrincipal(String principal) {
        final Config config = ConfigManager.getConfig();
        final Object sessionId = activeUserCache.opsForHash().get(config.getSession().getActiveUserCacheKey(), principal);
        if (null == sessionId) {
            return null;
        }
        return sessionCache.opsForValue().get(getSessionIdPrefix() + sessionId.toString());
    }

    @Override
    public void validateExpiredSessions() {
        final Config config = ConfigManager.getConfig();
        log.info("清理无效在线用户ID=  开始");
        //合法SimpleSessions,这里没有，但是上面2个里面有的，就是需要清理的,这里的数据带Session前缀
        final Set<String> validSessions = sessionCache.keys(getSessionIdPrefix() + "*");
        //如果当前就没有登录用户，Hash 和 Zset全部清空
        if (null == validSessions || validSessions.isEmpty()) {
            log.info("没合法的SessionId,Hash 和 Zset全部清空");
            activeUserCache.delete(config.getSession().getActiveUserCacheKey());
            activeUserCache.delete(config.getSession().getActiveSessionCacheKey());
            //缓存的所有用户权限也全部清除
            clearAllUserAuthorities();
        } else {
            log.info("当前合法Session总数:{},现在开始进行Hash和ZSet清理", validSessions.size());
            //活动用户Hash
            final Map<Object, Object> activeUsers = activeUserCache.opsForHash().entries(config.getSession().getActiveUserCacheKey());
            if (!activeUsers.isEmpty()) {
                log.info("当前活动用户Hash总数:{}", activeUsers.size());
                List<String> activeUsersToBeDeleted = new ArrayList<>();
                for (Map.Entry<Object, Object> data : activeUsers.entrySet()) {
                    final Object value = data.getValue();
                    if (null != value) {
                        String sessionKey = getSessionIdPrefix() + value.toString();
                        //合法session里没有这个value，说明这个无效了
                        if (!validSessions.contains(sessionKey)) {
                            //把 SessionId (value) 放入待删除列表
                            activeUsersToBeDeleted.add(sessionKey);
                        }
                    }
                }
                log.info("当前 待清理 活动用户Hash总数:{}", activeUsersToBeDeleted.size());
                if (!activeUsersToBeDeleted.isEmpty()) {
                    log.info("被清理的 活动用户Hash，总数:{}", activeUsersToBeDeleted.size());
                    activeUserCache.opsForHash().delete(config.getSession().getActiveUserCacheKey(), activeUsersToBeDeleted.toArray());
                }
            }
            //活动Session ZSet
            final Set<String> activeSessions = activeUserCache.opsForZSet().range(config.getSession().getActiveSessionCacheKey(), 0, -1);
            if (null != activeSessions && !activeSessions.isEmpty()) {
                log.info("当前 活动Session ZSet 总数:{}", activeSessions.size());
                final List<String> activeSessionsToBeDeleted =
                        activeSessions.stream().filter(d -> !validSessions.contains(getSessionIdPrefix() + d))
                                .collect(Collectors.toList());
                if (!activeSessionsToBeDeleted.isEmpty()) {
                    log.info("被清理的 活动Session ZSet，总数:{}", activeSessionsToBeDeleted.size());
                    activeUserCache.opsForZSet().remove(config.getSession().getActiveSessionCacheKey(), activeSessionsToBeDeleted.toArray());
                }

            } else {
                log.info("当前 活动Session ZSet 为空，不做处理");
            }

        }
        //发布事件
        afterSessionValidScheduled();
    }

    /**
     * Gets session id generator.
     *
     * @return the session id generator
     */
    public SessionIdGenerator getSessionIdGenerator() {
        return sessionIdGenerator;
    }

    /**
     * Sets session id generator.
     *
     * @param sessionIdGenerator the session id generator
     */
    public void setSessionIdGenerator(SessionIdGenerator sessionIdGenerator) {
        this.sessionIdGenerator = sessionIdGenerator;
    }

    /**
     * Gets session cache.
     *
     * @return the session cache
     */
    public RedisTemplate<String, SimpleSession> getSessionCache() {
        return sessionCache;
    }


    /**
     * Gets active user cache.
     *
     * @return the active user cache
     */
    public StringRedisTemplate getActiveUserCache() {
        return activeUserCache;
    }

    /**
     * Sets servlet holder.
     *
     * @param servletHolder the servlet holder
     */
    public void setServletHolder(ServletHolder servletHolder) {
        this.servletHolder = servletHolder;
    }

    @Override
    public ServletHolder getServletHolder() {
        return servletHolder;
    }

    @Override
    public CookieService getCookieService() {
        return cookieProvider;
    }

    /**
     * Sets cookie provider.
     *
     * @param cookieProvider the cookie provider
     */
    public void setCookieProvider(CookieService cookieProvider) {
        this.cookieProvider = cookieProvider;
    }

    /**
     * 获取 SessionID 前缀
     *
     * @return the session id prefix
     */
    public String getSessionIdPrefix() {
        String sessionIdPrefix = ConfigManager.getConfig().getSession().getSessionIdPrefix();
        return StrUtils.isBlank(sessionIdPrefix) ? DEFAULT_SESSION_ID_PREFIX : sessionIdPrefix;
    }

    /**
     * Gets global session timeout.
     *
     * @return the global session timeout
     */
    public long getGlobalSessionTimeout() {
        final long globalSessionTimeout = ConfigManager.getConfig().getSession().getGlobalSessionTimeout();
        return globalSessionTimeout < 30 ? GLOBAL_SESSION_TIMEOUT : globalSessionTimeout;
    }

    /**
     * Gets active session cache key.
     *
     * @return the active session cache key
     */
    public String getActiveSessionCacheKey() {
        String activeSessionCacheKey = ConfigManager.getConfig().getSession().getActiveSessionCacheKey();
        return StrUtils.isBlank(activeSessionCacheKey) ? DEFAULT_ACTIVE_SESSION_CACHE_KEY : activeSessionCacheKey;
    }

    /**
     * Gets active user cache key.
     *
     * @return the active user cache key
     */
    public String getActiveUserCacheKey() {
        final String activeUserCacheKey = ConfigManager.getConfig().getSession().getActiveUserCacheKey();
        return StrUtils.isBlank(activeUserCacheKey) ? DEFAULT_ACTIVE_USER_CACHE_KEY : activeUserCacheKey;
    }

    /**
     * 获取当前分页起始记录索引
     *
     * @param page the page
     * @param size the size
     * @return the start
     */
    private long getStart(int page, int size) {
        long l = (long) (page - 1) * (long) size;
        l = l < 0 ? 0 : l;
        return l;
    }

    /**
     * 获取当前分页结束记录索引
     *
     * @param page the page
     * @param size the size
     * @return the end
     */
    private long getEnd(int page, int size) {
        return page * size;
    }


    /**
     * 清理 zset 缓存和 hash 缓存
     * <p>
     * 主要提供给 redis 事件使用
     */
    @Override
    public void clearOnlineUserCache(String sessionId) {
        final Config config = ConfigManager.getConfig();
        //清理用户的权限缓存
        log.warn("清理用户权限缓存,SessionId:[{}]", sessionId);
        userAuthCache.opsForHash().delete(config.getAuthority().getUserCachedKey(), sessionId);
        log.info("Session 删除事件 ,key:[{}],从zSet删除", sessionId);
        //删除ZSet中对应Key (SessionId)
        final Long session = activeUserCache.opsForZSet().remove(config.getSession().getActiveSessionCacheKey(), sessionId);
        log.info("Session 删除事件 ,key:[{}],从 Session zSet 删除，结果:{}", sessionId, session);
        //拿到 Hash 中所有数据
        final Map<Object, Object> entries = activeUserCache.opsForHash().entries(config.getSession().getActiveUserCacheKey());
        List<String> toBeDeleted = new ArrayList<>();
        if (!entries.isEmpty()) {
            //遍历，如果value(sessionId)  与传入的SessionId相同,把key加入待删除List
            for (Map.Entry<Object, Object> data : entries.entrySet()) {
                if (data.getValue().equals(sessionId)) {
                    toBeDeleted.add(data.getKey().toString());
                }
            }
        }
        // Hash 中有数据要删除
        if (!toBeDeleted.isEmpty()) {
            final Long user = activeUserCache.opsForHash().delete(config.getSession().getActiveUserCacheKey(), toBeDeleted.toArray());
            log.info("Session 删除事件 ,key:[{}],从 User Hash 删除，结果:{}", sessionId, user);
        }
    }

    /////用户权限缓存


    @Override
    public void setUserAuthorities(String sessionId, List<? extends GrantedAuthority> authorities) {
        final AuthorityProperty authority = ConfigManager.getConfig().getAuthority();
        if (null != authorities && !authorities.isEmpty()) {
            userAuthCache.opsForHash().put(authority.getUserCachedKey(), sessionId, authorities);
            userAuthCache.expire(authority.getUserCachedKey(), authority.getUserExpire(), TimeUnit.HOURS);
        } else {
            log.warn("缓存用户权限失败，用户权限为空");
        }
    }

    @Override
    public List<? extends GrantedAuthority> getUserAuthorities(String sessionId) {
        final AuthorityProperty authority = ConfigManager.getConfig().getAuthority();
        final Object o = userAuthCache.opsForHash().get(authority.getUserCachedKey(), sessionId);
        if (null != o) {
            return StrUtils.castList(o, GrantedAuthority.class);
        }
        return null;
    }

    @Override
    public void clearUserAuthorities(String sessionId) {
        if (StrUtils.isNotBlank(sessionId)) {
            final AuthorityProperty authority = ConfigManager.getConfig().getAuthority();
            log.warn("清理系统用户缓存，SessionId:[{}]", sessionId);
            userAuthCache.opsForHash().delete(authority.getUserCachedKey(), sessionId);
        } else {
            log.warn("清理用户权限缓存失败，SessionID 为空");
        }

    }

    @Override
    public void clearAllUserAuthorities() {
        final AuthorityProperty authority = ConfigManager.getConfig().getAuthority();
        userAuthCache.delete(authority.getUserCachedKey());
    }
}
