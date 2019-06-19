package com.example.olegnaumov.myplaces.presenter;

import com.example.olegnaumov.myplaces.view.MvpView;

public interface MvpPresenter {

    void attachView(MvpView mvpView);

    void detachView();

}
