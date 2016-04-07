package com.kuassivi.october.mvp;

import com.kuassivi.october.mvp.contract.Presentable;
import com.kuassivi.october.mvp.contract.Viewable;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Base class for all Presenters that has access to the View reference.
 * <p>
 * {@inheritDoc}
 *
 * @param <V> The Viewable interface reference
 */
public abstract class OctoberPresenter<V extends Viewable>
        implements Presentable<V>, OctoberPresenterInterface {

    /**
     * The current Viewable object.
     */
    private WeakReference<V> viewRef;

    /**
     * Deliver the attachment lifecycle to the listener.
     */
    private OnAttachListener<Presentable> listener;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {}

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewCreated() {}

    /**
     * {@inheritDoc}
     */
    @Override
    final public void attachView(@NonNull Viewable viewable) {
        //noinspection unchecked
        this.listener = (OnAttachListener<Presentable>) viewable;
        //noinspection unchecked
        this.viewRef = new WeakReference<V>((V) viewable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public void detachView() {
        if (this.viewRef != null) {
            this.viewRef.clear();
        }
        if (this.listener != null) {
            this.listener.detachPresenter();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @NonNull
    @Override
    final public V getView() {
        if (this.viewRef == null) {
            throw new NullPointerException("The View reference is null. "
                                           + "Have you called attachView()?");
        }

        if (this.viewRef.get() == null) {
            try {
                Type[] types = ((ParameterizedType) getClass().getGenericSuperclass())
                                .getActualTypeArguments();
                //noinspection unchecked
                Class<V> viewClass = (Class<V>) types[0];
                return NoOp.of(viewClass);
            } catch (Exception ignored) {
                throw new IllegalArgumentException(
                        String.format(
                                "Invalid or missing view parameter type in "
                                + "(%s extends %s<V>), <V> must be a Viewable Interface.",
                                this.getClass().getSimpleName(),
                                this.getClass().getSuperclass().getSimpleName()));
            }
        }

        return this.viewRef.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doProcess(Object... objects) {
        // no-op by default
    }
}

