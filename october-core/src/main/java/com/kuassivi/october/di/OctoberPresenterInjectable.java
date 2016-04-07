package com.kuassivi.october.di;

import android.support.annotation.Nullable;

import dagger.Lazy;

/**
 * Presenter Injectable contract.
 */
public interface OctoberPresenterInjectable {

    /**
     * Provides the current Lazy Presenter class.
     *
     * @param clazz requested Presenter class.
     * @param <P> Presenter interface type.
     * @return the current Lazy Presenter class.
     */
    @Nullable
    <P> Lazy<P> get(Class<P> clazz);
}
