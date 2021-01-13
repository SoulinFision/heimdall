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

package com.luter.heimdall.core.session.scheduler;


import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.luter.heimdall.core.config.ConfigManager;
import com.luter.heimdall.core.config.property.SchedulerProperty;
import com.luter.heimdall.core.session.dao.SessionDAO;
import com.luter.heimdall.core.session.listener.AbstractSessionEvent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * 过期 Session 定期清理任务¬
 *
 * @author Luter
 */
@Slf4j
public class DefaultInvalidSessionClearScheduler extends AbstractSessionEvent {

    /**
     * Instantiates a new Default invalid session clear scheduler.
     *
     * @param sessionStore the session store
     */
    public DefaultInvalidSessionClearScheduler(SessionDAO sessionStore) {
        final SchedulerProperty scheduler = ConfigManager.getConfig().getScheduler();
        log.debug("过期Session定时清理任务初始化完毕,配置\n{}", scheduler.toString());
        this.startTask(scheduler.getInitialDelay(), scheduler.getPeriod(), sessionStore);

    }

    /**
     * 初始化任务
     *
     * @param initialDelay the initial delay
     * @param period       the period
     * @param sessionStore the session store
     */
    protected void startTask(long initialDelay, long period, SessionDAO sessionStore) {
        Runnable runnable = () -> {
            try {
                log.info("过期Session定时清理任务执行");
                sessionStore.validateExpiredSessions();
            } catch (Exception e) {
                e.printStackTrace();
                log.error("执行过期Session定时清理任务出现异常:{}", e.getMessage());

            }

        };
        //延迟执行时间（秒）
        initialDelay = initialDelay < 1 ? 600 : initialDelay;
        //执行的时间间隔（秒）
        period = period < 1 ? 300 : period;
        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("-thread-%d").setDaemon(true).build();
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1, namedThreadFactory);
        executorService.scheduleWithFixedDelay(runnable, initialDelay, period, TimeUnit.SECONDS);


    }


}
