package com.example.nerdherd;

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

    public NonnegativeTrial(Integer totalcount){
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
