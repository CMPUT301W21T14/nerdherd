package com.example.nerdherd;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BinomialTrial extends Trial{

    private TextView editText;
    private EditText editDescription;
    private TextView successCounter,failureCounter;
    private int pass;
    private int fail;
    public BinomialTrial(int pass, int fail){
        this.pass = pass;
        this.fail = fail;
    }

    public BinomialTrial(){
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.binomialtrial);

        Button sucesstbn = findViewById(R.id.btn_success);
        Button failurebtn = findViewById(R.id.btn_failure);
        Button cancelbtn = findViewById(R.id.CanceltrialCount);
        Button confirmbtn = findViewById(R.id.confirmtrials);
        successCounter = findViewById(R.id.success_counter);
        failureCounter = findViewById(R.id.failure_counter);


        final int[] failcounter = {0};
        final int[] successcount = {0};

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.putExtra("successCount",successcount[0]);
                intent.putExtra("failureCount", failcounter[0]);
                setResult(2,intent);
                finish();
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                setResult(1,intent);
                finish();
            }
        });

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





    }
}
