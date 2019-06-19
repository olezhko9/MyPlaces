package com.example.olegnaumov.myplaces.presenter;

import com.example.olegnaumov.myplaces.model.MvpModel;
import com.example.olegnaumov.myplaces.view.MvpView;

public class BasePresenter implements MvpPresenter {

    private MvpView view;
    private MvpModel model;

    public BasePresenter() {
    }

    @Override
    public void attachView(MvpView mvpView) {
        this.view = mvpView;
    }

    @Override
    public void detachView() {
        this.view = null;
    }
}
