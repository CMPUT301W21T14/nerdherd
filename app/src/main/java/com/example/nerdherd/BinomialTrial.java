package com.example.nerdherd;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class BinomialTrial extends Trial {

    //will be responsible for just setting the values and stuff
    private Integer success;
    private Integer failure;

    BinomialTrial (Integer success, Integer failure)
    {
        this.success = success;
        this.failure = failure;

    }

    public Integer getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public Integer getFailure() {
        return failure;
    }

    /*  Given an experiment, you can get the totalTrial
        the success and failure for an experiment*/
    public Integer totalTrial(){
        return success + failure;
    }
    public void setFailure(Integer failure) {
        this.failure = failure;
    }

    /* For an experiment, you can calculate the total Success Rate
     */
    public int calculateTotalSucessFail(Integer sVal, Integer fVal){

        float sRate = (float) sVal + fVal;
        Float sRt1 = Float.valueOf(sVal);
        float result = ((sRt1) / (sRate)) * 100;
        return (int) result;
    }
}