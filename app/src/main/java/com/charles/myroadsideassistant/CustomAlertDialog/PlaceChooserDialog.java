package com.charles.myroadsideassistant.CustomAlertDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.charles.myroadsideassistant.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlaceChooserDialog extends DialogFragment {
    Spinner spin;
    Map<String, String> map1= new LinkedHashMap<>();
    Button Ok;
    List<String> stringlist;
    ArrayAdapter<String> arrayadapter;

    String choose="", mobilenumber="";

    placechooseListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final String[] spinnerItems = new String[]{
                "Select nearest emergency place",
                "Police",
                "Fire station",
                "Hospital",
                "Petrol pump",
                "Pharmacy",
                "Atm",
                "Post office",
                "child helpline",
                "Railway station"
        };

        stringlist= new ArrayList<>();

        //  stringlist.add(0, "Select a helpline number");

        stringlist= Arrays.asList(spinnerItems);

        AlertDialog.Builder profileDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.panic_layout, null);

        profileDialog.setIcon(R.drawable.places_icon);

        profileDialog.setView(view)
                .setTitle("Panic");
              /*  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
              /*  .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!choose.equals("Select helpline number")) {
                            listener.applyPlaceChooserFields(choose);
                        }
                        else Toast.makeText(getActivity(), "Select a helpline number at first", Toast.LENGTH_SHORT).show();
                    }
                }); */

        spin= (Spinner) view.findViewById(R.id.call_helpline_id);

        Ok= (Button) view.findViewById(R.id.ok_id);

        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!choose.equals("Select helpline number")) {
                    listener.applyPlaceChooserFields(choose);
                }
                else Toast.makeText(getActivity(), "Select a helpline number at first", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<String> adp = new ArrayAdapter<String> (getActivity(), R.layout.text,stringlist);
        // APP CURRENTLY CRASHING HERE
        spin.setAdapter(adp);
        //Set listener Called when the item is selected in spinner
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long arg3)
            {
                choose =parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), choose, Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> arg0)
            {
                // TODO Auto-generated method stub
            }
        });

      //  System.out.println("From PanicDialog choose is: " + choose + "\tmobile number is: " + map1.get(choose));

        //  newContactNumber = (EditText) view.findViewById(R.id.new_contact_number_id);
        //   oldName = (EditText) view.findViewById(R.id.old_contact_name_id);
        return profileDialog.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (placechooseListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Must implement this Doctor_profile_Listener");
        }
        ;
    }

    public interface placechooseListener {
        public void applyPlaceChooserFields(String number1);
    }
}
