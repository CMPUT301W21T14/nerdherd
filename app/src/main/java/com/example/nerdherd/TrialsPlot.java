package com.example.nerdherd;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
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

public class TrialsPlot extends AppCompatActivity  {
    private FireStoreController fireStoreController;
    private ArrayList<Experiment> savedList;
    private ArrayList<Experiment> showList;
    private DrawerLayout drawerLayout;
    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> labelName;
    ArrayList<Double> doubles_values2 = new ArrayList<>();
    ArrayList<Integer> measurements = new ArrayList<Integer>();
    ArrayList<Integer> nonnegatives = new ArrayList<>();
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
    private ArrayList<Integer> trials;
    private ArrayList<Double> Quartiles;
    private ArrayList<Integer> trial_values;
    private ArrayList<Double> quartiles;
    ArrayList<NonnegativeTrial> nonNegativetrialing = new ArrayList<>();
    private ArrayList<Integer> nonNegativetrialValues;
    private ArrayList<Integer> test_nonNegative;
    private ArrayList<Integer> testing_1;
    ArrayList<Double> doubles_values = new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_statistics);
        navigationView = findViewById(R.id.navigate);
        barChart = findViewById(R.id.barChart);
        setSupportActionBar(toolbar);


        menuController = new MenuController(TrialsPlot.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        savedList = new ArrayList<Experiment>();
        showList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();
        targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        expType = targetexp.getType();

        if (expType.equals("Binomial Trial")) {

            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Binomial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    Log.d("cval", String.valueOf(list));
                    //no calculations can be done on this
                    if (list.isEmpty()){

                    }
                    else {
                        binomialtrialing = (ArrayList<BinomialTrial>) list.clone();
                        binomtrialValues = ConvertBinomial_val();
                        create_Chart(binomtrialValues);
                    }
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }

            });
        }
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

                        // displaying the occurrence of elements in the arraylist
                        for (Map.Entry<Integer, Integer> val : hm.entrySet()) {
                            Log.d("map valus", val.toString());
                        }

                        //
                        create_Chart(counttrialValues);
                    }
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }
            });


        }
        if (expType.equals("Measurement")) {
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Measurement trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    Testtrial2 = (ArrayList<MeasurementTrial>) list.clone();
                    if (list.isEmpty()){
                    }
                    else {

                        //convert to single array
                        trial_values = measurement_val();
                        int length = trial_values.size();
                        Double val = calculateMedian2(trial_values, length);
                        //standard deviation calculations
                        doubles_values2 = convert_double(trial_values);

                        //quartiles calculations - performed on performed on data - with at least 4 data values

                        if (trial_values.size() >= 4) {
                            Quartiles = QuartilesCalculation(doubles_values2, length, val);
                            for(int y = 0; y < Quartiles.size(); y++){
                                measurements.add(Quartiles.get(y).intValue());
                            }
                            create_Chart(measurements);
                        }
                    }
                }
            }, new FireStoreController.FireStoreCertainKeepFailCallback() {
                @Override
                public void onCallback() {
                    Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
                }

            });


        }

        if (expType.equals("Non-Negative Integer Count")) {
            testing_1 = new ArrayList<Integer>();
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Non-negative trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    if (list.isEmpty()) {

                    } else {
                        nonNegativetrialing = (ArrayList<NonnegativeTrial>) list.clone();
                        nonNegativetrialValues = nonNegative_val();
                        //Median calculations
                        int len = nonNegativetrialValues.size();
                        Log.d("i got here", String.valueOf(nonNegativetrialValues));
                        Double median_calc = calculateMedian2(nonNegativetrialValues, len);

                        //                    //standard deviation calculations
                        Double std_value = CalculateStandarddeviation2(nonNegativetrialValues, len);

                        doubles_values = convert_double(nonNegativetrialValues);
                        Log.d("test1", String.valueOf(nonNegativetrialValues));
                        // quartiles calculations - performed on data - with at least 4 data values
                        if (nonNegativetrialValues.size() >= 4) {
                            Quartiles = QuartilesCalculation(doubles_values, len, median_calc);
                            for(int y = 0; y < Quartiles.size(); y++){
                                nonnegatives.add(Quartiles.get(y).intValue());
                            }
                            create_Chart(nonnegatives);
                        }
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

    //handle int std deviation- for population
    public double CalculateStandarddeviation2(ArrayList<Integer> values, int arr_legnth)
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

        Log.d("i got here",String.valueOf(Math.sqrt(std_deviation/arr_legnth)));
        return Math.sqrt(std_deviation/arr_legnth);
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



    public ArrayList<Double> convert_double(ArrayList<Integer> doubleVal){
        ArrayList<Double> arr=new ArrayList<Double>(doubleVal.size());
        for (int i=0; i<doubleVal.size(); i++) {
            arr.add((double) doubleVal.get(i));
        }
        return arr;
    }

    public double calculateMedian2(ArrayList<Integer>values, int arr_legnth)
    {
        // First we sort the array
        Collections.sort(values);

        // check for even case
        if (arr_legnth % 2 != 0)
            return (double) values.get(arr_legnth / 2);

        return (double)((values.get((arr_legnth - 1) / 2)+ values.get(arr_legnth / 2)) / 2.0);
    }

    //handle double quartiles calculations
    public ArrayList<Double> QuartilesCalculation(ArrayList<Double> values, int arr_legnth, double median)
    {
        quartiles = new ArrayList<Double>();
        Collections.sort(values);
        int mid_index = indexing(values, 0, arr_legnth);

        double q1_result, q2_result, q3_result;
        //the median of the first section of passed in array
        int q1 = indexing(values, 0,
                mid_index);
        q1_result = values.get(q1);

        q2_result = values.get(mid_index);

        // the median of the second section of passed in array
        int q3 = mid_index + indexing(values,
                mid_index + 1, arr_legnth);
        q3_result = values.get(q3);
        quartiles.add(q1_result);
        quartiles.add(q3_result);
        quartiles.add(q2_result);
        return quartiles;
    }

    public int indexing(ArrayList<Double> values, int left_sub, int right_sub){
        int temp = right_sub - left_sub + 1;
        temp = (temp + 1) / 2-1;
        return temp;
    }

    //handle double Median calculations
    public Double calculateMedian(ArrayList<Double>values, int arr_legnth)
    {
        // First we sort the array
        Collections.sort(values);

        // check for even case
        if (arr_legnth % 2 != 0)
            return (double) values.get(arr_legnth / 2);

        return (double)((values.get((arr_legnth - 1) / 2)+ values.get(arr_legnth / 2)) / 2.0);
    }

    public ArrayList<Integer> measurement_val(){
        trials = new ArrayList<Integer>();
        Log.d("------------------","---------------------------------");
        for (int i = 0; i < Testtrial2.size(); ++i) {
            for(int j = 0; j < Testtrial2.get(i).getMeasurements().size(); ++j) {
                trials.add(Testtrial2.get(i).getMeasurements().get(j).intValue());
            }
        }
        Log.d("Measurement Histogram", trials.toString());
        return trials;
    }

    //return all counts for given Experiment
    public ArrayList<Integer> countConvert(){
        trials_2 = new ArrayList<Integer>();
        for (int x = 0; x < counttrialing.size(); x++){
            trials_2.add(counttrialing.get(x).getTotalCount());
        }
        return trials_2;
    }


    //return all successes for given experiment
    public ArrayList<Integer> ConvertBinomial_val(){
        trials_1 = new ArrayList<Integer>();
        for (int y = 0; y < binomialtrialing.size(); y++){
            trials_1.add(binomialtrialing.get(y).getSuccess());

        }

        return trials_1;
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

    public void create_Chart(ArrayList<Integer> trials){
        barEntryArrayList = new ArrayList<>();
        labelName = new ArrayList<>();
        for (int i = 0; i < trials.size(); i++){
            String trial_val = trials.get(i).toString();

            barEntryArrayList.add(new BarEntry(i, Float.parseFloat(trial_val)));
            labelName.add(trial_val);
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
