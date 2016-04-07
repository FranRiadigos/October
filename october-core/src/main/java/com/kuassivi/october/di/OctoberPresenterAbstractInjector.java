package com.kuassivi.october.di;

import android.support.annotation.Nullable;

import dagger.Lazy;

/**
 * Abstract Presenter injector class.
 */
public abstract class OctoberPresenterAbstractInjector implements OctoberPresenterInjectable {

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    final public <P> Lazy<P> get(Class<P> clazz) {
        return (Lazy<P>) provide(clazz);
    }

    /**
     * This method provides the current Lazy Presenter class.
     *
     * @param clazz requested Presenter class.
     * @param <P>   Presenter interface type.
     * @return the current Lazy Presenter class.
     */
    @Nullable
    public abstract <P> Lazy<?> provide(Class<P> clazz);
}
