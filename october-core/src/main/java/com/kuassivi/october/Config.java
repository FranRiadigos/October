package com.kuassivi.october;

import com.kuassivi.october.util.StringUtils;

public interface Config {

    String PACKAGE = Config.class.getPackage().getName();

    String MVP_CLASS_PATTERN = ".*(\\.mvp.*)";

    String OCTOBER_DI_PREFIX = "Dagger";
    String OCTOBER_DI_NAME   = "October" + OCTOBER_DI_PREFIX;

    String OCTOBER_ACTIVITY_CLASS              = "com.kuassivi.october.mvp.OctoberActivity";
    String OCTOBER_ACTIVITY_CLASS_SIMPLE_NAME  = OCTOBER_ACTIVITY_CLASS
            .replaceAll(MVP_CLASS_PATTERN, "$1");
    String OCTOBER_APPCOMPAT_CLASS             = "com.kuassivi.october.mvp.OctoberCompatActivity";
    String OCTOBER_APPCOMPAT_CLASS_SIMPLE_NAME = OCTOBER_APPCOMPAT_CLASS
            .replaceAll(MVP_CLASS_PATTERN, "$1");
    String OCTOBER_FRAGMENT_CLASS              = "com.kuassivi.october.mvp.OctoberFragment";
    String OCTOBER_FRAGMENT_CLASS_SIMPLE_NAME  = OCTOBER_FRAGMENT_CLASS
            .replaceAll(MVP_CLASS_PATTERN, "$1");
    String OCTOBER_PRESENTER_CLASS             = "com.kuassivi.october.mvp.OctoberPresenter";
    String OCTOBER_PRESENTER_CLASS_SIMPLE_NAME = OCTOBER_PRESENTER_CLASS
            .replaceAll(MVP_CLASS_PATTERN, "$1");

    String ANDROID_APPLICATION_QUALIFIED_NAME = "android.app.Application";

    String COMPONENT_INJECTOR_METHOD      = "inject";
    String APPLICATION_COMPONENT          = "OctoberApplicationComponent";
    String ACTIVITY_COMPONENT             = "OctoberActivityComponent";
    String FRAGMENT_COMPONENT             = "OctoberFragmentComponent";
    String ACTIVITY_COMPONENT_SIMPLE_NAME = "ActivityComponent";
    String FRAGMENT_COMPONENT_SIMPLE_NAME = "FragmentComponent";

    String HELPER_COMPONENT_METHOD          = "apply";
    String HELPER_ACTIVITY_COMPONENT        = "HelperActivityComponent";
    String HELPER_ACTIVITY_COMPONENT_METHOD = StringUtils.uncapitalize(HELPER_ACTIVITY_COMPONENT);
    String HELPER_FRAGMENT_COMPONENT        = "HelperFragmentComponent";
    String HELPER_FRAGMENT_COMPONENT_METHOD = StringUtils.uncapitalize(HELPER_FRAGMENT_COMPONENT);

    String PRESENTER_INJECTOR_METHOD   = "provide";
    String PRESENTER_ACTIVITY_INJECTOR = "PresenterActivityInjector";
    String PRESENTER_FRAGMENT_INJECTOR = "PresenterFragmentInjector";

    String DAGGER_APPLICATION_COMPONENT_METHOD = "initialize";
}
