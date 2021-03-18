package com.example.nerdherd;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class BinomialTrialDialogFragment extends DialogFragment {

    private TextView successCounter,failureCounter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_binomial_trial, null);

        //link to xml
        Button sucesstbn = view.findViewById(R.id.btn_success);
        Button failurebtn = view.findViewById(R.id.btn_failure);
        successCounter = view.findViewById(R.id.success_counter);
        failureCounter = view.findViewById(R.id.failure_counter);


        final int[] failcounter = {0};
        final int[] successcount = {0};

        //each time successbtn is clicked increment success for trial
        sucesstbn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                successcount[0] = Integer.parseInt(successCounter.getText().toString());
                successcount[0]++;
//               need to show the output on the textView
                successCounter.setText(successcount[0] +"");
            }
        });

        //each time failbutton is clicked increment fail for trial
        failurebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                failcounter[0] = Integer.parseInt(failureCounter.getText().toString());
                failcounter[0]++;
                //need to show the output on the textView
                failureCounter.setText(failcounter[0]+"");
            }
        });




        // we create the acctual dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (successcount[0] == 0){
                            successcount[0] = 0;
                        }
                        else if (failcounter[0] == 0){
                            failcounter[0] = 0;
                        }
                        ((TrialActivity) getActivity()).updateBinomialTrialView();
                    }
                })

                .setNegativeButton("Cancel", null)
                .create();
    }
}
