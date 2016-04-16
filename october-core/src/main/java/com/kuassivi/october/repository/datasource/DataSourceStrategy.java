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

import android.support.annotation.Nullable;

import rx.Observable;

/**
 * Default DataSource Strategy Contract.
 */
public abstract class DataSourceStrategy {

    /**
     * The current Cache instance attached from your Repository implementation.
     */
    private RepositoryProxyCache cache;

    /**
     * Attaches the current proxy cache.
     *
     * @param cache The proxy cache
     */
    final void attachCache(RepositoryProxyCache cache) {
        this.cache = cache;
    }

    /**
     * Delegates the {@link Observable} to perform specific strategies.
     */
    final public <T> Observable<T> delegate(Observable<T> observable) {
        return compose(observable);
    }

    /**
     * Executes a process before to perform the {@link Observable}.
     */
    <T> Observable<T> compose(Observable<T> observable) {
        // no-op by default
        return observable;
    }

    /**
     * Helper method to persist the current cache as is.
     */
    final public <T> T persistCache(T entity) {
        if (this.cache != null && !this.cache.isCached()) {
            this.cache.persist();
        }
        return entity;
    }

    /**
     * Returns the current attached Cache if exists, or null otherwise.
     */
    @Nullable
    final public RepositoryProxyCache getCache() {
        return cache;
    }
}
