package com.example.nerdherd.UserInterfaceTrials;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nerdherd.Deprecated.BinomialTrialDeprecated;
import com.example.nerdherd.Deprecated.CountTrialDeprecated;
import com.example.nerdherd.Deprecated.Experiment_Deprecated;
import com.example.nerdherd.Deprecated.FireStoreController;
import com.example.nerdherd.Deprecated.MeasurementTrialDeprecated;
import com.example.nerdherd.Deprecated.NonnegativeTrialDeprecated;
import com.example.nerdherd.Deprecated.Trial_Deprecated;
import com.example.nerdherd.MenuController;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.ExperimentStatistics;
import com.example.nerdherd.Model.Trial;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.R;
import com.example.nerdherd.RecycleViewAdapters.AdapterController;
import com.example.nerdherd.RecycleViewAdapters.TrialsListAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class TrialStatisticsActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    private int expType;
    private ArrayList<Double> quartiles;
    private ArrayList<Double> Quartiles;
    private Experiment targetexp;
    private TextView trialValues;
    private TextView median_value;
    private TextView stdDeviationValue;
    private TextView quartileVal1;
    private TextView quartileVal2;
    private TextView quartileVal3;


    private ArrayList<MeasurementTrialDeprecated> Testtrial2 = new ArrayList<>();
    private Button checkPlot;
    private Button Histogrambtn;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_layout);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_statistics);
        navigationView = findViewById(R.id.navigate);
        trialValues = findViewById(R.id.mean_value);
        median_value = findViewById(R.id.median_value);
        stdDeviationValue = findViewById(R.id.std_deviationValue);
        quartileVal1 = findViewById(R.id.std_quartilesValue1);
        quartileVal2 = findViewById(R.id.std_quartilesValue2);
        quartileVal3 = findViewById(R.id.std_quartilesValue3);
        checkPlot = findViewById(R.id.plot);
        Histogrambtn = findViewById(R.id.histogram);
        setSupportActionBar(toolbar);


        menuController = new MenuController(TrialStatisticsActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        Intent intent = getIntent();
        String experimentId = intent.getStringExtra("experimentId");

        ExperimentManager eMgr = ExperimentManager.getInstance();
        Experiment experiment = eMgr.getExperiment(experimentId);
        ExperimentStatistics eStats = new ExperimentStatistics(eMgr.getTrialsExcludeBlacklist(experimentId), experiment.getType());
        if(experiment == null) {
            Log.d("Stats ", "exp=NULL");
            return;
        }

        checkPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrialStatisticsActivity.this, TrialScatterPlotActivity.class);
                intent.putExtra("experimentId", experimentId);
                startActivity(intent);
            }
        });

        Histogrambtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrialStatisticsActivity.this, TrialHistogramActivity.class);
                intent.putExtra("experimentId", experimentId);
                startActivity(intent);
            }
        });

        int maxLength = 6;
        targetexp = experiment;
        expType = targetexp.getType();
        trialValues.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        median_value.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        stdDeviationValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        quartileVal1.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        quartileVal2.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        quartileVal3.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        Log.d("Experiment type", experiment.typeToString());
        ArrayList<Trial> list = eMgr.getTrialsExcludeBlacklist(experimentId);
        ArrayList<Double> valList = new ArrayList<>();
        if (expType <= Experiment.EXPERIMENT_TYPE_MEASUREMENT) {
            for(Trial t : list) {
                valList.add(t.getOutcome());
            }
            Log.d("cval", String.valueOf(list));
            if (list.isEmpty()){
                trialValues.setText("");
                median_value.setText("");
                stdDeviationValue.setText("");
                quartileVal1.setText("");
                quartileVal3.setText("");
                quartileVal2.setText("");
            }
            else {
                //Mean calculations
                Double trialsMean = calculate_mean(valList);
                trialValues.setText(trialsMean + "");
                //Median calculations
                int length = valList.size();
                Double val = calculateMedian(valList, length);
                median_value.setText(val + "");
                //standard deviation calculations
                Double std_value = CalculateStandarddeviation(valList, length);
                stdDeviationValue.setText(std_value + "");
                //quartiles calculations - performed on performed on data - with at least 4 data value
                if (valList.size() >= 4) {
                    Quartiles = QuartilesCalculation(valList, length, val);
                    quartileVal1.setText(Quartiles.get(0) + "");
                    quartileVal3.setText(Quartiles.get(1) + "");
                    quartileVal2.setText(Quartiles.get(2) + "");
                }
            }
        }
    }


    //handle double calculation respect to mean
    public Double calculate_mean(ArrayList<Double>values){
        double total_sum = 0;
        for (int calc = 0; calc < values.size(); calc++){
            total_sum+=values.get(calc);
        }
        return total_sum / values.size();
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
}

