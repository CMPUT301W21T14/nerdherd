package com.example.nerdherd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nerdherd.ObjectManager.ExperimentManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Maintain consistency in the app
 * 4 types of trials have been broken down into their model classes and fragment classes to maintain consistency and so that the app can follow everything being clicked on the screen that affects it
 * Essentially to create and host dialog per trial
 * @author Ogooluwa S. osamuel
 */

public class CountTrialDialogFragment extends DialogFragment implements ExperimentManager.ExperimentOnChangeEventListener {

    private TextView counterTv;
    private ExperimentManager eMgr = ExperimentManager.getInstance();

    private String experimentId;
    private Bitmap image;


    public CountTrialDialogFragment(String experimentId){
        this.experimentId=experimentId;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_count_trial, null);

        Button Recordtbn = view.findViewById(R.id.record_measurement);
        TextView qrcontainstv = view.findViewById(R.id.tv_binom_qr_data);
        Button saveQRBtn = view.findViewById(R.id.btn_save_qr_code);
        ImageView generateQRiv = view.findViewById(R.id.iv_binom_qr);
        image = QRHelper.generateQRCode(experimentId+":1");
        generateQRiv.setImageBitmap(image);
        qrcontainstv.setText("Add a single count to current experiment");
        Button countbtn = view.findViewById(R.id.btn_count);
        eMgr.addOnChangeListener(this);
        counterTv = view.findViewById(R.id.counter);
        counterTv.setText(String.valueOf(eMgr.getTrialCount(experimentId)));

        //each time successbtn is clicked increment success for trial
        countbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TrialActivity) getActivity()).addCountTrial();
            }
        });

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

    @Override
    public void onExperimentDataChanged() {
        counterTv.setText(String.valueOf(eMgr.getTrialCount(experimentId)));
    }
}
