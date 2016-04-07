package com.kuassivi.october.di.component.internal;

import com.kuassivi.october.di.module.BaseActivityModule;

/**
 * Helper component to allow injection on Activities and AppCompatActivities.
 * @param <C> the OctoberActivityComponent generated.
 */
public interface BaseHelperActivityComponent<C> {

    /**
     * Apply and ActivityModule to this helper.
     *
     * @param module an ActivityModule instance that inherits from {@link BaseActivityModule}.
     * @return the OctoberActivityComponent generated.
     */
    C apply(BaseActivityModule module);
}
