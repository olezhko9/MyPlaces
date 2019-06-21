package com.example.olegnaumov.myplaces;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, MapPlacesContract.View {

    private GoogleMap mMap;
    private Marker mapCenterMarker;
    private FloatingActionButton mFab;
    private BottomSheetDialog bottomSheet;

    private MapPlacesContract.Presenter mPresenter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mPresenter = new MapPlacesPresenter(getApplicationContext());
        mPresenter.attachView(this);

        mFab = (FloatingActionButton) findViewById(R.id.add_marker_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onAddMarkerFabClicked(mapCenterMarker);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mPresenter.enableMyLocation();

        mapCenterMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                mapCenterMarker.setPosition(mMap.getCameraPosition().target);
            }
        });

        googleMap.setOnMarkerClickListener(this);

        mPresenter.onMapReady();
    }

    @Override
    public Activity getActivity() {
        return MapsActivity.this;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if (!marker.equals(mapCenterMarker)) {
            showBottomSheetDialog(marker);
        }
        return true;
    }

    public void animateCamera(Location location) {
        mMap.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()),15f)
        );
    }

    public void showPlaceSavingDialog(Bundle markerLocationBundle) {
        SaveMarkerDialog saveMarkerDialog = new SaveMarkerDialog();
        saveMarkerDialog.setPresenter(mPresenter);
        saveMarkerDialog.setArguments(markerLocationBundle);
        saveMarkerDialog.show(getSupportFragmentManager(), "Saving Dialog");
    }

    @Override
    public void showBottomSheetDialog(final Marker marker) {
        bottomSheet = new BottomSheetDialog(MapsActivity.this);
        View bottomDialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
        bottomSheet.setContentView(bottomDialogView);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomDialogView.getParent());
        bottomSheetBehavior.setPeekHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics()));


        TextView placeTitleTV = bottomDialogView.findViewById(R.id.place_title_tv);
        TextView placeDescriptionTV = bottomDialogView.findViewById(R.id.place_description_tv);

        placeTitleTV.setText(marker.getTitle());
        placeDescriptionTV.setText(marker.getSnippet());

        Button closeButton = bottomDialogView.findViewById(R.id.button_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeBottomSheetDialog();
            }
        });

        Button deletePlaceButton = bottomDialogView.findViewById(R.id.delete_place_btn);
        deletePlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onDeletePlaceButtonClicked(marker);
            }
        });

        bottomSheet.show();
    }


    @Override
    public void closeBottomSheetDialog() {
        if (bottomSheet != null) {
            bottomSheet.cancel();
        }
    }

    @Override
    public void enableMyLocationButton() {
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void makeToast(String msg) {
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void addMapMarker(String title, String snippet, LatLng latLng, int tag) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latLng.latitude, latLng.longitude))
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.place_24dp)));
        marker.setTag(tag);
    }

    @Override
    public void removeMapMarker(Marker marker) {
        marker.remove();
    }
}
