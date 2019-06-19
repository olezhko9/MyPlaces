package com.example.olegnaumov.myplaces;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.example.olegnaumov.myplaces.presenter.MvpPresenter;
import com.example.olegnaumov.myplaces.view.MvpView;
import com.google.android.gms.maps.model.Marker;

public interface MapPlacesContract {

    interface View extends MvpView {

        Activity getActivity();

        void showPlaceSavingDialog(Bundle markerLocationBundle);

        void enableMyLocationButton();

        void animateCamera(Location location);
    }

    interface Presenter extends MvpPresenter<View> {

        void askInfoAboutPlace(Marker marker);

        void enableMyLocation();

        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

        void getDeviceLocation();
    }
}
