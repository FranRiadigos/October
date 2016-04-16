package com.kuassivi.example.october.di.module;

import com.kuassivi.example.october.data.repository.ExampleRepository;
import com.kuassivi.example.october.data.repository.ExampleRepositoryImpl;
import com.kuassivi.example.october.data.repository.datasource.ExampleRealmCloudDataSource;
import com.kuassivi.october.annotation.PerActivity;
import com.kuassivi.october.di.module.OctoberActivityModule;
import com.kuassivi.october.repository.datasource.DataSourceFactory;
import com.kuassivi.october.repository.datasource.DataSourceFactoryImpl;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule implements OctoberActivityModule {
    // Provide Activity injections

    @Provides
    @PerActivity
    @Named("ExampleDataSource")
    DataSourceFactory provideExampleDataSource(ExampleRealmCloudDataSource cloudDataSource) {
        return new DataSourceFactoryImpl<>(cloudDataSource, null);
    }

    @Provides
    @PerActivity
    ExampleRepository provideExampleRepository(ExampleRepositoryImpl repository) {
        return repository;
    }
}
