package com.kuassivi.october.annotation;

import java.lang.annotation.Retention;

import javax.inject.Scope;
import javax.inject.Singleton;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A scoping annotation to permit objects whose lifetime should
 * conform to the life of the Activity to be memorized in the
 * correct component.
 * <p>
 * Remember you cannot inject any @{@link PerActivity} component inside a @{@link Singleton}
 * component.
 * <p>
 * Remember you cannot inject any @{@link PerFragment} component inside a @{@link PerActivity} or @
 * {@link Singleton} component.
 */
@Scope
@Retention(RUNTIME)
public @interface PerActivity {

}
