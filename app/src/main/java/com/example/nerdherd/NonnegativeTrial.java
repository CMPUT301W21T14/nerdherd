package com.example.nerdherd;

/**
 * Last of the 4 trial classes
 * Allows user to perform a nonnegative trial which outputs a count in a nonnegative interger format
 * Inherits Trial class that controls all the information of specific trials regardless of its kind
 * @author Ogooluwa S. osamuel
 */

public class NonnegativeTrial extends Trial{
    private int totalNonnegativeCount;

    public NonnegativeTrial(int totalNonnegativeCount) {
        this.totalNonnegativeCount = totalNonnegativeCount;
    }

    public int totalNonnegativecount(){
        return totalNonnegativeCount;
    }

    public int getTotalNonnegativeCount() {
        return totalNonnegativeCount;
    }

    public void setTotalNonnegativeCount(int totalNonnegativeCount) {
        this.totalNonnegativeCount = totalNonnegativeCount;
    }
}
