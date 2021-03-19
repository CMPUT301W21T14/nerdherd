package com.example.nerdherd;

/* This class inherits the Trial.
*Measurement trials are repeated measurements and you record a decimal value of whatever was measured.
* */

public class MeasurementTrial extends Trial {

    private int totalMeasurementCount;

    public MeasurementTrial(int totalMeasurementCount) {
        this.totalMeasurementCount = totalMeasurementCount;
    }

    public int totalMeasureCount(){
        return totalMeasurementCount;
    }

    public int getTotalMeasurementCount() {
        return totalMeasurementCount;
    }

    public void setTotalMeasurementCount(int totalMeasurementCount) {
        this.totalMeasurementCount = totalMeasurementCount;
    }
}
