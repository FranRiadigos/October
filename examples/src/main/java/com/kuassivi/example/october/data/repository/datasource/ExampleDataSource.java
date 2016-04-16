package com.kuassivi.example.october.data.repository.datasource;

import rx.Observable;

public interface ExampleDataSource {

    Observable<Void> getData();
}
