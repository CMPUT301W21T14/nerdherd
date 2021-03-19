package com.example.nerdherd;


/*This again inherits the Trial class.
* It allows the user to perform the trails where outcome could be integer
* */

public class CountTrial extends Trial {

    private Integer totalCount;

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
