/*******************************************************************************
 * Copyright (c) 2016 Francisco Gonzalez-Armijo Riádigos
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.kuassivi.october.repository.datasource;

import com.kuassivi.annotation.RepositoryProxyCache;

/**
 * Creates a DataSource Factory that can manage a Cache.
 */
public class DataSourceFactoryImpl<T extends DataSourceStrategy> implements DataSourceFactory<T> {

    private ICloudDataSource cloudDataSource;
    private ILocalDataSource localDataSource;

    private DataSourceFactory.Builder<T> builder;

    /**
     * You must pass each DataSource implementation instances.
     * <p>
     * Sometimes you may not need to pass a specific DataSource, because you don´t need it, that is
     * fine.
     *
     * @param cloudDataSource Cloud DataSource instance
     * @param localDataSource Local DataSource instance
     * @param <C>             ICloudDataSource
     * @param <L>             ILocalDataSource
     */
    public <C extends ICloudDataSource, L extends ILocalDataSource>
    DataSourceFactoryImpl(C cloudDataSource, L localDataSource) {
        this.cloudDataSource = cloudDataSource;
        this.localDataSource = localDataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSourceFactory<T> attach(Builder<T> builder) {
        this.builder = builder;
        return this;
    }

    /**
     * Returns the right Data Source wrapped over a Service Strategy.
     * Checks whether the cache exists and isExpired.
     *
     * @param cache the current cache
     * @return the current Data Source Strategy
     */
    @Override
    final public T createService(RepositoryProxyCache cache) {
        T dataSourceStrategy;

        checkNonNullDataSources();

        if (!cache.isExpired()) {
            //noinspection unchecked
            dataSourceStrategy = (T) this.localDataSource;
        } else {
            //noinspection unchecked
            dataSourceStrategy = (T) this.cloudDataSource;
            dataSourceStrategy.attachCache(cache);
        }

        builder.attach(dataSourceStrategy);

        return dataSourceStrategy;
    }

    /**
     * Returns only the Cloud DataSource
     *
     * @return the current Data Source Strategy
     */
    @Override
    final public T createCloudService() {
        checkNonNullCloudDataSources();
        //noinspection unchecked
        builder.attach((T) this.cloudDataSource);
        //noinspection unchecked
        return (T) this.cloudDataSource;
    }

    /**
     * Returns only the Local DataSource.
     *
     * @return the current Data Source Strategy
     */
    @Override
    final public T createLocalService() {
        checkNonNullLocalDataSources();
        //noinspection unchecked
        builder.attach((T) this.localDataSource);
        //noinspection unchecked
        return (T) this.localDataSource;
    }

    /**
     * Checks for Data Sources to be set
     */
    private void checkNonNullDataSources() {
        checkNonNullCloudDataSources();
        checkNonNullLocalDataSources();
    }

    /**
     * Checks for a Cloud Data Source to be set
     */
    private void checkNonNullCloudDataSources() {
        if (this.cloudDataSource == null) {
            throw new NullPointerException("Null Cloud DataSource in DataSourceFactoryImpl");
        }
    }

    /**
     * Checks for a Local Data Source to be set
     */
    private void checkNonNullLocalDataSources() {
        if (this.localDataSource == null) {
            throw new NullPointerException("Null Local DataSource in DataSourceFactoryImpl");
        }
    }
}
