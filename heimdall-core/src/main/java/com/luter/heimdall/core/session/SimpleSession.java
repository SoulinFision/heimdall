/*
 *
 *  *    Copyright 2020-2021 Luter.me
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */

package com.luter.heimdall.core.session;

import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.details.UserDetails;
import com.luter.heimdall.core.exception.InvalidSessionException;
import com.luter.heimdall.core.exception.SessionException;
import com.luter.heimdall.core.session.generator.UUIDSessionIdGeneratorImpl;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Session对象
 *
 * @author Luter
 */
@Slf4j
public class SimpleSession {
    /**
     * SessionId
     */
    private String id;
    /**
     * Session创建时间
     */
    private Date startTimestamp;
    /**
     * 最近一次的访问时间
     */
    private Date lastAccessTime;
    /**
     * 全局超时时长，单位秒
     */
    private long timeout;
    /**
     * Session是否过期？
     * <p>
     * <p>
     * Session过期后的清理是由定时任务通过判断闲置时间来进行的。
     * <p>
     * 对于内存类型的缓存来说，当timedOut方法判断Session超时后，定时任务负责将超时Session从缓存中移除。
     * <p>
     * 对于redis等具有自主过期机制的缓存来说，Session的超时删除是通过缓存自己本身的机制实现的。
     * <p>
     * expired字段主要用在内存类型缓存场景下，当超时后，将此字段标识为:true,
     * <p>
     * 如果用户在定时清理任务执行之前依旧携带超时SessionID访问系统，
     * 此时系统可根据此状态有针对性的提醒用户：长期闲置，会话超时，重新登录
     * <p>
     * SessionDao#validateExpiredSessions()方法负责对Session是否过期进行判断，如果：
     * expired  =true 或者 lastAccessTime +timeout 超过了当前时间，则认为超时
     * <p>
     * <p>
     * 对于自带超时清理的缓存而言，比如redis，可以依赖redis自身的超时清理机制，这样就无法精准提示session过期错误。
     * <p>
     * 当然了，也可以将redis的过期时间设置为-1，关闭redis自己的清理策略，定时任务去遍历清除。这样会比较低效率。
     */
    private boolean expired;
    /**
     * 访问客户端主机IP地址
     */
    private String host;
    /**
     * Session携带的用户相关数据
     */
    private UserDetails details;

    /**
     * 构造默认Session
     * <p>
     * SessionID采用去掉横线的UUID
     */
    public SimpleSession() {
        this.id = new UUIDSessionIdGeneratorImpl().generate();
        this.timeout = ConfigManager.getConfig().getSession().getGlobalSessionTimeout();
        this.startTimestamp = new Date();
        this.lastAccessTime = this.startTimestamp;
        this.expired = false;
    }

    /**
     * Instantiates a new Simple session.
     *
     * @param id      the id
     * @param timeout the timeout
     */
    public SimpleSession(String id, long timeout) {
        this.id = id;
        this.timeout = timeout;
        this.startTimestamp = new Date();
        this.lastAccessTime = this.startTimestamp;
        this.expired = false;
    }

    /**
     * 判断Session是否超期,
     * <p>
     * 内存缓存下，用作对失效 Session 清理任务的判断
     * <p>
     * true:超期
     * false：正常
     *
     * @return the boolean
     */
    public boolean isTimedOut() {
        //如果设置过期
        if (isExpired()) {
            log.debug("这是个过期了的Session");
            return true;
        }
        //拿到全局过期时长
        long timeout = getTimeout();
        if (timeout >= 0L) {
            //最后访问时间
            Date lastAccessTime = getLastAccessTime();
            //为空的话，就抛异常
            if (lastAccessTime == null) {
                String msg = "session.lastAccessTime for session with id [" +
                        getId() + "] is null.  This value must be set at " +
                        "least once. Please check the " +
                        getClass().getName() + " implementation and ensure " +
                        "this value will be set";
                throw new SessionException(msg);
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //简单说，过期时间点=当前时间 往前推 timeout*1000秒，
            //当前时间不断变化,过期时间在不断追 lastAccessTime，追上了，就认为是超期.
            //最后访问时间 lastAccessTime 会根据业务逻辑动态更新，只要有系统资源访问行为，最后访问时间会被更新
            //如果长时间(超过timeout)没访问行为，Session就会处于过期状态，会被定时清理任务清理
            //也可以手动设置过期：setExpired(true)
            long expireTimeMillis = System.currentTimeMillis() - timeout * 1000;
            Date expireTime = new Date(expireTimeMillis);
            final boolean before = lastAccessTime.before(expireTime);
            log.debug("===The Session  TimedOut===\n===SessionId:{}\n===过期时间:{}\n===最后访问时间:{}\n===剩余时长 :{}秒",
                    getId(),
                    dateFormat.format(expireTime), dateFormat.format(lastAccessTime),
                    ((lastAccessTime.getTime() - expireTimeMillis) / 1000));
            return before;
        } else {
            log.warn("No timeout for session with id [" + getId() +
                    "].  Session is not considered expired.");
        }
        return false;
    }

    /**
     * Gets details.
     *
     * @return the details
     */
    public UserDetails getDetails() {
        return details;
    }

    /**
     * Sets details.
     *
     * @param details the details
     */
    public void setDetails(UserDetails details) {
        this.details = details;
    }

    /**
     * Instantiates a new Simple session.
     *
     * @param host the host
     */
    public SimpleSession(String host) {
        this();
        this.host = host;
    }

    /**
     * Sets start timestamp.
     *
     * @param startTimestamp the start timestamp
     */
    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * Sets last access time.
     *
     * @param lastAccessTime the last access time
     */
    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    /**
     * Sets expired.
     *
     * @param expired the expired
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * Sets host.
     *
     * @param host the host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets start timestamp.
     *
     * @return the start timestamp
     */
    public Date getStartTimestamp() {
        return this.startTimestamp;
    }

    /**
     * Gets last access time.
     *
     * @return the last access time
     */
    public Date getLastAccessTime() {
        return this.lastAccessTime;
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     * @throws InvalidSessionException the invalid session exception
     */
    public long getTimeout() throws InvalidSessionException {
        return this.timeout;
    }

    /**
     * Sets timeout.
     *
     * @param maxIdleTimeInMillis the max idle time in millis
     * @throws InvalidSessionException the invalid session exception
     */
    public void setTimeout(long maxIdleTimeInMillis) throws InvalidSessionException {
        this.timeout = maxIdleTimeInMillis;
    }

    /**
     * Gets host.
     *
     * @return the host
     */
    public String getHost() {
        return this.host;
    }


    /**
     * Is expired boolean.
     *
     * @return the boolean
     */
    public boolean isExpired() {
        return expired;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SimpleSession that = (SimpleSession) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SimpleSession{" +
                "id='" + id + '\'' +
                ", startTimestamp=" + startTimestamp +
                ", lastAccessTime=" + lastAccessTime +
                ", timeout=" + timeout +
                ", expired=" + expired +
                ", host='" + host + '\'' +
                ", userDetail=" + details +
                '}';
    }
}
