package com.example.nerdherd;

import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;

public class statsactivity_checking extends AppCompatActivity {
    private FireStoreController fireStoreController;
    private ArrayList<Experiment> savedList;
    private ArrayList<Experiment> showList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private MenuController menuController;
    private String expType;
    private ArrayList<Double> trials;
    private ArrayList<Integer> trials_1;
    private ArrayList<Integer> trials_2;
    private ArrayList<Integer> trials_4;
    private ArrayList<Double> quartiles;
    private ArrayList<Double> Quartiles;
    //    private ArrayList<Integer> trials_1;
    private ArrayList<Double> trial_values;
    private ArrayList<Integer> binomtrialValues;
    private ArrayList<Integer> counttrialValues;
    private ArrayList<Integer> nonNegativetrialValues;
    private Experiment targetexp;
    private TextView trialValues;
    private TextView median_value;
    private TextView stdDeviationValue;
    private TextView quartileVal1;
    private TextView quartileVal2;
    private TextView quartileVal3;
    private String trialType;
    private int mintrials;
    private FloatingActionButton addtrials;
    private AdapterController adapterController;
    private TrialsAdapter adapter;
    private ArrayList<Trial> trialArrayList = new ArrayList<Trial>();
    private TrialsAdapter.onClickListener listener;
    ArrayList<Experiment> dataList;
    ArrayList<Trial> trialsList= new ArrayList<>();
    ArrayList<Trial> mTrialList = new ArrayList<>();
    ArrayList<Trial> TestList = new ArrayList<>();
    ArrayList<Double> doubles_values = new ArrayList<>();
    ArrayList<Double> doubles_values2 = new ArrayList<>();
    ArrayList<MeasurementTrial> Testtrial2 = new ArrayList<>();
    ArrayList<NonnegativeTrial> Testtrial3 = new ArrayList<>();
    ArrayList<BinomialTrial> binomialtrialing = new ArrayList<>();
    ArrayList<CountTrial> counttrialing = new ArrayList<>();
    ArrayList<NonnegativeTrial> nonNegativetrialing = new ArrayList<>();
    ListView ExperimentList;
    ArrayAdapter<Experiment> experimentAdapter;
    private ArrayList<Integer> testing_1;
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
        setSupportActionBar(toolbar);


        menuController = new MenuController(statsactivity_checking.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

//        recyclerView = findViewById(R.id.subscription_experiment_recyclerView);

        int maxLength = 7;
        savedList = new ArrayList<Experiment>();
        showList = new ArrayList<Experiment>();
        fireStoreController = new FireStoreController();
        targetexp = GlobalVariable.experimentArrayList.get(GlobalVariable.indexForExperimentView);
        expType = targetexp.getType();
        trialValues.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        median_value.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        stdDeviationValue.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
        Log.d("Experiment type", expType);
        if (expType.equals("Measurement")) {
            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Measurement trial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    Log.d("cval", String.valueOf(list));
                    Testtrial2 = (ArrayList<MeasurementTrial>) list.clone();
                    if (list.isEmpty()){
                        trialValues.setText("");
                        median_value.setText("");
                        stdDeviationValue.setText("");
                        quartileVal1.setText("");
                        quartileVal3.setText("");
                        quartileVal2.setText("");
                    }
                    else {

                        //convert to single array
                        trial_values = measurement_val();
                        //Mean calculations
                        Double trialsMean = calculate_mean(trial_values);
                        trialValues.setText(trialsMean + "");
                        //Median calculations
                        int length = trial_values.size();
                        Double val = calculateMedian(trial_values, length);
                        median_value.setText(val + "");
                        //standard deviation calculations
                        Double std_value = CalculateStandarddeviation(trial_values, length);
                        stdDeviationValue.setText(std_value + "");
                        //quartiles calculations - performed on performed on data - with at least 4 data values

                        if (trial_values.size() >= 4) {
                            Quartiles = QuartilesCalculation(trial_values, length, val);
                            quartileVal1.setText(Quartiles.get(0) + "");
                            quartileVal3.setText(Quartiles.get(1) + "");
                            quartileVal2.setText(Quartiles.get(2) + "");
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
        if (expType.equals("Binomial Trial")) {

            fireStoreController.keepGetTrialData(trialArrayList, targetexp.getTitle(), "Binomial", new FireStoreController.FireStoreCertainKeepCallback() {
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    Log.d("cval", String.valueOf(list));
                    //no calculations can be done on this
                    if (list.isEmpty()){
                        trialValues.setText("");
                        median_value.setText("");
                        stdDeviationValue.setText("");
                        quartileVal1.setText("");
                        quartileVal3.setText("");
                        quartileVal2.setText("");
                    }
                    else {
                        binomialtrialing = (ArrayList<BinomialTrial>) list.clone();
                        binomtrialValues = ConvertBinomial_val();
                        //Mean calculations
                        Double trialsMean = calculate_intmean(binomtrialValues);
                        trialValues.setText(trialsMean + "");
                        //Median calculations
                        int len = binomtrialValues.size();
                        Log.d("i got here", String.valueOf(binomtrialValues));
                        Double median_calc = calculateMedian2(binomtrialValues, len);
                        median_value.setText(median_calc + "");
                        //                    //standard deviation calculations
                        Double std_value = CalculateStandarddeviation2(binomtrialValues, len);
                        stdDeviationValue.setText(std_value + "");
                        //convert arraylist to double - to reuse quartiles function
                        doubles_values = convert_double(binomtrialValues);
                        Log.d("test1", String.valueOf(binomtrialValues));
                        // quartiles calculations - performed on data - with at least 4 data values
                        if (binomtrialValues.size() >= 4) {
                            Quartiles = QuartilesCalculation(doubles_values, len, median_calc);
                            quartileVal1.setText(Quartiles.get(0) + "");
                            quartileVal3.setText(Quartiles.get(1) + "");
                            quartileVal2.setText(Quartiles.get(2) + "");
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
                @Override
                public void onCallback(ArrayList<Trial> list) {
                    if (list.isEmpty()) {
                        trialValues.setText("");
                        median_value.setText("");
                        stdDeviationValue.setText("");
                        quartileVal1.setText("");
                        quartileVal3.setText("");
                        quartileVal2.setText("");
                    } else {
                        Testtrial3 = (ArrayList<NonnegativeTrial>) list.clone();

                        for (int i = 0; i < Testtrial3.size(); ++i) {
                            for(int j = 0; j < Testtrial3.get(i).getNonNegativeTrials().size(); ++j) {
                                testing_1.add((Testtrial3.get(i).getNonNegativeTrials().get(j)));

                            }
                        }
                        int su = 0;
                        for (int y =0; y < testing_1.size(); y++){
                            su = su + testing_1.get(0);
                        }
                        Log.d("non-negative", String.valueOf(testing_1.get(0)));
//                        Log.d("val_test", String.valueOf(testing_1));
////                        nonNegativetrialValues = nonNegative_val();
////                        Log.d("test long", String.valueOf(Testtrial3.get(0).getNonNegativeTrials().get(0)));
//
//                        //Mean calculations
//                        double val = calculate_intmean2(testing_1);
//                        trialValues.setText(CountMean + "");
                        //Median calculations
//                        int len = nonNegativetrialValues.size();
//                        Log.d("i got here", String.valueOf(nonNegativetrialValues));
//                        Double median_calc2 = calculateMedian2(nonNegativetrialValues, len);
//                        median_value.setText(median_calc2 + "");
//                        //standard deviation calculations
//                        Double std_value2 = CalculateStandarddeviation2(nonNegativetrialValues, len);
//                        stdDeviationValue.setText(std_value2 + "");
//                        //convert arraylist to double - to reuse quartiles function
//                        doubles_values2 = convert_double(nonNegativetrialValues);
//                        // quartiles calculations - performed on data - with at least 4 data values
//                        if (nonNegativetrialValues.size() >= 4) {
//                            Quartiles = QuartilesCalculation(doubles_values2, len, median_calc2);
//                            quartileVal1.setText(Quartiles.get(0) + "");
//                            quartileVal3.setText(Quartiles.get(1) + "");
//                            quartileVal2.setText(Quartiles.get(2) + "");
//                        }
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
                    counttrialing = (ArrayList<CountTrial>) list.clone();
                    if (list.isEmpty()){
                        trialValues.setText("");
                        median_value.setText("");
                        stdDeviationValue.setText("");
                        quartileVal1.setText("");
                        quartileVal3.setText("");
                        quartileVal2.setText("");
                    }
                    else {
                        counttrialValues = countConvert();
                        //Mean calculations
                        Double CountMean = calculate_intmean(counttrialValues);
                        trialValues.setText(CountMean + "");
                        //Median calculations
                        int len = counttrialValues.size();
                        Log.d("i got here", String.valueOf(counttrialValues));
                        Double median_calc2 = calculateMedian2(counttrialValues, len);
                        median_value.setText(median_calc2 + "");
                        //standard deviation calculations
                        Double std_value2 = CalculateStandarddeviation2(counttrialValues, len);
                        stdDeviationValue.setText(std_value2 + "");
                        //convert arraylist to double - to reuse quartiles function
                        doubles_values2 = convert_double(counttrialValues);
                        // quartiles calculations - performed on data - with at least 4 data values
                        if (counttrialValues.size() >= 4) {
                            Quartiles = QuartilesCalculation(doubles_values2, len, median_calc2);
                            quartileVal1.setText(Quartiles.get(0) + "");
                            quartileVal3.setText(Quartiles.get(1) + "");
                            quartileVal2.setText(Quartiles.get(2) + "");
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


    //handle double calculation respect to mean
    public Double calculate_mean(ArrayList<Double>values){
        double total_sum = 0;
        for (int calc = 0; calc < values.size(); calc++){
            total_sum+=values.get(calc);
        }
        return total_sum / values.size();
    }

    //handle double calculation respect to mean
    public Double calculate_intmean(ArrayList<Integer>values){
        double total_sum = 0;
        for (int calc = 0; calc < values.size(); calc++){
            total_sum+=values.get(calc);
        }
        return total_sum / values.size();
    }

    public double calculate_intmean2(ArrayList<Integer>values){
        double sum = 0;
        int n = values.size();
        ArrayList<Integer> val = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            sum+=values.get(i);
        }
        return sum / n;
//        return total_sum / values.size();
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

    //handle int Median calculations
    public double calculateMedian2(ArrayList<Integer>values, int arr_legnth)
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

    /*This will convert 2d array Measurements into single array -
        to do calculations on all the data
     */
    public ArrayList<Double> measurement_val(){
        trials = new ArrayList<Double>();
        Log.d("------------------","---------------------------------");
        for (int i = 0; i < Testtrial2.size(); ++i) {
            for(int j = 0; j < Testtrial2.get(i).getMeasurements().size(); ++j) {
                trials.add(Testtrial2.get(i).getMeasurements().get(j));
            }
        }
        return trials;
    }

    /*This will convert 2d array Nonnegative into single array -
        to do calculations on all the data
     */
    public ArrayList<Integer> nonNegative_val(){
        trials_1 = new ArrayList<Integer>();
        Log.d("------------------","---------------------------------");
        for (int i = 0; i < Testtrial3.size(); ++i) {
            for(int j = 0; j < Testtrial3.get(i).getNonNegativeTrials().size(); ++j) {
                trials_1.add(Testtrial3.get(i).getNonNegativeTrials().get(j));
            }
        }
        return trials_1;
    }

    //convert to single array of binomial trials
    public ArrayList<Integer> ConvertBinomial_val(){
        trials_1 = new ArrayList<Integer>();
        Log.d("------------------","---------------------------------");
//        Log.d("binomial trial", String.valueOf(binomialtrialing.get(0).getSuccess()));
        for (int y = 0; y < binomialtrialing.size(); y++){
            trials_1.add(binomialtrialing.get(y).getSuccess());
            trials_1.add(binomialtrialing.get(y).getFailure());
        }
        return trials_1;
    }

    //add total count for each trial int osingle array - calculations will
    //be applied after this
    public ArrayList<Integer> countConvert(){
        trials_2 = new ArrayList<Integer>();
        for (int x = 0; x < counttrialing.size(); x++){
            trials_2.add(counttrialing.get(x).getTotalCount());
        }
        return trials_2;
    }

    public ArrayList<Double> convert_double(ArrayList<Integer> doubleVal){
        ArrayList<Double> arr=new ArrayList<Double>(doubleVal.size());
        for (int i=0; i<doubleVal.size(); i++) {
            arr.add((double) doubleVal.get(i));
        }
        return arr;
    }
}

