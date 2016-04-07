package com.kuassivi.october.di.module;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module to wrap the Application states and expose it to the graph.
 */
@Module
public class BaseApplicationModule {

    /**
     * The current {@link Application}
     */
    protected final Application application;

    /**
     * Once initialized it provides the current {@link Application} to the constructor.
     *
     * @param application the current {@link Application}
     */
    public BaseApplicationModule(Application application) {
        this.application = application;
    }

    /**
     * Exposes the Application to dependents in the graph.
     *
     * @return the current {@link Application}
     */
    @Provides
    @Singleton
    protected Application provideApplication() {
        return this.application;
    }

    /**
     * Exposes the Application Context to dependents in the graph.
     *
     * @return the current {@link Context}
     */
    @Provides
    @Singleton
    protected Context provideContext() {
        return this.application.getApplicationContext();
    }
}
