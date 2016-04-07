package com.kuassivi.october.annotation;

import com.kuassivi.october.OctoberApplicationComponent;
import com.kuassivi.october.OctoberComponent;
import com.kuassivi.october.di.module.OctoberActivityModule;
import com.kuassivi.october.di.module.OctoberApplicationModule;
import com.kuassivi.october.di.module.OctoberFragmentModule;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is the most important annotation for the dependency injection automation of October.
 * <p>
 * You must annotate your Application class with this annotation, and provide the classes it
 * requests.
 * <p>
 * Your Application must also inherits from {@link OctoberApplicationComponent}, and must implement
 * the {@link OctoberApplicationComponent#getOctoberComponent()} method inside.
 * <p>
 * To return the {@link OctoberComponent}, once you re-build your project, initialize October
 * as follow:
 * <p>
 * <b>OctoberComponent octoberComponent = OctoberDaggerApplicationComponent.initialize(Application);</b>
 * <p>
 * You need to create three classes that extends from {@link OctoberApplicationModule}, {@link
 * OctoberActivityModule} and {@link OctoberFragmentModule} where you will put instance providers.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ApplicationComponent {

    Class<? extends OctoberApplicationModule> application();

    Class<? extends OctoberActivityModule> activity();

    Class<? extends OctoberFragmentModule> fragment();
}
