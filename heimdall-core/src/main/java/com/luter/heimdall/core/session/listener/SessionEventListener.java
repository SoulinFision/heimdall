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

package com.luter.heimdall.core.session.listener;

import com.luter.heimdall.core.session.SimpleSession;

/**
 * Session事件监听
 *
 * @author luter
 */
public interface SessionEventListener {
    /**
     * Session 创建
     *
     * @param session the session
     */
    default void afterCreated(SimpleSession session) {
    }

    /**
     * Session 读取
     *
     * @param session the session
     */
    default void afterRead(SimpleSession session) {
    }

    /**
     * Session 更新
     *
     * @param session the session
     */
    default void afterUpdated(SimpleSession session) {
    }

    /**
     * Session 删除
     *
     * @param session the session
     */
    default void afterDeleted(SimpleSession session) {
    }

    /**
     * 过期 Session 清理
     */
    default void afterSessionValidScheduled() {
    }


}
