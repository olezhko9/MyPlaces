package com.example.olegnaumov.myplaces;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.example.olegnaumov.myplaces.presenter.BasePresenter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapPlacesPresenter extends BasePresenter<MapPlacesContract.View> implements MapPlacesContract.Presenter {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 9999;
    private Context context;

    @Override
    public void attachView(MapPlacesContract.View mvpView) {
        super.attachView(mvpView);
        context = mvpView.getActivity().getApplicationContext();
    }

    public void enableMyLocation() {
        context = getView().getActivity().getApplicationContext();

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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation();
        }
    }

    public void askInfoAboutPlace(Marker marker) {
        Bundle markerLocationBundle = new Bundle();
        markerLocationBundle.putDouble("markerLat", marker.getPosition().latitude);
        markerLocationBundle.putDouble("markerLng", marker.getPosition().longitude);

        getView().showPlaceSavingDialog(markerLocationBundle);
    }
}
