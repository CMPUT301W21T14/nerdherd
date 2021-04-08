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
    private ArrayList<Integer> trials;
    private ArrayList<Double> Quartiles;
    private ArrayList<Integer> trial_values;
    private ArrayList<Double> quartiles;
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
    }





    public int indexing(ArrayList<Double> values, int left_sub, int right_sub){
        int temp = right_sub - left_sub + 1;
        temp = (temp + 1) / 2-1;
        return temp;
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
