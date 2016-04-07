package com.kuassivi.october.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If you annotate Activities or AppCompatActivities classes with this annotation, October will
 * register these classes as Dagger Components, then you will be able to inject any object inside,
 * without the need to configure Dagger specific component on it.
 * <p>
 * Once annotated, you will not need to do things like as follow in each onCreate() methods:
 * <p>
 * <i>DaggerActivityComponent.builder().activityModule(new Module()).build();</i>
 * <p>
 * October will perform this automatically for you, and it will allows you also to inject other
 * scoped object on it.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface ActivityComponent {}
