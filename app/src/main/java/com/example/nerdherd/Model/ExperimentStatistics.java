package com.example.nerdherd.Model;

import java.util.ArrayList;

/**
 * Class to generate statistics about an experiment
 * Do not save this in the database or aggregate within Experiment
 */
public class ExperimentStatistics {
    // pass in Experiment.getTrials(), Experiment.getType()
    private float mean;
    private float median;
    private float q1;
    private float q2;
    private float stdev;

    public ExperimentStatistics(ArrayList<TrialT> results, int type) {

    }

    public void generateStatistics() {

    }

    private void findMean() {

    }

    private void findStdev() {

    }

    private void findMedianAndQuartiles() {

    }
}
