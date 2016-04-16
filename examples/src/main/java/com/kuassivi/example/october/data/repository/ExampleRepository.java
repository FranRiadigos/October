package com.kuassivi.example.october.data.repository;

import rx.Observable;

public interface ExampleRepository {

    Observable<Void> getData();
}
