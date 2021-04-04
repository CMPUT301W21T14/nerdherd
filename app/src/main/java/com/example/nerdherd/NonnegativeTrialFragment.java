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

public class NonnegativeTrialFragment extends DialogFragment {
    private TextView Nonnegative_val;
    private String recorded_vals;

    private int minTrials;
    private boolean isInt;
    private int user_recordedVal;

    /**
     * Constraint for the trial
     * Getter/setter for the class
     * @param minTrials for the trial to be successful
     */

    public NonnegativeTrialFragment(int minTrials){
        this.minTrials = minTrials;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.non_negative_trial, null);

        //link to xml
        Button Recordtbn = view.findViewById(R.id.record_nonNegativeTrial);

        ArrayList<Long> Nonnegativetrials = new ArrayList<>();

        Nonnegative_val = view.findViewById(R.id.nonNegative_input);

        Recordtbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorded_vals = (Nonnegative_val.getText().toString());
                isInt = check_int(recorded_vals);
                if (isInt){
                    user_recordedVal = Integer.parseInt(recorded_vals);
                    Nonnegativetrials.add((long) user_recordedVal);
                }
                else{
                    Toast.makeText(getContext(), "Invalid Input: only Non-negative Integer Allowed", Toast.LENGTH_SHORT).show();
                }

                Nonnegative_val.setText("");

            }
        });


        // we create the acctual dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int count = Nonnegativetrials.size();
//                        if (count < minTrials){
//                            Toast.makeText(getActivity(),"Requirement: Minimum Number of Trials not met", Toast.LENGTH_SHORT).show();
//                        }
                        ((TrialActivity) getActivity()).updateNonnegativeTrialView(Nonnegativetrials, minTrials);
                    }
                })

                .setNegativeButton("Cancel", null)
                .create();
    }

    public boolean check_int(String val){
        try
        {
            Integer.parseInt(val);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }
}
