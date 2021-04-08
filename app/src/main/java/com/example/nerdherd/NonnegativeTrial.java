package com.example.nerdherd;

import java.util.ArrayList;

/**
 * Last of the 4 trial classes
 * Allows user to perform a nonnegative trial which outputs a count in a nonnegative interger format
 * Inherits Trial class that controls all the information of specific trials regardless of its kind
 * @author Ogooluwa S. osamuel
 */


/*This again inherits the Trial class.
 * It allows the user to perform the trails where outcome could be integer
 * */
public class NonnegativeTrial extends Trial {

    private String timestamp;
    private Integer totalCount;
    private ArrayList<Long> nonNegativeTrials;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public NonnegativeTrial(ArrayList<Long>nonNegatives, String timestamp){
        this.nonNegativeTrials = nonNegatives;
        this.timestamp = timestamp;
    }

    public ArrayList<Long> getNonNegativeTrials(){

//
        return nonNegativeTrials;
    }


    public void setNonNegativeTrials(ArrayList<Long> nonNegativeResult){
        this.nonNegativeTrials = nonNegativeResult;
    }
}
