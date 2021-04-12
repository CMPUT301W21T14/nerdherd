package com.example.nerdherd.UserInterfaceTrials;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.QRCodes.QRHelper;
import com.example.nerdherd.R;
import com.example.nerdherd.UserInterfaceQRCodes.RegisterBarcodeActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.print.PrintHelper;

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

    private String qdata = null;
    private Button launchRegisterQrButton;
    private ImageButton printQRButton;

    public MeaurementTrialFragment(String experimentId){
        this.experimentId=experimentId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_measurement_trial, null);

        Button Recordtbn = view.findViewById(R.id.record_measurement);
        TextView qrcontainstv = view.findViewById(R.id.tv_binom_qr_data);
        Button saveQRBtn = view.findViewById(R.id.btn_save_qr_code);
        ImageView generateQRiv = view.findViewById(R.id.iv_binom_qr);
        printQRButton = view.findViewById(R.id.ib_print_qr);
        image = null;
        qrcontainstv.setText("");
        Measurement_val = view.findViewById(R.id.measurement_input);

        launchRegisterQrButton = view.findViewById(R.id.btn_launch_register_qr);

        launchRegisterQrButton.setVisibility(View.GONE);
        printQRButton.setVisibility(View.GONE);

        launchRegisterQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterBarcodeActivity.class);
                startActivityForResult(intent, 1);
            }
        });

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
                    float outcome;
                    if(validateFloat(value)) {
                        outcome = Float.parseFloat(value);
                        qdata = experimentId+":"+String.valueOf(outcome);
                        launchRegisterQrButton.setVisibility(View.VISIBLE);
                        launchRegisterQrButton.setText("Register Result to Barcode");
                        printQRButton.setVisibility(View.VISIBLE);
                    }
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

        printQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintHelper photoPrinter = new PrintHelper(getActivity());
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                photoPrinter.printBitmap("qr_"+experimentId+"_print", image);
            }
        });

        saveQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image != null) {
                    if(QRHelper.saveQRCode(image, qdata)) {
                        saveSuccessToast();
                    }
                }
            }
        });

        // we create the actual dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK) {
            int overwrite = data.getIntExtra("overwrite", -1);
            String qrData = data.getStringExtra("qrData");
            if(overwrite != -1 && qrData != null) {
                LocalUser.addRegisteredBarcode(qrData, qdata, overwrite, true);
                launchRegisterQrButton.setText("Result Registered!");
            }
        }
    }

    private void saveSuccessToast() {
        Toast.makeText(getContext(), "Saved to Downloads!", Toast.LENGTH_LONG).show();
    }

    private String getQRActionDescription(String outcome) {
        return "QR to Add a Measurement trial of " + outcome + " to current experiment";
    }
}
