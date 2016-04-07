package com.kuassivi.example;

import com.kuassivi.example.di.module.ActivityModule;
import com.kuassivi.example.di.module.ApplicationModule;
import com.kuassivi.example.di.module.FragmentModule;
import com.kuassivi.october.October;
import com.kuassivi.october.annotation.ApplicationComponent;

import android.app.Application;

@ApplicationComponent(application = ApplicationModule.class,
                      fragment = FragmentModule.class,
                      activity = ActivityModule.class)
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        October.initialize(this);
    }
}
