package com.kuassivi.october.annotation;

import com.kuassivi.october.di.module.OctoberActivityModule;
import com.kuassivi.october.di.module.OctoberApplicationModule;
import com.kuassivi.october.di.module.OctoberFragmentModule;

import android.app.Application;

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
 * In order to initialize <b>October</b>, do it as follow inside your {@link Application#onCreate()} method:
 * <p>
 * <i>super.onCreate();</i><br><i>October.initialize(this);</i>
 * <p>
 * You must first create three classes that extends from {@link OctoberApplicationModule}, {@link
 * OctoberActivityModule} and {@link OctoberFragmentModule} respectively, where you should put
 * instance providers.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ApplicationComponent {

    Class<? extends OctoberApplicationModule> application();

    Class<? extends OctoberActivityModule> activity();

    Class<? extends OctoberFragmentModule> fragment();
}
