package com.kuassivi.october.executor;

import rx.Scheduler;

/**
 * Interface contract which establish the Scheduler that must be applied to an {@link rx.Observable}
 * in its {@link rx.Observable#observeOn(Scheduler)} method.
 */
public interface ObserverThread extends ThreadExecutor {}
