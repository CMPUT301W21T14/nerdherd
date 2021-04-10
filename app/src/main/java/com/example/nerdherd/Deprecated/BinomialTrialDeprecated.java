package com.example.nerdherd.Deprecated;

/**
 * First of the 4 trial classes
 * Allows user to perform a binomial trial with boolean outcomes
 * Inherits Trial class that controls all the information of specific trials regardless of its kind
 * @author Ogooluwa S. osamuel
 */

public class BinomialTrialDeprecated extends Trial_Deprecated {

    //will be responsible for just setting the values and stuff
    private Integer success;
    private Integer failure;
    private String timestamp;



    /**
     * Getter/setter/constructor for the class
     * @param success of the trial
     * @param failure of the trial
     */


    BinomialTrialDeprecated(Integer success, Integer failure, String timestamp)
    {
        this.success = success;
        this.failure = failure;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
