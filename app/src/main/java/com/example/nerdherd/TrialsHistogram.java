package com.example.nerdherd;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TrialsHistogram extends AppCompatActivity  {
    private FireStoreController fireStoreController;
    private ArrayList<Experiment> savedList;
    private ArrayList<Experiment> showList;
    private DrawerLayout drawerLayout;
    private ArrayList<Integer> nonNegativetrialValues;
    private ArrayList<Double> measurementValues;
    private ArrayList<Integer> test_nonNegative;
    ArrayList<NonnegativeTrial> nonNegativetrialing = new ArrayList<>();
    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> labelName;
    ArrayList<Double> doubles_values2 = new ArrayList<>();
    ArrayList<Integer> measurements = new ArrayList<Integer>();
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MenuController menuController;
    private Experiment targetexp;
    private String expType;
    private ArrayList<Trial> trialArrayList = new ArrayList<Trial>();
    ArrayList<BinomialTrial> binomialtrialing = new ArrayList<>();
    private ArrayList<Integer> binomtrialValues;
    private ArrayList<Integer> trials_1;
    private ArrayList<Integer> trials_2;
    ArrayList<CountTrial> counttrialing = new ArrayList<>();
    private ArrayList<Integer> counttrialValues;
    ArrayList<MeasurementTrial> Testtrial2 = new ArrayList<>();
    private  ArrayList<Integer> frequency = new ArrayList<>();
    private ArrayList<Integer> trials;
    private ArrayList<Double> Quartiles;
    private ArrayList<Double> trial_values;
    private ArrayList<Double> quartiles;
    private ArrayList<Double> trialsing;
    private TextView trialheader;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_statistics);
        navigationView = findViewById(R.id.navigate);
        barChart = findViewById(R.id.barChart);
        trialheader = findViewById(R.id.trials);
        trialheader.setText("HISTOGRAM"+"");
        setSupportActionBar(toolbar);


        menuController = new MenuController(TrialsHistogram.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        savedList = new ArrayList<Experiment>();
        showList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();
        targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        expType = targetexp.getType();


        // Represents counts of counts. How many 0s, how many 2s.
        if (expType.equals("Count")) {
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Count trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {

                    if (list.isEmpty()){

                    }
                    else {
                        counttrialing = (ArrayList<CountTrial>) list.clone();
                        counttrialValues = countConvert();
                        //
                        Map<Integer, Integer> hm = new HashMap<>();
                        for (Integer i : counttrialValues) {
                            Integer j = hm.get(i);
                            hm.put(i, (j == null) ? 1 : j + 1);
                        }

                        //
                        create_Chart(hm);
                    }
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        // Represents counts of counts. How many 0s, how many 2s.
        if (expType.equals("Non-Negative Integer Count")){
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Non-negative trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCallback(ArrayList<Trial> list) {

                    if (list.isEmpty()){

                    }
                    else {
                        nonNegativetrialing = (ArrayList<NonnegativeTrial>) list.clone();
                        nonNegativetrialValues = nonNegative_val();
                        //
                        Map<Integer, Integer> hm = new HashMap<>();
                        for (Integer i : nonNegativetrialValues) {
                            Integer j = hm.get(i);
                            hm.put(i, (j == null) ? 1 : j + 1);
                        }

                        //
                        create_Chart(hm);
                    }
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (expType.equals("Measurement")){
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Measurement trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCallback(ArrayList<Trial> list) {

                    if (list.isEmpty()){

                    }
                    else {
                        Testtrial2 = (ArrayList<MeasurementTrial>) list.clone();
                        trial_values = measurement_val();
                        Log.d("Measurments", trial_values.toString());
                        //1. calculate # of bins
                        int numDataPoints = (int) Math.ceil(Math.sqrt(trial_values.size()));
                        Log.d("bin size", String.valueOf(numDataPoints));

                        //2. Lowest and Highest class Value
//                        double lowest_val = Collections.min(trial_values);
//                        double highest_val = Collections.max(trial_values);
//
//                        int length = trial_values.size();
//                        //3. Method 1: Range of each bin item / Interval Class Width
//                        //we Use 10 Intervals
//                        double width = (highest_val - lowest_val) / (10);
//                        Log.d("class interval", String.valueOf(width));
//                        //Method 2: interval size
//                        Double std_value = CalculateStandarddeviation(trial_values, length);
//                        Double classInterval = std_value / 3;
//                        Log.d("class Interval", classInterval.toString());
//
//
//                        int classRange = (int) (Math.ceil(highest_val - lowest_val) / classInterval);
//                        Log.d("Range", String.valueOf(classRange));
//                        int current_val = 0;
//                        ArrayList<Double> temp_val = new ArrayList<>();
//                        for (int y = 0; y < trial_values.size(); y++){
//                            temp_val.add(trial_values.get(y) + classInterval);
//                        }
//
//                        Log.d("temp values", temp_val.toString());
//                        int count = 0;
//                        for (int x = 0; x < trial_values.size(); x++){
//                            for (int y = 0; y < temp_val.size(); y++){
//                                if (trial_values.get(y) <= temp_val.get(x)){
//                                    count+=1;
//                                }
//                            }
//                            Log.d("total counted", String.valueOf(count));
//
//                        }
                        Map<Double, Double> hm = new HashMap<>();
                        for (Double i : trial_values) {
                            Double j = hm.get(i);
                            hm.put(i, (j == null) ? 1 : j + 1);
                        }

                        //
                        create_Charting(hm);
                    }
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }




    //handle double standard deviation calculations - for population
    public double CalculateStandarddeviation(ArrayList<Double> values, int arr_legnth)
    {
        double sum_val = 0.0;
        double std_deviation = 0.0;
        for(double num_val :values) {
            sum_val += num_val;
        }

        double mean = sum_val/arr_legnth;
        for(double num: values) {
            std_deviation += Math.pow(num - mean, 2);
        }
        return Math.sqrt(std_deviation/arr_legnth);
    }


    public int indexing(ArrayList<Double> values, int left_sub, int right_sub){
        int temp = right_sub - left_sub + 1;
        temp = (temp + 1) / 2-1;
        return temp;
    }

    /*This will convert 2d array Measurements into single array -
        to do calculations on all the data
     */
    public ArrayList<Double> measurement_val(){
        trialsing= new ArrayList<Double>();
        Log.d("------------------","---------------------------------");
        for (int i = 0; i < Testtrial2.size(); ++i) {
            for(int j = 0; j < Testtrial2.get(i).getMeasurements().size(); ++j) {
                trialsing.add(Testtrial2.get(i).getMeasurements().get(j));
            }
        }
        return trialsing;
    }


    //return all counts for given Experiment
    public ArrayList<Integer> countConvert(){
        trials_2 = new ArrayList<Integer>();
        for (int x = 0; x < counttrialing.size(); x++){
            trials_2.add(counttrialing.get(x).getTotalCount());
        }
        return trials_2;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public ArrayList<Integer> nonNegative_val(){
        test_nonNegative= new ArrayList<Integer>();
        Log.d("------------------","---------------------------------");
        for (int i = 0; i < nonNegativetrialing.size(); ++i) {
            for(int j = 0; j < nonNegativetrialing.get(i).getNonNegativeTrials().size(); ++j) {
                test_nonNegative.add(Math.toIntExact(nonNegativetrialing.get(i).getNonNegativeTrials().get(j)));
            }
        }
        return test_nonNegative;
    }


    public void create_Charting(Map<Double, Double> values){


        barEntryArrayList = new ArrayList<>();
        labelName = new ArrayList<>();
        int i =0;
        for (Map.Entry<Double, Double> val : values.entrySet()) {
            double key = val.getKey();
            double value = val.getValue();
            barEntryArrayList.add(new BarEntry(i, (float) value));
            labelName.add(String.valueOf(key));
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Trials");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        Description description = new Description();
        description.setText("");
        description.setTextSize(15);
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();

        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelName));


        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelName.size());
        barChart.animateY(2000);
        barChart.invalidate();

    }

    public void create_Chart(Map<Integer, Integer> hm){
        // displaying the occurrence of elements in the arraylist

        barEntryArrayList = new ArrayList<>();
        labelName = new ArrayList<>();
        int i =0;
        for (Map.Entry<Integer, Integer> val : hm.entrySet()) {
            int key = val.getKey();
            int value = val.getValue();
            barEntryArrayList.add(new BarEntry(i, value));
            labelName.add(String.valueOf(key));
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntryArrayList, "Trials");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        Description description = new Description();
        description.setText("");
        description.setTextSize(15);
        barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();

        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelName));


        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelName.size());
        barChart.animateY(2000);
        barChart.invalidate();

    }
}
