package com.kuassivi.october.di.component;

import com.kuassivi.october.annotation.PerFragment;
import com.kuassivi.october.di.OctoberPresenterFragmentInjectable;
import com.kuassivi.october.di.module.BaseFragmentModule;

import android.support.v4.app.Fragment;

import dagger.Subcomponent;

/**
 * A component whose lifetime is the life of the Fragment.
 *
 * @param <PFI>    the {@link OctoberPresenterFragmentInjectable}
 */
@PerFragment // Constraints this component to one-per-fragment bindings.
@Subcomponent(modules = BaseFragmentModule.class)
public interface BaseFragmentComponent<PFI extends OctoberPresenterFragmentInjectable> {

    /**
     * Provides the {@link OctoberPresenterFragmentInjectable}
     *
     * @param injector the {@link OctoberPresenterFragmentInjectable} injector class
     */
    void inject(PFI injector);

    /**
     * Exposes the Fragment to sub-graphs.
     *
     * @return the current {@link Fragment}
     */
    Fragment fragment();
}
