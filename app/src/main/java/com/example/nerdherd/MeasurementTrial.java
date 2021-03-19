package com.example.nerdherd;

/**
 * Third of the 4 trial classes
 * Allows user to perform a measurement trial or repeated measurements and record a decimal value of the recorded
 * Inherits Trial class that controls all the information of specific trials regardless of its kind
 * @author Ogooluwa S. osamuel
 */

public class MeasurementTrial extends Trial {

    private int totalMeasurementCount;

    /**
     * Records for the user to keep track of
     * Getter/setter of the class
     * @param totalMeasurementCount to keep record
     */

    public MeasurementTrial(int totalMeasurementCount) {
        this.totalMeasurementCount = totalMeasurementCount;
    }

    public int totalMeasureCount(){
        return totalMeasurementCount;
    }

    public double getTotalMeasurementCount() {
        return totalMeasurementCount;
    }

    public void setTotalMeasurementCount(int totalMeasurementCount) {
        this.totalMeasurementCount = totalMeasurementCount;
    }
}
