package com.example.olegnaumov.myplaces.presenter;

import com.example.olegnaumov.myplaces.model.MvpModel;
import com.example.olegnaumov.myplaces.view.MvpView;

public class BasePresenter<V extends MvpView> implements MvpPresenter<V> {

    private V view;
    private MvpModel model;

    public BasePresenter() { }

    @Override
    public void attachView(V mvpView) {
        this.view = mvpView;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    public V getView() {
        return view;
    }
}
