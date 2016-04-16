package com.kuassivi.example.october.di.module;

import com.kuassivi.october.di.module.OctoberApplicationModule;
import com.kuassivi.october.executor.ObserverThread;
import com.kuassivi.october.executor.SubscriberThread;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
public class ApplicationModule implements OctoberApplicationModule {
    // Provide Singleton injections

    @Provides
    @Singleton
    SubscriberThread provideSubscriberThread() {
        return new SubscriberThread() {
            @Override
            public Scheduler getScheduler() {
                return Schedulers.io();
            }
        };
    }

    @Provides
    @Singleton
    ObserverThread provideObserverThread() {
        return new ObserverThread() {
            @Override
            public Scheduler getScheduler() {
                return AndroidSchedulers.mainThread();
            }
        };
    }
}
