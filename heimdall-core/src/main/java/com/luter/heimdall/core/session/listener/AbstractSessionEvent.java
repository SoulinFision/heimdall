package com.luter.heimdall.core.session.listener;

import com.luter.heimdall.core.session.SimpleSession;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Session 事件监听抽象类
 * <p>
 * 主动捕获异常，避免影响主要业务的执行。事件是否发出关系不大
 *
 * @author Luter
 */
@Slf4j
public abstract class AbstractSessionEvent {

    /**
     * Session 事件监听器
     */
    private Collection<SessionEventListener> listeners = new ArrayList<>();

    /**
     * Session 创建
     *
     * @param session the session
     */
    public void afterCreated(SimpleSession session) {
        for (SessionEventListener listener : this.listeners) {
            try {
                listener.afterCreated(session);
            } catch (Exception e) {
                log.error("Session事件监听 afterRead 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * Session 读取
     *
     * @param session the session
     */
    public void afterRead(SimpleSession session) {

        for (SessionEventListener listener : this.listeners) {
            try {
                listener.afterRead(session);
            } catch (Exception e) {
                log.error("Session事件监听 afterRead 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * Session 更新
     *
     * @param session the session
     */
    public void afterUpdated(SimpleSession session) {
        for (SessionEventListener listener : this.listeners) {
            try {
                listener.afterUpdated(session);
            } catch (Exception e) {
                log.error("Session事件监听 afterUpdated 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * Session 删除
     *
     * @param session the session
     */
    public void afterDeleted(SimpleSession session) {
        for (SessionEventListener listener : this.listeners) {
            try {
                listener.afterDeleted(session);
            } catch (Exception e) {
                log.error("Session事件监听 afterDeleted 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * 过期 Session 清理
     */
    public void afterSessionValidScheduled() {
        for (SessionEventListener listener : this.listeners) {
            try {
                listener.afterSessionValidScheduled();
            } catch (Exception e) {
                log.error("Session事件监听 afterSessionValidScheduled 出现错误:{}", e.getMessage(), e);
            }
        }
    }

    /**
     * Gets listeners.
     *
     * @return the listeners
     */
    public Collection<SessionEventListener> getListeners() {
        return listeners;
    }

    /**
     * Sets listeners.
     *
     * @param listeners the listeners
     */
    public void setListeners(Collection<SessionEventListener> listeners) {
        this.listeners = listeners;
    }
}
