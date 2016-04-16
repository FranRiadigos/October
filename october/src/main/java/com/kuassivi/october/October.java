package com.kuassivi.october;

import com.kuassivi.october.annotation.ApplicationComponent;

import android.app.Application;

/**
 * Main class who initialize the October Framework.
 * Remember to create before all dependency module files as specified in the web manuals.
 * <p>
 * <b>Usage:</b> Add the @{@link ApplicationComponent} annotation on your {@link Application}
 * class and initialize the October Framework as follow:
 * <pre>
 * <code>public void onCreate() {
 *   super.onCreate();
 *   October.initialize(this);
 * }
 * </code>
 * </pre>
 */
public class October {

    private static OctoberComponent component;

    public static void initialize(Application application) {
        //noinspection TryWithIdenticalCatches
        try {
            Class<?> clazz = Class.forName(Config.PACKAGE + "." + Config.OCTOBER_DI_NAME);
            OctoberComponentInitializer initializer =
                    (OctoberComponentInitializer) clazz.getConstructor().newInstance();
            //noinspection unchecked
            component = initializer.initialize(application);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Something unexpected was happened. "
                                       + "Please, rebuild your project and try again.");
        } catch (Exception ignored) {
        }
    }

    public static OctoberComponent getComponent() {
        return component;
    }
}
