package com.kuassivi.october.executor;

import rx.Scheduler;

/**
 * Default Executor interface that provides its Scheduler.
 */
public interface ThreadExecutor {

    /**
     * Provide a specific {@link Scheduler}.
     * @return {@link Scheduler}
     */
    Scheduler getScheduler();
}
