package com.example.nerdherd;


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
