package com.kuassivi.example.data.repository;

import com.kuassivi.example.data.repository.datasource.ExampleDataSource;
import com.kuassivi.october.annotation.PerActivity;
import com.kuassivi.october.repository.OctoberRepository;
import com.kuassivi.october.repository.datasource.DataSourceFactory;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;

@PerActivity
public class ExampleRepositoryImpl extends OctoberRepository<ExampleDataSource>
        implements ExampleRepository {

    @Inject
    public ExampleRepositoryImpl(
            @Named("ExampleDataSource")
            DataSourceFactory dataSourceFactory) {
        super(dataSourceFactory);
    }

    @Override
    public Observable<Void> getData() {
        return this.getDataSourceFactory().createCloudService().getData();
    }
}
