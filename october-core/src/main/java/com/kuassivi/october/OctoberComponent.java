package com.kuassivi.october;

import com.kuassivi.october.di.OctoberPresenterActivityInjectable;
import com.kuassivi.october.di.OctoberPresenterFragmentInjectable;
import com.kuassivi.october.di.component.BaseActivityComponent;
import com.kuassivi.october.di.component.BaseApplicationComponent;
import com.kuassivi.october.di.component.BaseFragmentComponent;
import com.kuassivi.october.di.component.internal.BaseHelperActivityComponent;
import com.kuassivi.october.di.module.BaseActivityModule;
import com.kuassivi.october.di.module.BaseFragmentModule;
import com.kuassivi.october.mvp.OctoberActivityInterface;
import com.kuassivi.october.mvp.OctoberFragmentInterface;

/**
 * Component that decorates dependency injection bases.
 */
public interface OctoberComponent {

    BaseApplicationComponent getOctoberApplicationComponent();

    <T extends BaseActivityComponent> BaseHelperActivityComponent<T> getHelperActivityComponent();

    OctoberPresenterActivityInjectable getPresenterActivityInjector();

    OctoberPresenterFragmentInjectable getPresenterFragmentInjector();

    void apply(BaseActivityModule module);

    void apply(BaseFragmentModule module);

    <AC extends BaseActivityComponent> AC getActivityComponent();

    <FC extends BaseFragmentComponent> FC getFragmentComponent();

    void inject(OctoberActivityInterface activity);

    void inject(OctoberFragmentInterface fragment);
}
