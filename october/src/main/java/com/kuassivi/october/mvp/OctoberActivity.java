package com.kuassivi.october.mvp;

import com.kuassivi.october.October;
import com.kuassivi.october.OctoberComponent;
import com.kuassivi.october.annotation.ActivityComponent;
import com.kuassivi.october.di.OctoberPresenterActivityInjectable;
import com.kuassivi.october.di.module.BaseActivityModule;
import com.kuassivi.october.mvp.contract.Presentable;
import com.kuassivi.october.mvp.contract.Viewable;
import com.kuassivi.october.util.MethodLooper;
import com.trello.rxlifecycle.components.RxActivity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import dagger.Lazy;

/**
 * Base class for all Activities that implement a MVP pattern.
 * <p>
 * {@inheritDoc}
 *
 * @param <P> The Presenter interface reference
 */
public abstract class OctoberActivity<P extends Presentable> extends RxActivity
        implements Viewable, Presentable.OnAttachListener<P>, OctoberActivityInterface {

    /**
     * Current Presenter.
     * <p>
     * This is the presenter that is currently attached to this Activity.
     */
    private P presenter;

    /**
     * Safe Presenter for no-op purposes.
     * <p>
     * This presenter is a safe null object that avoids NullPointerException issues when it has not
     * been attached.
     */
    private P presenterNoOp;

    /**
     * Initializes Dagger injections if present, attaches this current View and delegate the {@link
     * Presentable#onCreate()} method on the presenter.
     * <p>
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewInjector();
        getPresenter().attachView(this);
        getPresenter().onCreate();
    }

    /**
     * Sets one layout resource to the Activity and starts some binding configurations.
     * <p>
     * {@inheritDoc}
     */
    final public void setContentView(@LayoutRes int layoutResID) {
        MethodLooper.warning(this, "setContentView");
        super.setContentView(layoutResID);
        onViewCreated();
    }

    /**
     * Sets one View component to the Activity and starts some binding configurations.
     * <p>
     * {@inheritDoc}
     */
    final public void setContentView(View view) {
        MethodLooper.warning(this, "setContentView");
        super.setContentView(view);
        onViewCreated();
    }

    /**
     * Sets one View component to the Activity and starts some binding configurations.
     * <p>
     * It also allows to pass some Layout Params for the parent Layout of the View component.
     * <p>
     * {@inheritDoc}
     */
    final public void setContentView(View view, ViewGroup.LayoutParams params) {
        MethodLooper.warning(this, "setContentView");
        super.setContentView(view, params);
        onViewCreated();
    }

    /**
     * Binds Butterknife views and delegate onViewCreated method on the Presenter.
     */
    private void onViewCreated() {
        ButterKnife.bind(this);
        getPresenter().onViewCreated();
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onResume() {
        MethodLooper.warning(this, "onResume");
        super.onResume();
        getPresenter().onResume();
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onPause() {
        getPresenter().onPause();
        super.onPause();
    }

    /**
     * Performs some memory optimizations.
     * <p>
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    protected void onDestroy() {
        View root = getWindow().getDecorView();
        if (root != null) {
            unbindDrawables(root);
        }
        ButterKnife.unbind(this);
        getPresenter().detachView();
        getPresenter().onDestroy();
        super.onDestroy();
        System.gc();
    }

    /**
     * Avoids consuming memory on background drawables.
     *
     * @param view The current content layout View
     */
    private void unbindDrawables(@NonNull View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            if (!(view instanceof AdapterView)) {
                try {
                    ((ViewGroup) view).removeAllViews();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Initializes Dagger injections on the View
     */
    private <PAI extends OctoberPresenterActivityInjectable>
    void initializeViewInjector() {

        OctoberComponent component = October.getComponent();

        if (component != null) {

            component.apply(new BaseActivityModule(this));
            component.inject(this);

            //noinspection unchecked
            PAI injector = (PAI) component.getPresenterActivityInjector();

            if (injector == null) {

                throw new RuntimeException(
                        String.format("Presenter injector does not exist. "
                                      + "Have you annotated your Application class with @%s?",
                                      ActivityComponent.class.getSimpleName()));
            }

            //noinspection unchecked
            component.getActivityComponent().inject(injector);

            Class<P> viewClass;

            try {

                Type[] types = ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments();

                //noinspection unchecked
                viewClass = (Class<P>) types[0];
            } catch (Exception ignored) {

                throw new IllegalArgumentException(
                        String.format(
                                "Invalid or missing presenter parameter type in (%s extends %s<P>),"
                                + " <P> must be a Presentable Interface.",
                                this.getClass().getSimpleName(),
                                this.getClass().getSuperclass().getSimpleName()));
            }

            Lazy<P> lazyPresenter = injector.get(viewClass);

            if (lazyPresenter == null) {

                throw new RuntimeException(
                        String.format("Cannot find %s class as a component. "
                                      + "Have you annotated your Application class with @%s?",
                                      viewClass.getSimpleName(),
                                      ActivityComponent.class.getSimpleName()));
            }

            this.presenter = lazyPresenter.get();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPresenterReady() {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public boolean isAlive() {
        boolean isAlive = !isFinishing();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            isAlive = isAlive && !isDestroyed();
        }
        return isAlive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showMessage(@StringRes int resource) {
        showMessage(getString(resource));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showMessage(@NonNull String msg) {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showError(@StringRes int resource) {
        showError(getString(resource));
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void showError(@NonNull String msg) {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWarning(@StringRes int resource) {
        showWarning(getString(resource));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showWarning(@NonNull String msg) {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showLoading() {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hideLoading() {
        // no-op by default
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attachPresenter(@NonNull P presenter) {
        this.presenter = presenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void detachPresenter() {
        this.presenter = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    final public P getPresenter() {
        if (this.presenter == null) {
            if (this.presenterNoOp == null) {
                try {
                    Type[] types =
                            ((ParameterizedType) getClass().getGenericSuperclass())
                                    .getActualTypeArguments();
                    //noinspection unchecked
                    Class<P> viewClass = (Class<P>) types[0];
                    this.presenterNoOp = NoOp.of(viewClass);
                } catch (Exception ignored) {
                    throw new IllegalArgumentException(
                            String.format(
                                    "Invalid or missing presenter parameter type in "
                                    + "(%s extends %s<P>), <P> must be a Presentable Interface.",
                                    this.getClass().getSimpleName(),
                                    this.getClass().getSuperclass().getSimpleName()));
                }
            }
            return this.presenterNoOp;
        }
        return this.presenter;
    }
}
