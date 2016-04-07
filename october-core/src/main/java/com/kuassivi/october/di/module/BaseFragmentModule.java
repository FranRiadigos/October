package com.kuassivi.october.di.module;

import com.kuassivi.october.annotation.PerFragment;

import android.support.v4.app.Fragment;

import dagger.Module;
import dagger.Provides;

/**
 * A module to wrap the Fragment states and expose it to the graph.
 */
@Module
public class BaseFragmentModule {

    /**
     * The current {@link Fragment}
     */
    protected final Fragment fragment;

    /**
     * Once initialized it provides the current {@link Fragment} to the constructor.
     *
     * @param fragment the current {@link Fragment}
     */
    public BaseFragmentModule(Fragment fragment) {
        this.fragment = fragment;
    }

    /**
     * Exposes the Fragment to dependents in the graph.
     *
     * @return the current {@link Fragment}
     */
    @Provides
    @PerFragment
    protected Fragment provideFragment() {
        return fragment;
    }
}
