package com.example.nerdherd;

/**
 * Second of the 4 trial classes
 * Allows user to perform a count trial and store the outcome as an integer
 * Inherits Trial class that controls all the information of specific trials regardless of its kind
 * @author Ogooluwa S. osamuel
 */

public class CountTrial extends Trial {

    private Integer totalCount;

    /**
     * Getter/setter/constructor of the class
     * @param totalcount of the things observed
     */

    public CountTrial(Integer totalcount){
        this.totalCount = totalcount;
    }

    public Integer totaltrialCount(){
        return totalCount;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
