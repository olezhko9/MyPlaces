package com.example.olegnaumov.myplaces;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class SaveMarkerDialog extends AppCompatDialogFragment {

    private EditText markerTitleEditText, markerDescriptionEditText;
    private MapPlacesContract.Presenter mPresenter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_marker, null);

        final double markerLat, markerLng;
        if (getArguments() != null) {
            markerLat = getArguments().getDouble("markerLat");
            markerLng = getArguments().getDouble("markerLng");

            builder
                .setView(view)
                .setTitle(String.format("Добавить место\n%f, %f", markerLat, markerLng))
                .setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                })
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPresenter.onSaveButtonClicked(
                                markerTitleEditText.getText().toString().trim(),
                                markerDescriptionEditText.getText().toString().trim(),
                                markerLat,
                                markerLng
                        );
                    }
                });
        }

        markerTitleEditText = view.findViewById(R.id.marker_title_et);
        markerDescriptionEditText = view.findViewById(R.id.marker_description_et);

        return builder.create();
    }

    public void setPresenter(MapPlacesContract.Presenter presenter) {
        mPresenter = presenter;
    }
}
