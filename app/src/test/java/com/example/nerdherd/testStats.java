package com.example.nerdherd;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class testStats {
    public <T>ArrayList<T> MockExperiment(Class<? extends Trial> type) {
        Experiment mockExperiment = new Experiment(new Profile("name","password", "email", "id", 0),
                                                "mock title", "status", "mock description", "type", 5,
                                                false, true, new ArrayList<String>(), new ArrayList<Trial>());
        if (type == CountTrial.class) {
            ArrayList<CountTrial> trials = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                trials.add(new CountTrial(i));
            }
            return (ArrayList<T>)trials;
        }
        else if (type == BinomialTrial.class) {
            ArrayList<BinomialTrial> trials = new ArrayList<>();
            trials.add(new BinomialTrial(1, 4));
            trials.add(new BinomialTrial(10, 0));
            return (ArrayList<T>)trials;
        }
        else if (type == NonnegativeTrial.class) {
            ArrayList<NonnegativeTrial> trials = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                ArrayList<Long> mockTrial = new ArrayList<>();
                mockTrial.add(Long.valueOf(i));
                trials.add(new NonnegativeTrial(mockTrial));
            }
            return (ArrayList<T>)trials;
        }
        return null;
    }

    @Test
    public void testCountConvert() {
        ArrayList<CountTrial> mockTrials = MockExperiment(CountTrial.class);
        statsactivity_checking mockActivity = new statsactivity_checking();
        mockActivity.counttrialing = mockTrials;
        ArrayList<Integer> convertList = mockActivity.countConvert();
        for (int i = 0; i < 10; i++) {
            assertEquals(i, (int)convertList.get(i));
        }
    }

    @Test
    public void testBinomialConvert() {
        ArrayList<BinomialTrial> mockTrials = MockExperiment(BinomialTrial.class);
        statsactivity_checking mockActivity = new statsactivity_checking();
        mockActivity.binomialtrialing = mockTrials;
        ArrayList<Integer> convertList = mockActivity.ConvertBinomial_val();

        assertEquals(1, (int)convertList.get(0));
        assertEquals(4, (int)convertList.get(1));
        assertEquals(10, (int)convertList.get(2));
        assertEquals(0, (int)convertList.get(3));
    }

    @Test
    public void testNonNegativeMean() {
        ArrayList<NonnegativeTrial> mockTrials = MockExperiment(NonnegativeTrial.class);
        statsActivity mockActivity = new statsActivity();

        ArrayList<Integer> postConvertList = new ArrayList<>();
        for (int i = 0; i < mockTrials.size(); i++)
            for (int j = 0; j < mockTrials.get(i).getNonNegativeTrials().size(); j++)
                postConvertList.add(mockTrials.get(i).getNonNegativeTrials().get(j).intValue());

        double mean = mockActivity.calculate_mean(postConvertList);
        assertEquals(4.5, mean, 0);
    }

}
