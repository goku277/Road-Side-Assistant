package com.charles.myroadsideassistant.CustomAlertDialog;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.charles.myroadsideassistant.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class WeatherDialog extends AppCompatDialogFragment {

    EditText Cityname;
    private ProfileCreateListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder profileDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.weather_input_layout, null);

        profileDialog.setView(view)
                .setTitle("Get current Weather")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Get Weather", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String cityname = Cityname.getText().toString().trim();
                        listener.applyProfileCreateFields(cityname);
                    }
                });

        Cityname = (EditText) view.findViewById(R.id.input_city_name_id);
        return profileDialog.create();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ProfileCreateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Must implement this Doctor_profile_Listener");
        }
    }
    public interface ProfileCreateListener {
        public void applyProfileCreateFields(String cityname);
    }
}