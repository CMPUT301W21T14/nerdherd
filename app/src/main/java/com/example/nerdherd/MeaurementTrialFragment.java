package com.example.nerdherd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    private Double measures;

    private int minTrials;



    public MeaurementTrialFragment(){
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_measurement_trial, null);

        //link to xml
        Button Recordtbn = view.findViewById(R.id.record_measurement);

        ArrayList<Double> measurements = new ArrayList<Double>();

        Measurement_val = view.findViewById(R.id.measurement_input);

        Recordtbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Measurement_val.getText().toString().equals("")){
                    measures = Double.parseDouble(Measurement_val.getText().toString());
                    measurements.add(measures);
                }
                else{
                    Toast.makeText(getContext(), "Invalid Input", Toast.LENGTH_SHORT).show();
                }
                Measurement_val.setText("");

            }
        });
//
//        //each time successbtn is clicked increment success for trial
//        Counttbn.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                count[0] = Integer.parseInt(Counter.getText().toString());
//                count[0]++;
////               need to show the output on the textView
//                Counter.setText(count[0] +"");
//            }
//        });





        // we create the acctual dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (count[0] == 0){
//                            count[0] = 0;
//                        }
                        //pass the arraylist
                        int count = measurements.size();
//                        if (count < minTrials){
//                            Toast.makeText(getActivity(),"Requirement: Minimum Number of Trials not met", Toast.LENGTH_SHORT).show();
//                        }
                        //((TrialActivity) getActivity()).updateMeasurementTrialView(measurements, minTrials);
                    }
                })

                .setNegativeButton("Cancel", null)
                .create();
    }
}
