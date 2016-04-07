package com.kuassivi.example.data.repository.datasource;

import rx.Observable;

/**
 * Created by fran on 7/4/16.
 */
public interface ExampleDataSource {

    Observable<Void> getData();
}
