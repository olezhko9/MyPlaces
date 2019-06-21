package com.example.olegnaumov.myplaces;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import com.example.olegnaumov.myplaces.model.MvpModel;
import com.example.olegnaumov.myplaces.model.MyPlace;
import com.example.olegnaumov.myplaces.presenter.MvpPresenter;
import com.example.olegnaumov.myplaces.view.MvpView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public interface MapPlacesContract {

    interface View extends MvpView {

        Activity getActivity();

        void showPlaceSavingDialog(Bundle markerLocationBundle);

        void showBottomSheetDialog(Marker marker);

        void closeBottomSheetDialog();

        void enableMyLocationButton();

        void animateCamera(Location location);

        void addMapMarker(String title, String snippet, LatLng latLng, int tag);

        void removeMapMarker(Marker marker);
    }

    interface Presenter extends MvpPresenter<View> {

        void onAddMarkerFabClicked(Marker marker);

        void enableMyLocation();

        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

        void getDeviceLocation();

        void onMapReady();

        void onSaveButtonClicked(String title, String description, double lat, double lng);

        void onDeletePlaceButtonClicked(Marker marker);
    }

    interface Model extends MvpModel {

        List<MyPlace> getAllPlaces();

        int addPlace(MyPlace place);

        void deletePlace(int placeNumber);

        MyPlace getPlace();
    }
}
