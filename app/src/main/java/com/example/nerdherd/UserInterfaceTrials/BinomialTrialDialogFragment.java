package com.example.nerdherd.UserInterfaceTrials;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
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
public class BinomialTrialDialogFragment extends DialogFragment {

    private String experimentId;
    private Bitmap image = null;
    private String qdata = null;
    private Button launchRegisterQrButton;
    private Switch successSwitch;
    private ImageButton printQRButton;

    public BinomialTrialDialogFragment(String experimentId){
        this.experimentId=experimentId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_binomial_trial, null);
        printQRButton = view.findViewById(R.id.ib_print_qr);
        //link to xml
        Button sucesstbn = view.findViewById(R.id.btn_success);
        launchRegisterQrButton = view.findViewById(R.id.btn_launch_register_qr);
        TextView qrcontainstv = view.findViewById(R.id.tv_binom_qr_data);
        Button saveQRBtn = view.findViewById(R.id.btn_save_qr_code);
        ImageView generateQRiv = view.findViewById(R.id.iv_binom_qr);
        successSwitch = view.findViewById(R.id.sw_success_failure_for_qr);
        qdata = experimentId + ":" + "0";

        image = QRHelper.generateQRCode(qdata);
        qrcontainstv.setText(getQRActionDescription("Unsuccessful"));
        generateQRiv.setImageBitmap(image);

        launchRegisterQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterBarcodeActivity.class);
                startActivityForResult(intent, 1);
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

        successSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    qdata = experimentId + ":" + "1";
                    image = QRHelper.generateQRCode(qdata);
                    qrcontainstv.setText(getQRActionDescription("Successful"));
                    generateQRiv.setImageBitmap(image);
                    launchRegisterQrButton.setText("Register Result to Barcode");
                    sucesstbn.setText("Add Success");
                } else {
                    qdata = experimentId + ":" + "0";
                    image = QRHelper.generateQRCode(qdata);
                    qrcontainstv.setText(getQRActionDescription("Unsuccessful"));
                    generateQRiv.setImageBitmap(image);
                    launchRegisterQrButton.setText("Register Result to Barcode");
                    sucesstbn.setText("Add Failure");
                }
            }
        });


        //each time successbtn is clicked increment success for trial
        sucesstbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(successSwitch.isChecked()) {
                    ((TrialActivity) getActivity()).addSuccessfulBinomialTrial();
                } else {
                    ((TrialActivity)getActivity()).addUnsuccessfulBinomialTrial();
                }
            }
        });

        printQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintHelper photoPrinter = new PrintHelper(getActivity());
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                photoPrinter.printBitmap("qr_"+experimentId+"_print", image);
            }
        });

        // we create the actual dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing to really do with this I guess
                    }
                })
                .create();
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
        return "QR to Add a " + outcome + " trial to current experiment";
    }
}
