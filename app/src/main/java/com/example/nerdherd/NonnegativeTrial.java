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

    private Integer totalCount;
    private ArrayList<Long> nonNegativeTrials;

    public NonnegativeTrial(ArrayList<Long>nonNegatives){
        this.nonNegativeTrials = nonNegatives;
    }

    public ArrayList<Long> getNonNegativeTrials(){

//
        return nonNegativeTrials;
    }


    public void setNonNegativeTrials(ArrayList<Long> nonNegativeResult){
        this.nonNegativeTrials = nonNegativeResult;
    }
}
