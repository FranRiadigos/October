package com.kuassivi.october.repository.datasource;

import com.kuassivi.october.service.adapter.OctoberRetrofitFactory;

import io.realm.Realm;

/**
 * {@link Realm} specific DataSource decorator for Cloud operations.
 * <p>
 * This class depends by default on a {@link OctoberRetrofitFactory}.
 *
 * @param <F> Retrofit Factory Implementation
 * @see OctoberRetrofitFactory
 */
public abstract class RealmCloudDataSource<F extends OctoberRetrofitFactory>
        extends RealmDataSourceStrategy implements ICloudDataSource {

    private F retrofitFactory;

    public RealmCloudDataSource(F retrofitFactory) {
        this.retrofitFactory = retrofitFactory;
    }

    public F getRetrofitFactory() {
        //noinspection unchecked
        return retrofitFactory;
    }
}
