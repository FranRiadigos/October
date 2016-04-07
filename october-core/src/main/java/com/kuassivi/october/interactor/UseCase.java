/*******************************************************************************
 * Copyright (c) 2016 Francisco Gonzalez-Armijo Riádigos
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package com.kuassivi.october.interactor;

import com.kuassivi.october.executor.ThreadExecutor;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 */
public abstract class UseCase<T> {

    private final ThreadExecutor threadSubscriber;
    private final ThreadExecutor threadObserver;
    private       Action0        onSubscribe;
    private       Action0        onTerminate;

    protected UseCase(ThreadExecutor threadSubscriber, ThreadExecutor threadObserver) {
        this.threadSubscriber = threadSubscriber;
        this.threadObserver = threadObserver;
    }

    private Observable.Transformer<T, T> buildUseCaseObservable() {
        return observable -> {
            Observable<T> observableBuilder = observable
                    .subscribeOn(threadSubscriber.getScheduler())
                    .observeOn(threadObserver.getScheduler());
            if (onSubscribe != null) {
                observableBuilder = observableBuilder.doOnSubscribe(onSubscribe);
            }
            if (onTerminate != null) {
                observableBuilder = observableBuilder
                        .doOnNext(t -> onTerminate.call())
                        .doOnError(throwable -> onTerminate.call());
            }
            return observableBuilder;
        };
    }

    protected abstract Observable<T> provideObservable();

    final public void onSubscribe(final Action0 action) {
        this.onSubscribe = action;
    }

    final public void onTerminate(final Action0 action) {
        this.onTerminate = action;
    }

    final public Observable<T> asObservable() {
        return provideObservable().compose(buildUseCaseObservable());
    }

    final public Subscription subscribe(Subscriber<T> subscriber) {
        return asObservable().subscribe(subscriber);
    }
}
