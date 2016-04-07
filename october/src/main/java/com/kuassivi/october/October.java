package com.kuassivi.october;

import com.kuassivi.october.annotation.ApplicationComponent;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;

public class October {

    private static OctoberComponent component;

    public static void initialize(Application application) {
        //noinspection ReflectionForUnavailableAnnotation
        if (!application.getClass().isAnnotationPresent(ApplicationComponent.class)) {
            throw new RuntimeException(String.format("%s class is not annotated with @%s",
                                                     application.getClass().getSimpleName(),
                                                     ApplicationComponent.class.getSimpleName()));
        }

        //noinspection TryWithIdenticalCatches
        try {
            Class<?> clazz = Class.forName(Config.OCTOBER_CLASS_NAME);
            OctoberComponentInitializer initializer =
                    (OctoberComponentInitializer) clazz.getConstructor().newInstance();
            component = initializer.initialize(application);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Something unexpected was happened. "
                                       + "Please, try again and rebuild your project.");
        } catch (InvocationTargetException ignored) {
        } catch (NoSuchMethodException ignored) {
        } catch (InstantiationException ignored) {
        } catch (IllegalAccessException ignored) {
        }
    }

    public static OctoberComponent getComponent() {
        return component;
    }
}
