package com.example.nerdherd;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.TrialT;
import com.example.nerdherd.ObjectManager.ExperimentManager;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Actually histograms, change later
 */
public class TrialHistogramActivity extends AppCompatActivity  {
    private DrawerLayout drawerLayout;
    BarChart barChart;
    ArrayList<BarEntry> barEntryArrayList;
    ArrayList<String> labelName;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histogram);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_statistics);
        navigationView = findViewById(R.id.navigate);
        barChart = findViewById(R.id.barChart);
        setSupportActionBar(toolbar);

        menuController = new MenuController(TrialHistogramActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        Intent fromIntent = getIntent();
        String experimentId = fromIntent.getStringExtra("experimentId");

        ExperimentManager eMgr = ExperimentManager.getInstance();
        ExperimentE experiment = eMgr.getExperiment(experimentId);
        if (experiment == null) {
            Log.d("TrialsPlot", "exp=NULL");
            return;
        }
        ArrayList<Integer> values = new ArrayList<>();
        for (TrialT t : eMgr.getTrialsExcludeBlacklist(experimentId)) {
            values.add((int) Math.round(t.getOutcome()));
        }
        create_Chart(values);
    }

    public void create_Chart(ArrayList<Integer> trials){
        barEntryArrayList = new ArrayList<>();
        labelName = new ArrayList<>();
        /*for (int i = 0; i < trials.size(); i++){
            String trial_val = trials.get(i).toString();

            barEntryArrayList.add(new BarEntry(i, Float.parseFloat(trial_val)));
            labelName.add(trial_val);
        }*/

        Map<Integer, Integer> binCount = new HashMap<>();
        for( Integer i : trials ) {
            if(binCount.containsKey(i)) {
                int count = binCount.get(i);
                count++;
                binCount.put(i, count);
            } else {
                binCount.put(i, 1);
            }
        }

        binCount = new TreeMap<Integer, Integer>(binCount);

        int counter = 0;
        for(Map.Entry entry : binCount.entrySet()) {
            float y = Float.parseFloat(entry.getValue().toString());
            BarEntry b = new BarEntry(counter, y);
            counter++;
            barEntryArrayList.add(b);
            labelName.add(entry.getKey().toString());
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
