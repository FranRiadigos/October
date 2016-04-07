package com.kuassivi.october.repository;

import com.kuassivi.october.repository.datasource.DataSourceFactory;
import com.kuassivi.october.repository.datasource.DataSourceStrategy;

import rx.Observable;

/**
 * This class is intended to provide the DataSource Factory associated with this repository.
 * <p>
 * It also builds or composes conveniently the original observable to apply specific routines on
 * it.
 * <p>
 * By default, you don't need to wrap your final observable you return with the build method.
 *
 * @param <T> It is a contract interface that specifies all operations of your DataSource.
 *            <p>
 *            You get this type when you call <i>getDataSourceFactory().createService(Cache)</i>
 * @see DataSourceFactory
 * @see DataSourceFactory.Builder#build(Observable)
 */
public abstract class OctoberRepository<T> implements DataSourceFactory.Builder<T> {

    private DataSourceFactory dataSourceFactory;
    private T                 dataSourceStrategy;

    public OctoberRepository(DataSourceFactory dataSourceFactory) {
        //noinspection unchecked
        this.dataSourceFactory = dataSourceFactory.attach(this);
    }

    @Override
    public void attach(T dataSourceStrategy) {
        this.dataSourceStrategy = dataSourceStrategy;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public DataSourceFactory<T> getDataSourceFactory() {
        //noinspection unchecked
        return dataSourceFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public <E> Observable<E> build(Observable<E> observable) {
        return ((DataSourceStrategy) this.dataSourceStrategy).build(observable);
    }
}
