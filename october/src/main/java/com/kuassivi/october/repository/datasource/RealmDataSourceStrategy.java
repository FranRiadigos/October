package com.kuassivi.october.repository.datasource;

import java.util.Collection;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.functions.Action0;

/**
 * Realm specific Data Source Strategy class that starts and closes a Realm instance in the same
 * Thread and observable is subscribed.
 * <p>
 * It also provides an utility method to create, update and/or clear specific Realm objects
 * synchronously through the {@link #createOrUpdate(Object, Class[])} method.
 */
abstract class RealmDataSourceStrategy extends DataSourceStrategy {

    /**
     * We cannot Inject Realm with dagger, due to the access thread confinement.
     * <p>
     * Let {@link #startRealm} to instantiate Realm in each subscription.
     */
    private Realm realm;

    /**
     * Starts the Realm instance when the Observable subscribes.
     */
    private final Action0 startRealm = () -> {
        if (this.realm == null || this.realm.isClosed()) {
            this.realm = Realm.getDefaultInstance();
        }
    };

    /**
     * Closes the Realm instance when the Observable terminates.
     */
    private final Action0 stopRealm = () -> {
        if (this.realm != null && !this.realm.isClosed()) {
            this.realm.close();
            this.realm = null;
        }
    };

    /**
     * Manages the Realm instance.
     * <p>
     * {@inheritDoc}
     */
    @Override
    <T> Observable<T> compose(Observable<T> observable) {
        return observable.compose(
                observable1 -> observable1
                        .doOnSubscribe(startRealm)
                        .doOnTerminate(stopRealm));
    }

    /**
     * Entity to be stored in a {@link Realm.Transaction} no matter of whether is an object or a
     * Collection.
     * <p>
     * If second parameter is passed, it will clear all data of the specified {@link RealmObject}
     * class.
     *
     * @param entity     {@link RealmObject} or {@link io.realm.RealmList}
     * @param clearFirst Optional {@link RealmObject} to be cleared
     * @param <T>        {@link RealmObject} or {@link io.realm.RealmList}
     * @param <B>        {@link RealmObject}
     */
    @SafeVarargs
    final public <T, B extends RealmObject> void createOrUpdate(T entity,
                                                                Class<B>... clearFirst) {
        if (entity != null) {
            this.realm.executeTransaction(
                    realmInstance -> {
                        if (clearFirst != null
                            && clearFirst.length > 0
                            && isAbleToClear(entity)) {
                            for (Class<B> clazz : clearFirst) {
                                realmInstance.clear(clazz);
                            }
                        }
                        if (entity instanceof RealmObject) {
                            realmInstance.copyToRealmOrUpdate((RealmObject) entity);
                        } else if (hasCollectionItems(entity)) {
                            realmInstance.copyToRealmOrUpdate((Collection) entity);
                        }
                    });
        }
    }

    /**
     * Checks whether is able to clear all entities on the Realm database.
     *
     * @param entity {@link RealmObject} or {@link io.realm.RealmList}
     * @param <T>    {@link RealmObject} or {@link io.realm.RealmList}
     * @return true if is able to clear all entities on the Realm database, false otherwise
     */
    private <T> boolean isAbleToClear(T entity) {
        return (entity instanceof RealmObject)
               || hasCollectionItems(entity);
    }

    /**
     * Checks whether is a Collection and if it's not empty
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
     * Returns the current Realm instance for the current Thread execution.
     *
     * @return The current Realm instance
     */
    final public Realm getRealm() {
        return realm;
    }
}
