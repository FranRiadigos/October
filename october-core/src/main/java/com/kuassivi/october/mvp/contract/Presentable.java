/*******************************************************************************
 * Copyright (c) 2016 Francisco Gonzalez-Armijo Riádigos
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations
 * under
 * the License.
 ******************************************************************************/

package com.kuassivi.october.mvp.contract;

import android.support.annotation.NonNull;

/**
 * Android contract for every MVP Presenter
 */
public interface Presentable<V extends Viewable> {

    /**
     * Every Presentable must implement onCreate state.
     * <p>
     * <b><font style="color:red">Caution:</font></b>
     * There is no View reference yet!
     */
    void onCreate();

    /**
     * Every Presentable must implement onViewCreated state.
     * <p>
     * <b><font style="color:green">Notice:</font></b>
     * View reference has been attached.
     */
    void onViewCreated();

    /**
     * Every Presentable must implement onResume state
     */
    void onResume();

    /**
     * Every Presentable must implement onPause state
     */
    void onPause();

    /**
     * Every Presentable must implement onDestroy state
     */
    void onDestroy();

    /**
     * Every Presentable must attach a Viewable
     *
     * @param viewable Viewable
     */
    void attachView(@NonNull Viewable viewable);

    /**
     * Every Presentable must detach its Viewable
     */
    void detachView();

    /**
     * Every Presentable must be able to access to its attached View
     *
     * @return V Viewable
     */
    V getView();

    /**
     * Presenter helper method. Override on your own Presenter.
     * <p>
     * Call Presenter#doProcess(Object) to perform some process outside of the current View.
     * <p>
     * For instance, you might need to perform a specific process from your Activity
     * or from a different Class without knowing which Presenter is.
     *
     * @param objects Objects as parameters
     */
    void doProcess(Object... objects);

    /**
     * Interface to notify one listener when the Presenter is attached or detached.
     * <p>
     * This is useful to notify the Presenter instance in your Activity.
     */
    interface OnAttachListener<P extends Presentable> {

        /**
         * Attach the current Presenter into the View
         *
         * @param presenter The current Presenter to be attached
         */
        void attachPresenter(@NonNull P presenter);

        /**
         * Detach the current Presenter from the View
         */
        void detachPresenter();

        /**
         * Retrieves the current Presenter on the View
         *
         * @return the current Presentable object
         */
        P getPresenter();
    }
}
