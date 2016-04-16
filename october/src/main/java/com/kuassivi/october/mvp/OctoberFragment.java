package com.kuassivi.october.mvp;

import com.kuassivi.october.October;
import com.kuassivi.october.OctoberComponent;
import com.kuassivi.october.annotation.FragmentComponent;
import com.kuassivi.october.di.OctoberPresenterFragmentInjectable;
import com.kuassivi.october.di.module.BaseFragmentModule;
import com.kuassivi.october.mvp.contract.Presentable;
import com.kuassivi.october.mvp.contract.Viewable;
import com.kuassivi.october.util.MethodLooper;
import com.trello.rxlifecycle.components.support.RxFragment;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import butterknife.ButterKnife;
import dagger.Lazy;

/**
 * Base class for all Fragments that implement a MVP pattern.
 * <p>
 * {@inheritDoc}
 *
 * @param <P> The Presenter interface reference
 */
public abstract class OctoberFragment<P extends Presentable> extends RxFragment
        implements Viewable, Presentable.OnAttachListener<P>, OctoberFragmentInterface {

    /**
     * Current Presenter.
     * <p>
     * This is the presenter that will be attached to this Fragment.
     */
    private P presenter;

    /**
     * Safe Presenter for no-op purposes.
     * <p>
     * This presenter is a safe null object that avoid NullPointerException issues when it has not
     * been provided.
     */
    private P presenterNoOp;

    /**
     * This flag indicates if the fragment is in a transition or animation.
     * <p>
     * This helps you to perform initial operations when a Fragment has finished its transition
     * state.
     */
    private boolean isFragmentInTransition = false;

    /**
     * If constructor is called, it creates a Fragment with empty {@link Bundle} arguments.
     *
     * @see #setArguments(Bundle)
     */
    public OctoberFragment() {
        if (getArguments() == null) {
            setArguments(new Bundle());
        }
    }

    /**
     * Initializes Dagger injections if present, attaches this current View and delegate the {@link
     * Presentable#onCreate()} method on the presenter.
     * <p>
     * <b>Caution:</b> The {@link Presentable#onCreate()} method of the Presenter does not have a
     * reference of the View yet.
     * <p>
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViewInjector();
        getPresenter().onCreate();
    }

    /**
     * This fires after the view layout is created through
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, then the Presenter attaches
     * this Viewable class, and fires the {@link Presentable#onViewCreated()} method.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        MethodLooper.warning(this, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getPresenter().attachView(this);
        getPresenter().onViewCreated();
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    public void onResume() {
        MethodLooper.warning(this, "onResume");
        super.onResume();
        getPresenter().onResume();
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    public void onPause() {
        getPresenter().onPause();
        super.onPause();
    }

    /**
     * Unbinds Butterknife views.
     * <p>
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    public void onDestroyView() {
        getPresenter().detachView();
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    public void onDestroy() {
        getPresenter().onDestroy();
        super.onDestroy();
    }

    /**
     * Initializes Dagger injections on the View.
     */
    private <PFI extends OctoberPresenterFragmentInjectable>
    void initializeViewInjector() {

        OctoberComponent component = October.getComponent();

        if (component != null) {

            component.apply(new BaseFragmentModule(this));
            component.inject(this);

            //noinspection unchecked
            PFI injector = (PFI) component.getPresenterFragmentInjector();

            if (injector == null) {

                throw new RuntimeException(
                        String.format("Presenter injector does not exist. "
                                      + "Have you annotated your Application class with @%s?",
                                      FragmentComponent.class.getSimpleName()));
            }

            //noinspection unchecked
            component.getFragmentComponent().inject(injector);

            Class<P> viewClass;

            try {

                Type[] types =
                        ((ParameterizedType) getClass().getGenericSuperclass())
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
                        String.format("Cannot find %s class as a Component. "
                                      + "Have you annotated your Application class with @%s?",
                                      viewClass.getSimpleName(),
                                      FragmentComponent.class.getSimpleName()));
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
        return !isRemoving() && !isDetached();
    }

    /**
     * Flag indicating the Fragment is in an animated transition.
     *
     * @return true if is in an animated transition, false otherwise
     */
    final public boolean isFragmentInTransition() {
        return this.isFragmentInTransition;
    }

    /**
     * Override to perform some operation when the Fragment Transition Starts.
     */
    public void onFragmentTransitionStart() {
        // no-op by default
    }

    /**
     * Override to perform some operation when the Fragment Transition Ends.
     */
    public void onFragmentTransitionEnd() {
        // no-op by default
    }

    /**
     * Override to perform some operation when the Fragment Transition Repeats.
     */
    public void onFragmentTransitionRepeat() {
        // no-op by default
    }

    /**
     * Observes Fragment Transition States.
     *
     * {@inheritDoc}
     */
    @CallSuper
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        //Check if the superclass already created the animation
        Animation anim = super.onCreateAnimation(transit, enter, nextAnim);

        //If not, and an animation is defined, load it now
        if (anim == null && nextAnim != 0) {
            anim = AnimationUtils.loadAnimation(getActivity(), nextAnim);
        }

        //If there is an animation for this fragment, add a listener.
        if (anim != null) {
            this.isFragmentInTransition = true;
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    OctoberFragment.this.isFragmentInTransition = true;
                    onFragmentTransitionStart();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    OctoberFragment.this.isFragmentInTransition = false;
                    onFragmentTransitionEnd();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    OctoberFragment.this.isFragmentInTransition = true;
                    onFragmentTransitionRepeat();
                }
            });
        }

        return anim;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(@StringRes int resource) {
        setTitle(getString(resource));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTitle(@NonNull CharSequence msg) {
        getActivity().setTitle(msg);
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
