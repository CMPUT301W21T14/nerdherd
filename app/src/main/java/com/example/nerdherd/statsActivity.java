package com.example.nerdherd;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class displays all the stats of the experiments on the app screen for the user to see
 * The class contains some reference from <a href="https://bit.ly/2OQhXof"></a>
 * The class contains some reference from <a href="https://bit.ly/30UulWN"></a>
 * @author Zhipeng Z. zhipeng4
 */

public class statsActivity extends AppCompatActivity {


    private FireStoreController fireStoreController;
    private ArrayList<Experiment> savedList;
    private ArrayList<Experiment> showList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MenuController menuController;
    private String expType;
    private ArrayList<Integer> trials;
    private ArrayList<Integer> quartiles;
    private ArrayList<Integer> Quartiles;
//    private ArrayList<Integer> trials_1;
    private ArrayList<Integer> trial_values;
    private Experiment targetexp;
    private TextView trialValues;
    private TextView median_value;
    private TextView stdDeviationValue;
    private TextView quartileVal1;
    private TextView quartileVal2;
    private TextView quartileVal3;
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
        setSupportActionBar(toolbar);;

        menuController = new MenuController(statsActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

//        recyclerView = findViewById(R.id.subscription_experiment_recyclerView);


        savedList = new ArrayList<Experiment>();
        showList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();
        targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        expType = targetexp.getType();

        fireStoreController.experimentReader(savedList, new FireStoreController.FireStoreExperimentReadCallback() {
            @Override
            public void onCallback(ArrayList<Experiment> experiments) {

                for (Experiment allExperiment : experiments){
                    if (allExperiment.getTitle().contains(targetexp.getTitle())){
                        showList.add(allExperiment);
                    }
                }
                Log.d("count trials in exp", String.valueOf(targetexp.getType()));
                Log.d("size", String.valueOf(targetexp.getTrials().size()));

                //flatten to int array
                if (expType.equals("Binomial Trial") && targetexp.getTrials().size() >= 1){
                    trial_values = BinomialConvert(showList);
                    Double trialsMean = calculate_mean(trial_values);
                    trialValues.setText(trialsMean+"");
                    //median calculation
                    int length = trial_values.size();
                    Double val =calculateMedian(trial_values,length);
                    median_value.setText(val+"");
                    //standard deviation calculation
                    Double val_2 = CalculateStandarddeviation(trial_values,length);
                    val_2 = ((double)((int)(val_2 *100.0)))/100.0;
                    stdDeviationValue.setText(val_2+"");
                    //Quartiles Calculation
                    //given that there is a single element - then it is thus a repr:
                    //of the quartiles and also the median
                    if (targetexp.getTrials().size() == 1){
                        quartileVal1.setText(val+"");
                        quartileVal2.setText(val+"");
                        quartileVal3.setText(val+"");
                    }
                    else{
                        Quartiles = QuartilesCalculation(trial_values, length,val);
                        quartileVal1.setText(Quartiles.get(0)+"");
                        quartileVal2.setText(val+"");
                        quartileVal3.setText(Quartiles.get(1)+"");
                    }
//                    Log.d("testing trialvals", String.valueOf(trial_values.get(0)));
                }
                if (expType.equals("Count") && targetexp.getTrials().size() >= 1){
                    trial_values = CountConvert(showList);
                    Double trialsMean = calculate_mean(trial_values);
                    trialValues.setText(trialsMean+"");
                    //median calculation
                    int length = trial_values.size();
                    Double val =calculateMedian(trial_values,length);
                    median_value.setText(val+"");
                    //std calculation
                    Double val_2 = CalculateStandarddeviation(trial_values,length);
                    val_2 = ((double)((int)(val_2 *100.0)))/100.0;
                    stdDeviationValue.setText(val_2+"");
                    //Quartiles Calculation
                    Log.d("here", "iam here");
                    if (targetexp.getTrials().size() == 1){
                        quartileVal1.setText(val+"");
                        quartileVal2.setText(val+"");
                        quartileVal3.setText(val+"");
                    }
                    else{
                        Quartiles = QuartilesCalculation(trial_values, length,val);
                        quartileVal1.setText(Quartiles.get(0)+"");
                        quartileVal2.setText(val+"");
                        quartileVal3.setText(Quartiles.get(1)+"");
                    }
                }
                if (expType.equals("Measurement")&& targetexp.getTrials().size() >= 1){
//                    MeasurementConvert(showList);
                }
                if (expType.equals("Non-Negative Integer Count")&& targetexp.getTrials().size() >= 1){
                    Log.d("here", "iam here");
                    trial_values = NonnegativeConvert(showList);
                    Double trialsMean = calculate_mean(trial_values);
                    trialValues.setText(trialsMean+"");
                    //median calculation
                    int length = trial_values.size();
                    Double val =calculateMedian(trial_values,length);
                    median_value.setText(val+"");
                    //std calculation
                    Double val_2 = CalculateStandarddeviation(trial_values,length);
                    val_2 = ((double)((int)(val_2 *100.0)))/100.0;
                    stdDeviationValue.setText(val_2+"");
                    //Quartiles Calculation
                    if (targetexp.getTrials().size() == 1){
                        quartileVal1.setText(val+"");
                        quartileVal2.setText(val+"");
                        quartileVal3.setText(val+"");
                    }
                    else{
                        Quartiles = QuartilesCalculation(trial_values, length,val);
                        quartileVal1.setText(Quartiles.get(0)+"");
                        quartileVal2.setText(val+"");
                        quartileVal3.setText(Quartiles.get(1)+"");
                    }
                }
                //                showExperiments(recyclerView, showList);
            }

        }, new FireStoreController.FireStoreExperimentReadFailCallback() {
            @Override
            public void onCallback() {
                Toast.makeText(getApplicationContext(), "The database cannot be accessed at this point, please try again later. Thank you.", Toast.LENGTH_SHORT).show();
            }
        });






    }

    public ArrayList<Integer> NonnegativeConvert(ArrayList<Experiment>showList){
        trials = new ArrayList<Integer>();
        if (targetexp.getTrials().size() == 0){
            Log.d("dont display", "This experiment has no trials");
        }
        else{
            for (int counter = 0; counter < targetexp.getTrials().size();counter++){
//            if showList.get(counter).
                Log.d("testing non-negative",String.valueOf(((NonnegativeTrial)showList.get(0).getTrials().get(counter)).getTotalNonnegativeCount()));
                Integer counted_val = ((NonnegativeTrial)showList.get(0).getTrials().get(counter)).getTotalNonnegativeCount();
                trials.add(counted_val);
            }
        }
        return trials;
    }
    //instead of counts - it should be an array of measurments for each trial
    //need to convert to integer array
//    public void MeasurementConvert(ArrayList<Experiment> showList){
//        trials_1 = new ArrayList<Double>();
//        if (targetexp.getTrials().size() == 0){
//            Log.d("dont display", "This experiment has no trials");
//        }
//        else{
//            for (int counter = 0; counter < targetexp.getTrials().size();counter++){
////            if showList.get(counter).
//                Log.d("values", String.valueOf((MeasurementTrial)showList.get(0).getTrials().get(counter)));
////                trials_1.add(counted_val);
//            }
//        }
////        return trials_1;
//
//    }
    //need to convert to integer array
    public ArrayList<Integer> CountConvert(ArrayList<Experiment> showList){

        trials = new ArrayList<Integer>();
        if (targetexp.getTrials().size() == 0){
            Log.d("dont display", "This experiment has no trials");
        }
        else{
            for (int counter = 0; counter < targetexp.getTrials().size();counter++){
//            if showList.get(counter).
                Integer counted_val = ((CountTrial)showList.get(0).getTrials().get(counter)).getTotalCount();
                trials.add(counted_val);
            }
        }
        return trials;

    }

    //need to convert to integer array
    public ArrayList<Integer> BinomialConvert(ArrayList<Experiment> showList) {

        trials = new ArrayList<Integer>();
        if (targetexp.getTrials().size() == 0) {
            Log.d("dont display", "This experiment has no trials");
        } else {
            for (int counter = 0; counter < targetexp.getTrials().size(); counter++) {
//                Log.d("values test", String.valueOf(((BinomialTrial)showList.get(counter).getTrials().get(0)).getSuccess()));
                Integer success_trial = ((BinomialTrial) showList.get(0).getTrials().get(counter)).getSuccess();
                Integer failure_trial = ((BinomialTrial) showList.get(0).getTrials().get(counter)).getFailure();
                trials.add(success_trial);
                trials.add(failure_trial);
//                Log.d("values", String.valueOf(failure_trial));
            }
        }
        return trials;
    }

//        assuming values given are integers
    public Double calculate_mean(ArrayList<Integer>values){
        double total_sum = 0;
        for (int calc = 0; calc < values.size(); calc++){
            total_sum+=values.get(calc);
        }
        return total_sum / values.size();
    }

    public static double calculateMedian(ArrayList<Integer>values, int arr_legnth)
    {
        // First we sort the array
        Collections.sort(values);

        // check for even case
        if (arr_legnth % 2 != 0)
            return (double) values.get(arr_legnth / 2);

        return (double)((values.get(arr_legnth - 1) / 2)+ (values.get(arr_legnth / 2) / 2.0));
    }

    public double CalculateStandarddeviation(ArrayList<Integer> values, int arr_legnth)
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

    /*
        get the median index
     */
    public int indexing(ArrayList<Integer> values, int left_sub, int right_sub){
        int temp = right_sub - left_sub + 1;
        temp = (temp + 1) / 2-1;
        return temp + 1;
    }

    /*
        Q1 is the middle value between the smallest value and the median value in the array
        Q2 is just the median - being calculated already.
        Q3 will be the middle value between the median and the great value in the array.
     */
    public ArrayList<Integer> QuartilesCalculation(ArrayList<Integer> values, int arr_legnth, double median)
    {
        quartiles = new ArrayList<Integer>();
        Collections.sort(values);
        int mid_index = indexing(values, 0, arr_legnth);

        //the median of the first section of passed in array
        int q1 = indexing(values, 0,
                mid_index);
        q1 = values.get(q1);

        // the median of the second section of passed in array
        int q3 = mid_index + indexing(values,
                mid_index + 1, arr_legnth);
        q3 = values.get(q3);
        quartiles.add(q1);
        quartiles.add(q3);
        return quartiles;
    }



}
