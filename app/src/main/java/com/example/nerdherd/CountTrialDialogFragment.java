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

public class CountTrialDialogFragment extends DialogFragment {

    private TextView Counter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_count_trial, null);

        //link to xml
        Button Counttbn = view.findViewById(R.id.btn_count);

        Counter = view.findViewById(R.id.counter);

        final int[] count = {0};

        //each time successbtn is clicked increment success for trial
        Counttbn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                count[0] = Integer.parseInt(Counter.getText().toString());
                count[0]++;
//               need to show the output on the textView
                Counter.setText(count[0] +"");
            }
        });





        // we create the acctual dialog here
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (count[0] == 0){
                            count[0] = 0;
                        }
                        ((TrialActivity) getActivity()).updateCountTrialView();
                    }
                })

                .setNegativeButton("Cancel", null)
                .create();
    }
}
