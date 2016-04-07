package com.kuassivi.october.di.component.internal;

import com.kuassivi.october.di.module.BaseFragmentModule;

/**
 * Helper component to allow injection on Fragments.
 * @param <C> the OctoberFragmentComponent generated.
 */
public interface BaseHelperFragmentComponent<C> {

    /**
     * Apply and FragmentModule to this helper.
     *
     * @param module an FragmentModule instance that inherits from {@link BaseFragmentModule}.
     * @return the OctoberFragmentComponent generated.
     */
    C apply(BaseFragmentModule module);
}
