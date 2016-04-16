package com.kuassivi.october.repository.datasource;

import java.util.Collection;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action0;

/**
 * {@link Realm} specific DataSource strategy class that starts and closes a {@link Realm} instance
 * in the same {@link Thread} when an {@link Observable} is subscribed.
 * <p>
 * It also provides an utility method to create, update and/or clear specific {@link Realm} objects
 * <b>synchronously</b> through the {@link #createOrUpdate(Object, Class[])} method.
 * <p>
 * <b>Note:</b> Whenever you inherits from this class and planning to work with the current {@link
 * Realm} instance, you must {@link #delegate(Observable)} your upstream {@link Observable}.
 */
abstract class RealmDataSourceStrategy extends DataSourceStrategy {

    /**
     * We cannot Inject {@link Realm} with dagger, due to the access thread confinement.
     * <p>
     * Let {@link #startRealm} to instantiate {@link Realm} in each subscription.
     */
    private Realm realm;

    /**
     * Starts the {@link Realm} instance when the {@link Observable} subscribes.
     */
    private final Action0 startRealm = new Action0() {
        @Override
        public void call() {
            if (realm == null || realm.isClosed()) {
                realm = Realm.getDefaultInstance();
                realm.refresh(); // Fix
            }
        }
    };

    /**
     * Closes the {@link Realm} instance when the {@link Observable} terminates.
     */
    private final Action0 stopRealm = new Action0() {
        @Override
        public void call() {
            if (realm != null && !realm.isClosed()) {
                realm.close();
                realm = null;
            }
        }
    };

    /**
     * Manages the {@link Realm} instance.
     * <p>
     * {@inheritDoc}
     */
    @Override
    <T> Observable<T> compose(Observable<T> observable) {
        return observable.compose(new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable
                        .doOnSubscribe(startRealm)
                        .doOnTerminate(stopRealm);
            }
        });
    }

    /**
     * Entity to be stored in a {@link Realm.Transaction} no matter of whether is an object or a
     * Collection.
     * <p>
     * If second parameter is passed, it will clear all data of the specified {@link RealmObject}
     * class.
     *
     * @param entity     {@link RealmObject} or {@link io.realm.RealmList}
     * @param clearFirst Optional {@link RealmObject}s to be cleared
     * @param <T>        {@link RealmObject} or {@link io.realm.RealmList}
     * @param <B>        {@link RealmObject}
     */
    @SafeVarargs
    final public <T, B extends RealmObject> void createOrUpdate(T entity,
                                                                Class<B>... clearFirst) {
        if (entity != null) {
            this.realm.executeTransaction(
                    new Realm.Transaction() {
                        @Override
                        public void execute(Realm realmInstance) {
                            if (clearFirst != null
                                && clearFirst.length > 0
                                && RealmDataSourceStrategy.this.isAbleToClear(entity)) {
                                for (Class<B> clazz : clearFirst) {
                                    realmInstance.clear(clazz);
                                }
                            }
                            if (entity instanceof RealmObject) {
                                realmInstance.copyToRealmOrUpdate((RealmObject) entity);
                            } else if (RealmDataSourceStrategy.this.hasCollectionItems(entity)) {
                                realmInstance.copyToRealmOrUpdate((Collection) entity);
                            }
                        }
                    });
        }
    }

    /**
     * Checks whether is able to clear all entities on the {@link Realm} database.
     *
     * @param entity {@link RealmObject} or {@link io.realm.RealmList}
     * @param <T>    {@link RealmObject} or {@link io.realm.RealmList}
     * @return true if is able to clear all entities on the {@link Realm} database, false otherwise
     */
    private <T> boolean isAbleToClear(T entity) {
        return (entity instanceof RealmObject)
               || hasCollectionItems(entity);
    }

    /**
     * Checks whether is a Collection and if it's not empty.
     *
     * @param entity {@link RealmObject} or {@link io.realm.RealmList}
     * @param <T>    {@link RealmObject} or {@link io.realm.RealmList}
     * @return true if is a Collection and it is not empty, false otherwise
     */
    private <T> boolean hasCollectionItems(T entity) {
        return (entity instanceof Collection
                && !((Collection) entity).isEmpty());
    }

    /**
     * Returns the current {@link Realm} instance for the current Thread execution.
     *
     * @return The current {@link Realm} instance
     */
    final public Realm getRealm() {
        return realm;
    }
}
