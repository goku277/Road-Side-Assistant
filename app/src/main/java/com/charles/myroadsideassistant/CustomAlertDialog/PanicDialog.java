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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.DialogFragment;

import com.charles.myroadsideassistant.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PanicDialog extends DialogFragment {
    Spinner spin;
    Button ok, cancel;
    Map<String, String> map1= new LinkedHashMap<>();
    List<String> stringlist;
    ArrayAdapter<String> arrayadapter;

    String choose="", mobilenumber="";

    ContactsUpdateDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {


        final String[] spinnerItems = new String[]{
                "Select helpline number",
                "National Emergency Number",
                "Police",
                "Fire",
                "Ambulance",
                "Medical Helpline"
        };

        map1.put("National Emergency Number", "112");
        map1.put("Police","100");
        map1.put("Fire","101");
        map1.put("Ambulance","102");
        map1.put("Medical Helpline","109");

        stringlist= new ArrayList<>();

        stringlist= Arrays.asList(spinnerItems);

        AlertDialog.Builder profileDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.panic_layout, null);

        profileDialog.setIcon(R.drawable.index);

        profileDialog.setView(view)
                .setTitle("Panic");

        spin= (Spinner) view.findViewById(R.id.call_helpline_id);

       // cancel= (Button) view.findViewById(R.id.cancel_id);

        ok= (Button) view.findViewById(R.id.ok_id);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!choose.equals("Select helpline number")) {
                    listener.applyPanicFields(map1.get(choose));
                }
                else Toast.makeText(getActivity(), "Select a helpline number at first", Toast.LENGTH_SHORT).show();
            }
        });

        ArrayAdapter<String> adp = new ArrayAdapter<String> (getActivity(), R.layout.text, stringlist);
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

        System.out.println("From PanicDialog choose is: " + choose + "\tmobile number is: " + map1.get(choose));

        return profileDialog.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (ContactsUpdateDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " Must implement this Doctor_profile_Listener");
        }
        ;
    }

    public interface ContactsUpdateDialogListener {
        public void applyPanicFields(String number1);
    }
}
