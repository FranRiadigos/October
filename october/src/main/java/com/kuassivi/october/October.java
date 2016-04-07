package com.kuassivi.october;

import android.app.Application;

import java.lang.reflect.InvocationTargetException;

public class October {

    private static OctoberComponent component;

    public static void initialize(Application application) {
        //noinspection TryWithIdenticalCatches
        try {
            Class<?> clazz = Class.forName(Config.OCTOBER_CLASS_NAME);
            OctoberComponentInitializer initializer =
                    (OctoberComponentInitializer) clazz.getConstructor().newInstance();
            //noinspection unchecked
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
