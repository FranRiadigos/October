package com.kuassivi.october.repository.datasource;

import com.kuassivi.october.service.adapter.OctoberRetrofitFactory;

/**
 * Cloud DataSource strategy contract.
 * <p>
 * This class depends by default on a Retrofit Factory.
 *
 * @param <F> Retrofit Factory Implementation
 * @see OctoberRetrofitFactory
 */
public abstract class CloudDataSource<F extends OctoberRetrofitFactory>
        extends DataSourceStrategy implements ICloudDataSource {

    private F retrofitFactory;

    public CloudDataSource(F retrofitFactory) {
        this.retrofitFactory = retrofitFactory;
    }

    public F getRetrofitFactory() {
        //noinspection unchecked
        return retrofitFactory;
    }
}
