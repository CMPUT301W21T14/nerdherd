package com.example.nerdherd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Maintain consistency in the app
 * 4 types of trials have been broken down into their model classes and fragment classes to maintain consistency and so that the app can follow everything being clicked on the screen that affects it
 * Essentially to create and host dialog per trial
 * @author Ogooluwa S. osamuel
 */

public class BinomialTrialDialogFragment extends DialogFragment {

    private String experimentId;
    private Bitmap image = null;

    public BinomialTrialDialogFragment(String experimentId){
        this.experimentId=experimentId;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_binomial_trial, null);

        //link to xml
        Button sucesstbn = view.findViewById(R.id.btn_success);
        Button failurebtn = view.findViewById(R.id.btn_failure);
        TextView qrcontainstv = view.findViewById(R.id.tv_binom_qr_data);
        Button saveQRBtn = view.findViewById(R.id.btn_save_qr_code);
        ImageView generateQRiv = view.findViewById(R.id.iv_binom_qr);

        saveQRBtn.setVisibility(View.GONE);

        saveQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(image != null) {
                    QRHelper.saveQRCode(image);
                }
            }
        });


        //each time successbtn is clicked increment success for trial
        sucesstbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image = QRHelper.generateQRCode(experimentId+":"+"1");
                qrcontainstv.setText(getQRActionDescription("Successful"));
                generateQRiv.setImageBitmap(image);
                saveQRBtn.setVisibility(View.VISIBLE);
                ((TrialActivity)getActivity()).addSuccessfulTrial();
            }
        });

        //each time failbutton is clicked increment fail for trial
        failurebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image = QRHelper.generateQRCode(experimentId+":"+"0");
                qrcontainstv.setText(getQRActionDescription("Unsuccessful"));
                generateQRiv.setImageBitmap(image);
                saveQRBtn.setVisibility(View.VISIBLE);
                ((TrialActivity)getActivity()).addUnsuccessfulTrial();
            }
        });

        // we create the acctual dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // nothing to really do with this I guess
                    }
                })

                .setNegativeButton("Cancel", null)
                .create();
    }

    private String getQRActionDescription(String outcome) {
        return "QR to Add a " + outcome + " trial to current experiment";
    }
}
