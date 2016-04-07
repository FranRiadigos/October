package com.kuassivi.example.data.repository;

import rx.Observable;

/**
 * Created by fran on 7/4/16.
 */
public interface ExampleRepository {

    Observable<Void> getData();
}
