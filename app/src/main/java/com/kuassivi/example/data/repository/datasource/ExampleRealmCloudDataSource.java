package com.kuassivi.example.data.repository.datasource;

import com.kuassivi.example.data.services.adapter.RetrofitFactory;
import com.kuassivi.october.annotation.PerActivity;
import com.kuassivi.october.repository.datasource.RealmCloudDataSource;

import javax.inject.Inject;

import rx.Observable;

@PerActivity
public class ExampleRealmCloudDataSource extends RealmCloudDataSource<RetrofitFactory>
        implements ExampleDataSource {

    @Inject
    public ExampleRealmCloudDataSource(RetrofitFactory retrofitFactory) {
        super(retrofitFactory);
    }

    @Override
    public Observable<Void> getData() {
        return null;
    }
}
