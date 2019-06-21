package com.example.olegnaumov.myplaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

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
    private PlaceAdapter adapter;

    public MapPlacesPresenter(Context context) {
        this.context = context;
        jsonModel = new MapPlacesJsonModel(context);
        adapter = new PlaceAdapter(
                this.context, R.layout.activity_maps, 0, jsonModel.getAllPlaces());
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

        for (int i = 0; i < places.size(); i++) {
            MyPlace place = places.get(i);
            LatLng location = new LatLng(place.getLatitude(), place.getLongitude());
            getView().addMapMarker(place.getTitle(), place.getDescription(), location, i);
        }
    }

    @Override
    public void onAddMarkerFabClicked(Marker marker) {
        Bundle markerLocationBundle = new Bundle();
        markerLocationBundle.putDouble("markerLat", marker.getPosition().latitude);
        markerLocationBundle.putDouble("markerLng", marker.getPosition().longitude);

        getView().showPlaceSavingDialog(markerLocationBundle);
    }

    @Override
    public void onSaveButtonClicked(String title, String description, double lat, double lng) {
        if (title.length() != 0 && description.length() != 0) {
            MyPlace newPlace = new MyPlace(title, description, lat, lng);
            int tag = jsonModel.addPlace(newPlace);
            getView().addMapMarker(title, description, new LatLng(lat, lng), tag);
            createAutoCompleteAdapter();
        } else {
            getView().makeToast("Проверьте правильность введенных данных");
        }
    }

    @Override
    public void onDeletePlaceButtonClicked(Marker marker) {
        jsonModel.deletePlace((int) marker.getTag());
        getView().removeMapMarker(marker);
        getView().closeBottomSheetDialog();
        createAutoCompleteAdapter();
    }

    @Override
    public void createAutoCompleteAdapter() {
        adapter = new PlaceAdapter(
                context, R.layout.activity_maps, 0, jsonModel.getAllPlaces());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        getView().setAutoCompleteAdapter(adapter);
    }

    @Override
    public void onSearchPlaceItemClicked(AdapterView<?> adapterView, int pos) {
        InputMethodManager inputMethodManager = (
                InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                getView().getActivity().getCurrentFocus().getWindowToken(), 0);

        MyPlace selectedPlace = (MyPlace) adapterView.getItemAtPosition(pos);
        Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
        placeLocation.setLatitude(selectedPlace.getLatitude());
        placeLocation.setLongitude(selectedPlace.getLongitude());
        getView().animateCamera(placeLocation);
    }
}
