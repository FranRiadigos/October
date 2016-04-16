/*******************************************************************************
 * Copyright (c) 2016 Francisco Gonzalez-Armijo Riádigos
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package com.kuassivi.october.repository.datasource;

import com.kuassivi.annotation.RepositoryProxyCache;

import rx.Observable;

/**
 * DataSource factory which returns a Service to load data from the Local store or from the Cloud.
 *
 * @param <T> Provided as a parameter type, it is a contract interface that specifies all operations
 *            of your DataSource.
 *            <p>
 *            When you call {@link DataSourceFactory#createService(RepositoryProxyCache)} it should
 *            return an object that complies with this &lt;T&gt; parameter.
 */
public interface DataSourceFactory<T> {

    /**
     * Attaches the current builder to the DataSource Factory.
     */
    DataSourceFactory<T> attach(Builder<T> builder);

    /**
     * Creates a Data Strategy Service which should return a Local or Cloud DataSource depending on
     * the Cache.
     *
     * @param cache The current cache
     * @return the DataSource Service type provided
     */
    T createService(RepositoryProxyCache cache);

    /**
     * Creates a Data Strategy Service which should return always a Cloud DataSource.
     */
    T createCloudService();

    /**
     * Creates a Data Strategy Service which should return always a Local DataSource.
     */
    T createLocalService();

    /**
     * Builder of the Data Service Factory
     *
     * @param <T> Type of the Repository
     */
    interface Builder<T> {

        /**
         * Attaches the current DataSource Strategy to the repository builder.
         */
        void attach(T dataSourceStrategy);

        /**
         * Retrieves the current DataSource Factory.
         */
        DataSourceFactory<T> getDataSourceFactory();

        /**
         * Builds an Observable using the delegate method of an inner DataSource Strategy.
         * <p>
         * Allow to compose and apply specific routines on the original observable.
         * <p>
         * This is useful only if you plan to have your custom {@link DataSourceStrategy} class, and
         * operates some routines on the original observable provided.
         */
        <E> Observable<E> build(Observable<E> observable);
    }
}
