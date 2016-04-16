package com.kuassivi.october.repository.datasource;

import io.realm.Realm;

/**
 * {@link Realm} specific DataSource decorator for Local operations.
 */
public class RealmLocalDataSource extends RealmDataSourceStrategy implements ILocalDataSource {}
