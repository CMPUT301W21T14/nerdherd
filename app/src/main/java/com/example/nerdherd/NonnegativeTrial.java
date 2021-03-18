package com.example.nerdherd;

public class NonnegativeTrial extends Trial{
    private int totalNonnegativeCount;

    public NonnegativeTrial(int totalNonnegativeCount) {
        this.totalNonnegativeCount = totalNonnegativeCount;
    }

    public int totalNonnegativecount(){
        return totalNonnegativeCount;
    }
}
