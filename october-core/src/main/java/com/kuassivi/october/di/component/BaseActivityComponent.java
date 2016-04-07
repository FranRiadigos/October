package com.kuassivi.october.di.component;

import com.kuassivi.october.annotation.PerActivity;
import com.kuassivi.october.di.OctoberPresenterActivityInjectable;
import com.kuassivi.october.di.component.internal.BaseHelperActivityComponent;
import com.kuassivi.october.di.component.internal.BaseHelperFragmentComponent;
import com.kuassivi.october.di.module.BaseActivityModule;

import android.app.Activity;

import dagger.Subcomponent;

/**
 * A component whose lifetime is the life of the Activity.
 *
 * @param <H>   the {@link BaseHelperActivityComponent}
 * @param <PAI> the {@link OctoberPresenterActivityInjectable}
 */
@PerActivity // Constraints this component to one-per-activity bindings.
@Subcomponent(modules = BaseActivityModule.class)
public interface BaseActivityComponent<H, PAI extends OctoberPresenterActivityInjectable> {

    /**
     * Provides a Helper for the FragmentComponent.
     *
     * @return {@link BaseHelperFragmentComponent}
     */
    H helperFragmentComponent();

    /**
     * Provides the {@link OctoberPresenterActivityInjectable}
     *
     * @param injector the {@link OctoberPresenterActivityInjectable} injector class
     */
    void inject(PAI injector);

    /**
     * Exposes the Activity to sub-graphs.
     *
     * @return the current {@link Activity}
     */
    Activity activity();
}
