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

package com.kuassivi.october.util;

import com.kuassivi.october.annotation.PerActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

import javax.inject.Inject;

/**
 * Utility used to navigate through the application for both Activities and Fragments.
 */
@PerActivity
public class Navigator {

    /**
     * The current loaded Activity.
     */
    private Activity activity;

    /**
     * The current loaded Fragment.
     */
    private Fragment fragment;

    /**
     * The current fragment animations provided.
     */
    private int[] animations = new int[0];

    /**
     * If loaded with Dagger, it injects automatically the current Activity in this Navigator
     * instance.
     *
     * @param activity The current loaded Activity.
     */
    @Inject
    public Navigator(Activity activity) {
        this.activity = activity;
    }

    /**
     * Starts a new Activity without params.
     *
     * @param clazz Activity class
     * @param <T>   Activity type
     */
    public <T extends Activity> void start(Class<T> clazz) {
        start(activity, clazz, -1, null);
    }

    /**
     * Starts a new Activity with flags params.
     *
     * @param clazz Activity class
     * @param flags Activity flags
     * @param <T>   Activity type
     */
    public <T extends Activity> void start(Class<T> clazz, int flags) {
        start(activity, clazz, flags, null);
    }

    /**
     * Starts a new Activity with {@link Bundle} params.
     *
     * @param clazz  Activity class
     * @param extras Activity Bundle
     * @param <T>    Activity type
     */
    public <T extends Activity> void start(Class<T> clazz, Bundle extras) {
        start(activity, clazz, -1, extras);
    }

    /**
     * Starts a new Activity with flags and {@link Bundle} params.
     *
     * @param clazz  Activity class
     * @param flags  Activity flags
     * @param extras Activity Bundle
     * @param <T>    Activity type
     */
    public <T extends Activity> void start(Class<T> clazz, int flags, Bundle extras) {
        start(activity, clazz, flags, extras);
    }

    /**
     * Starts a new Activity with flags and {@link Bundle} params.
     *
     * @param activity Activity context
     * @param clazz    Activity class
     * @param flags    Activity flags
     * @param extras   Activity Bundle
     * @param <T>      Activity type
     */
    public <T extends Activity> void start(Context activity, Class<T> clazz, int flags,
                                           Bundle extras) {
        if (activity != null) {
            Intent intent = new Intent(activity, clazz);
            if (flags > 0) {
                intent.addFlags(flags);
            }
            if (extras != null) {
                intent.putExtras(extras);
            }
            activity.startActivity(intent);
        }
    }

    /**
     * Sets the fragment transition animations.
     *
     * @param enter animation transition
     * @param exit  animation transition
     */
    public void setFragmentAnimations(@AnimRes int enter,
                                      @AnimRes int exit) {
        animations = new int[2];
        animations[0] = enter;
        animations[1] = exit;
    }

    /**
     * Sets the fragment transition animations.
     *
     * @param enter    animation transition
     * @param exit     animation transition
     * @param popEnter animation transition
     * @param popExit  animation transition
     */
    public void setFragmentAnimations(@AnimRes int enter,
                                      @AnimRes int exit,
                                      @AnimRes int popEnter,
                                      @AnimRes int popExit) {
        animations = new int[4];
        animations[0] = enter;
        animations[1] = exit;
        animations[2] = popEnter;
        animations[3] = popExit;
    }

    /**
     * Replaces a Fragment into the Fragment Container.
     *
     * @param resourceId id for the fragment container
     * @param fragment   Fragment to be loaded on the container
     * @param animate    whether it must to be animated
     */
    public void load(@IdRes int resourceId, @NonNull Fragment fragment, boolean animate) {
        FragmentTransaction fragmentTransaction = prepareFragment(resourceId,
                                                                  fragment,
                                                                  animate,
                                                                  null);
        if (fragmentTransaction != null) {
            fragmentTransaction.commit();
        }
    }

    /**
     * Replaces a Fragment into the Fragment Container.
     *
     * @param resourceId id for the fragment container
     * @param fragment   Fragment to be loaded on the container
     */
    public void load(@IdRes int resourceId, @NonNull Fragment fragment) {
        FragmentTransaction fragmentTransaction = prepareFragment(resourceId,
                                                                  fragment,
                                                                  false,
                                                                  null);
        if (fragmentTransaction != null) {
            fragmentTransaction.commit();
        }
    }

    /**
     * Prepares a Fragment to be committed.
     *
     * @param resourceId     id for the fragment container
     * @param fragment       Fragment to be loaded on the container
     * @param animate        whether it must to be animated
     * @param addToBackStack the backStack string
     */
    @Nullable
    private FragmentTransaction prepareFragment(@IdRes int resourceId,
                                                @NonNull Fragment fragment,
                                                boolean animate,
                                                String addToBackStack) {

        if (resourceId <= 0) {
            throw new RuntimeException("You must supply an ID for the Fragment Container");
        }

        if (!(activity instanceof FragmentActivity)) {
            throw new RuntimeException(
                    "Activity must extends from FragmentActivity or AppCompatActivity");
        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

        /*
         * The method isFinishing() prevents to load a Fragment
         * when the activity is going to be killed or destroyed.
         */
        if ((fragmentManager == null || fragmentActivity.isFinishing())) {
            return null;
        }

        @SuppressLint("CommitTransaction") FragmentTransaction ft = fragmentManager
                .beginTransaction();

        if (animate && animations.length > 0) {
            if (animations.length == 2) {
                ft.setCustomAnimations(animations[0], animations[1]);
            } else {
                ft.setCustomAnimations(animations[0], animations[1], animations[2], animations[3]);
            }
        }

        ft.addToBackStack(addToBackStack);

        this.fragment = fragment;

        return ft.replace(resourceId, fragment);
    }

    /**
     * Pops back to the specified Fragment class existing in the BackStack <br>It will detach and
     * destroy forwarded Fragments
     *
     * @param clazz The fragment class
     */
    public void popToFragment(@NonNull Class<?> clazz) {
        popToFragment(clazz, null, false);
    }

    /**
     * Pops back with some specific fragment arguments <br>
     *
     * @param clazz          The fragment class
     * @param args           Arguments to update into the Fragment
     * @param clearArguments if true clear the current Fragment arguments
     */
    public void popToFragment(@NonNull Class<?> clazz, Bundle args, boolean clearArguments) {
        if (!(activity instanceof FragmentActivity)) {
            throw new RuntimeException("Activity must extend from FragmentActivity");
        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

        /*
         * The method isFinishing() prevents to load a Fragment
         * when the activity is going to be killed or destroyed.
         */
        if (fragmentManager == null || fragmentActivity.isFinishing()) {
            return;
        }

        List<Fragment> fragments = fragmentManager.getFragments();
        int index;

        for (index = 0; index < fragments.size(); index++) {
            Fragment frag = fragments.get(index);
            if (frag.getClass().equals(clazz)) {
                if (args != null && args.size() > 0 && frag.getArguments() == null) {
                    throw new IllegalArgumentException(
                            "Your Viewable constructor must call super()");
                }
                if (frag.getArguments() != null && args != null) {
                    if (clearArguments) {
                        frag.getArguments().clear();
                    }
                    frag.getArguments().putAll(args);
                }
                this.fragment = frag;
                popToStack(index + 1);
                break;
            }
        }
    }

    /**
     * Pops back to a specific BackStack position <br>It will detach and destroy forwarded
     * Fragments
     *
     * @param index index in the back stack entry
     */
    public void popToStack(int index) {
        if (!(activity instanceof FragmentActivity)) {
            throw new RuntimeException("Activity must extend from FragmentActivity");
        }

        FragmentActivity fragmentActivity = (FragmentActivity) activity;
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

        /*
         * The method isFinishing() prevents to load a Fragment
         * when the activity is going to be killed or destroyed.
         */
        if (fragmentManager == null || fragmentActivity.isFinishing()) {
            return;
        }

        final int firstFragmentCount = 1;
        int backStackCount = fragmentManager.getBackStackEntryCount();

        if (backStackCount <= firstFragmentCount) {
            fragmentActivity.finish();
        } else {
            if (index > 0) {
                List<Fragment> fragments = fragmentManager.getFragments();
                this.fragment = fragments.get(index - 1);
                fragmentManager
                        .popBackStackImmediate(index, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    /**
     * Return the current attached Fragment to an Activity
     *
     * @return the current attached Fragment
     */
    public Fragment getFragment() {
        return fragment;
    }
}
