/*******************************************************************************
 * Copyright (c) 2016 Francisco Gonzalez-Armijo Riádigos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.kuassivi.october.repository.datasource;

import com.kuassivi.annotation.RepositoryProxyCache;

/**
 * Creates a DataSource Factory that can manage a Cache
 */
public class DataSourceFactoryImpl<T extends DataSourceStrategy> implements DataSourceFactory<T> {

    private T cloudDataSource;
    private T localDataSource;

    private DataSourceFactory.Builder<T> builder;

    /**
     * {@inheritDoc}
     */
    @Override
    public DataSourceFactory<T> attach(Builder<T> builder) {
        this.builder = builder;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void setCloudDataSource(T cloudDataSource) {
        this.cloudDataSource = cloudDataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void setLocalDataSource(T localDataSource) {
        this.localDataSource = localDataSource;
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
            dataSourceStrategy = this.localDataSource;
            dataSourceStrategy.setIsCloudDataSource(false);
        } else {
            dataSourceStrategy = this.cloudDataSource;
            dataSourceStrategy.setIsCloudDataSource(true);
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
        this.cloudDataSource.setIsCloudDataSource(true);
        builder.attach(this.cloudDataSource);
        return this.cloudDataSource;
    }

    /**
     * Returns only the Local DataSource.
     *
     * @return the current Data Source Strategy
     */
    @Override
    final public T createLocalService() {
        checkNonNullLocalDataSources();
        this.cloudDataSource.setIsCloudDataSource(false);
        builder.attach(this.localDataSource);
        return this.localDataSource;
    }

    /**
     * Checks for data sources to be set
     */
    private void checkNonNullDataSources() {
        checkNonNullCloudDataSources();
        checkNonNullLocalDataSources();
    }

    /**
     * Checks for Cloud Data source to be set
     */
    private void checkNonNullCloudDataSources() {
        if(this.cloudDataSource == null) {
            throw new NullPointerException("Null Cloud DataSource in DataSourceFactory");
        }
    }

    /**
     * Checks for Local Data source to be set
     */
    private void checkNonNullLocalDataSources() {
        if(this.localDataSource == null) {
            throw new NullPointerException("Null Local DataSource in DataSourceFactory");
        }
    }
}
