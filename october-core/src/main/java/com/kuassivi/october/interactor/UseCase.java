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

import com.kuassivi.october.executor.ObserverThread;
import com.kuassivi.october.executor.SubscriberThread;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Action0;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 */
public abstract class UseCase<T> {

    private final SubscriberThread subscriberThread;
    private final ObserverThread   observerThread;
    private       Action0          onSubscribe;
    private       Action0          onTerminate;

    protected UseCase(SubscriberThread subscriberThread, ObserverThread observerThread) {
        this.subscriberThread = subscriberThread;
        this.observerThread = observerThread;
    }

    /**
     * Builds the provided {@link Observable} and performs some transformation on it.
     *
     * @return The Observable transformation applied.
     */
    private Observable.Transformer<T, T> buildUseCaseObservable() {
        return observable -> {
            Observable<T> observableBuilder = observable
                    .subscribeOn(subscriberThread.getScheduler())
                    .observeOn(observerThread.getScheduler());
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

    /**
     * Implement this method in your custom UseCase in order to provide the final {@link
     * Observable}.
     *
     * @return The provided Observable.
     */
    protected abstract Observable<T> provideObservable();

    /**
     * Allows you to apply and Action to the Observable when it subscribes.
     *
     * @param action Action to be applied when the Observable subscribes.
     */
    final public void onSubscribe(final Action0 action) {
        this.onSubscribe = action;
    }

    /**
     * Allows you to apply and Action to the Observable when it terminates.
     * <p>
     * This action will be fired always no mather whether an Exception happens.
     *
     * @param action Action to be applied when the Observable terminates.
     */
    final public void onTerminate(final Action0 action) {
        this.onTerminate = action;
    }

    /**
     * Exposes the provided {@link Observable}.
     * <p>
     * This is a handful method that allow you i.e. to chain more Observables or apply a map.
     *
     * @return The provided Observable.
     */
    final public Observable<T> asObservable() {
        Observable<T> ob = provideObservable();
        if (ob == null) {
            throw new NullPointerException(
                    String.format("provideObservable() method of %s class returns null.",
                                  this.getClass().getSimpleName()));
        }
        return ob.compose(buildUseCaseObservable());
    }

    /**
     * Allows you to subscribe directly to the provided {@link Observable}.
     *
     * @param subscriber The subscriber object.
     * @return The subscription made by the Observable.
     */
    final public Subscription subscribe(Subscriber<T> subscriber) {
        return asObservable().subscribe(subscriber);
    }
}
