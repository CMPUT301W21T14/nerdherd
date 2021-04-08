package com.example.nerdherd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

/**
 * Maintain consistency in the app
 * 4 types of trials have been broken down into their model classes and fragment classes to maintain consistency and so that the app can follow everything being clicked on the screen that affects it
 * Essentially to create and host dialog per trial
 * @author Ogooluwa S. osamuel
 */

public class MeaurementTrialFragment extends DialogFragment {
    private TextView Measurement_val;

    private String experimentId;
    private Bitmap image;

    public MeaurementTrialFragment(String experimentId){
        this.experimentId=experimentId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_measurement_trial, null);

        //link to xml
        Button Recordtbn = view.findViewById(R.id.record_measurement);
        TextView qrcontainstv = view.findViewById(R.id.tv_binom_qr_data);
        Button saveQRBtn = view.findViewById(R.id.btn_save_qr_code);
        ImageView generateQRiv = view.findViewById(R.id.iv_binom_qr);
        image = null;
        qrcontainstv.setText("");
        Measurement_val = view.findViewById(R.id.measurement_input);

        Measurement_val.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(validateFloat(Measurement_val.getText().toString())) {
                    String value = Measurement_val.getText().toString();
                    image = QRHelper.generateQRCode(experimentId+":"+value);
                    qrcontainstv.setText(getQRActionDescription(value));
                    generateQRiv.setImageBitmap(image);
                    saveQRBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Recordtbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = Measurement_val.getText().toString();
                image = QRHelper.generateQRCode(experimentId+":"+value);
                qrcontainstv.setText(getQRActionDescription(value));
                generateQRiv.setImageBitmap(image);
                saveQRBtn.setVisibility(View.VISIBLE);
                float outcome;
                if(validateFloat(value)) {
                    outcome = Float.parseFloat(value);
                    ((TrialActivity) getActivity()).addMeasurementTrial(outcome);
                } else {
                    inputError();
                }
            }
        });

        saveQRBtn.setVisibility(View.GONE);

        saveQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image != null) {
                    QRHelper.saveQRCode(image);
                }
            }
        });

        // we create the acctual dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })

                .setNegativeButton("Cancel", null)
                .create();
    }

    public void inputError() {
        Toast.makeText(this.getContext(), "Invalid entry!", Toast.LENGTH_LONG).show();
    }

    public boolean validateFloat(String input) {
        try {
            double val = Float.parseFloat(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String getQRActionDescription(String outcome) {
        return "QR to Add a Measurement trial of " + outcome + " to current experiment";
    }
}
