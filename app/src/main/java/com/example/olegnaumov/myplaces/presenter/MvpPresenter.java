package com.example.olegnaumov.myplaces.presenter;

import com.example.olegnaumov.myplaces.view.MvpView;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();

}
