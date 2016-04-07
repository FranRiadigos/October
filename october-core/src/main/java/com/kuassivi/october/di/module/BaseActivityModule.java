package com.kuassivi.october.di.module;

import com.kuassivi.october.annotation.PerActivity;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;

/**
 * A module to wrap the Activity states and expose it to the graph.
 */
@Module
public class BaseActivityModule {

    /**
     * The current {@link Activity}
     */
    protected final Activity activity;

    /**
     * Once initialized it provides the current {@link Activity} to the constructor.
     *
     * @param activity the current {@link Activity}
     */
    public BaseActivityModule(Activity activity) {
        this.activity = activity;
    }

    /**
     * Exposes the Activity to dependents in the graph.
     *
     * @return the current {@link Activity}
     */
    @Provides
    @PerActivity
    protected Activity provideActivity() {
        return activity;
    }
}
