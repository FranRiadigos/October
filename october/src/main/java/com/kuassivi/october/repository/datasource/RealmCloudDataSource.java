package com.kuassivi.october.repository.datasource;

import com.kuassivi.october.service.adapter.OctoberRetrofitFactory;

/**
 * Realm specific Data Source Strategy for Cloud operations.
 * <p>
 * This class depends by default on a Retrofit Factory.
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
