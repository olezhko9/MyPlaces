package com.example.olegnaumov.myplaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.example.olegnaumov.myplaces.model.MyPlace;
import com.example.olegnaumov.myplaces.presenter.BasePresenter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class MapPlacesPresenter extends BasePresenter<MapPlacesContract.View> implements MapPlacesContract.Presenter {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 9999;
    private Context context;
    private MapPlacesContract.Model jsonModel;

    public MapPlacesPresenter(Context context) {
        this.context = context;
        jsonModel = new MapPlacesJsonModel(context);

//        MyPlace place = new MyPlace("Случайное место", "Место добавлено из кода",
//                60, 30);
//
//        MyPlace place1 = new MyPlace("Случайное место из кода", "Место добавлено из кода",
//                59.98,30.312);
//
//        jsonModel.addPlace(place);
//        jsonModel.addPlace(place1);
    }

    @Override
    public void attachView(MapPlacesContract.View mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void enableMyLocation() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION};

            ActivityCompat.requestPermissions(getView().getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        } else {
            getView().enableMyLocationButton();
            this.getDeviceLocation();
        }
    }

    @Override
    public void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.
                getFusedLocationProviderClient(context);

        try {
            mFusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        getView().animateCamera(currentLocation);
                    }
                }
            });
        } catch (SecurityException e) {
            getView().makeToast("Разрешите доступ к местоположению!");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        }
    }

    @Override
    public void onMapReady() {
        List<MyPlace> places = jsonModel.getAllPlaces();

        for (MyPlace place : places) {
            LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
            getView().addMapMarker(place.getTitle(), place.getDescription(), location);
        }
    }

    @Override
    public void askInfoAboutPlace(Marker marker) {
        Bundle markerLocationBundle = new Bundle();
        markerLocationBundle.putDouble("markerLat", marker.getPosition().latitude);
        markerLocationBundle.putDouble("markerLng", marker.getPosition().longitude);

        getView().showPlaceSavingDialog(markerLocationBundle);
    }
}
