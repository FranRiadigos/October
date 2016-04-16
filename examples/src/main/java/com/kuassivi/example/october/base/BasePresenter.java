package com.kuassivi.example.october.base;

import com.kuassivi.october.interactor.UseCase;
import com.kuassivi.october.mvp.OctoberPresenter;
import com.kuassivi.october.mvp.contract.Viewable;

import rx.functions.Action0;

public class BasePresenter<V extends Viewable> extends OctoberPresenter<V> {

    protected void attachLoading(UseCase... useCases) {
        for (UseCase useCase: useCases) {
            useCase.onSubscribe(new Action0() {
                @Override
                public void call() {
                    getView().showLoading();
                }
            });
            useCase.onTerminate(new Action0() {
                @Override
                public void call() {
                    getView().hideLoading();
                }
            });
        }
    }
}
