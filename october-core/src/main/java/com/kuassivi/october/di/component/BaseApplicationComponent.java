package com.kuassivi.october.di.component;

import com.kuassivi.october.di.module.BaseApplicationModule;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component whose lifetime is the life of the Application.
 */
@Singleton // Constraints this component to one-per-application or non-scoped bindings.
@Component(modules = BaseApplicationModule.class)
public interface BaseApplicationComponent {

    /**
     * Exposes the Application to sub-graphs.
     *
     * @return the current {@link Application}
     */
    Application application();

    /**
     * Exposes the Context to sub-graphs.
     *
     * @return the current {@link Context}
     */
    Context context();
}
