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

package com.kuassivi.october.rx;

import com.kuassivi.october.exception.OctoberException;

import rx.functions.Action1;

/**
 * Default subscriber to be used whenever you want default error handling.
 */
public abstract class OctoberSubscriber<T> extends rx.Subscriber<T> {

    /**
     * Fast implementation for onNext methods.
     * <p>
     * Useful for lambdas.
     *
     * @param action Action to perform onNext
     * @param <T>    value Type retrieved
     * @return {@link OctoberSubscriber}
     */
    public static <T> OctoberSubscriber<T> onNext(final Action1<? super T> action) {
        return new OctoberSubscriber<T>() {
            @Override
            public void onError(OctoberException e) {
                // no-op by default.
            }

            @Override
            public void onNext(T value) {
                action.call(value);
            }
        };
    }

    /**
     * Fast implementation for onError methods.
     * <p>
     * Useful for lambdas purposes.
     * <p>
     * You can access the specific IO Exception through the {@link OctoberException#getCause()}
     * method.
     *
     * @param action Action to perform onError
     * @param <T>    unused value Type retrieved
     * @return {@link OctoberSubscriber}
     */
    public static <T> OctoberSubscriber<T> onError(final Action1<? super Throwable> action) {
        return new OctoberSubscriber<T>() {
            @Override
            public void onError(OctoberException e) {
                action.call(e);
            }

            @Override
            public void onNext(T value) {
                // no-op by default.
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCompleted() {
        // no-op by default.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError(Throwable e) {
        if (e instanceof OctoberException) {
            onError(e);
        } else {
            onError(new OctoberException(e));
        }
    }

    /**
     * It wraps the current Exception into an {@link OctoberException}.
     * <p>
     * You can access the specific IO Exception through the {@link OctoberException#getCause()}
     * method.
     *
     * @param e {@link OctoberException}
     */
    public abstract void onError(OctoberException e);
}
