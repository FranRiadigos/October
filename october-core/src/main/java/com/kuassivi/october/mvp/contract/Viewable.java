/*******************************************************************************
 * Copyright (c) 2016 Francisco Gonzalez-Armijo Riádigos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.kuassivi.october.mvp.contract;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

/**
 * Android contract for every MVP View
 */
public interface Viewable {

    /**
     * Sets the Title of the Screen
     */
    void setTitle(@StringRes int resource);

    /**
     * Sets the Title of the Screen
     */
    void setTitle(@NonNull CharSequence msg);

    /**
     * Every Viewable must to be notified the Presenter is ready
     */
    void onPresenterReady();

    /**
     * Every Viewable must to check whether it is alive
     * <br>
     * @return boolean
     */
    boolean isAlive();

    /**
     * Every Viewable must implement one show message feature
     */
    void showMessage(@StringRes int resource);

    /**
     * Every Viewable must implement one show message feature
     */
    void showMessage(@NonNull String msg);

    /**
     * Every Viewable must implement one show error feature
     */
    void showError(@StringRes int resource);

    /**
     * Every Viewable must implement one show error feature
     */
    void showError(@NonNull String msg);

    /**
     * Every Viewable must implement one show warning feature
     */
    void showWarning(@StringRes int resource);

    /**
     * Every Viewable must implement one show warning feature
     */
    void showWarning(@NonNull String msg);

    /**
     * Every Viewable must implement one show loading feature
     */
    void showLoading();

    /**
     * Every Viewable must implement one hide loading feature
     */
    void hideLoading();

}
