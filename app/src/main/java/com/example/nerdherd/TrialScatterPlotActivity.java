package com.example.nerdherd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.nerdherd.Model.ExperimentE;
import com.example.nerdherd.Model.TrialT;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class TrialScatterPlotActivity extends AppCompatActivity  {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scatter_plot);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.draw_layout_experiment_statistics);
        navigationView = findViewById(R.id.navigate);

        setSupportActionBar(toolbar);
        menuController = new MenuController(TrialScatterPlotActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(true);

        Intent fromIntent = getIntent();
        String experimentId = fromIntent.getStringExtra("experimentId");

        ExperimentManager eMgr = ExperimentManager.getInstance();
        ExperimentE experiment = eMgr.getExperiment(experimentId);
        if (experiment == null) {
            Log.d("TrialHisto", "exp=NULL");
            return;
        }

        ArrayList<TrialT> list = (ArrayList<TrialT>) eMgr.getTrialsExcludeBlacklist(experimentId).clone();
        ScatterChart scatterChart;
        ScatterData scatterData;
        ScatterDataSet scatterDataSet;
        ArrayList scatterEntries= new ArrayList<>();

        scatterChart = findViewById(R.id.scatterChart);

        for(TrialT t : list) {
            double diffTime = t.getDate().getSeconds()-experiment.getDate().getSeconds();
            diffTime = diffTime / 3600; // By the hour?
            //diffTime = diffTime / 24; // By the day?
            BarEntry b = new BarEntry((float)diffTime, (float)t.getOutcome());
            scatterEntries.add(b);
        }

        scatterDataSet = new ScatterDataSet(scatterEntries, "Measurement Value");
        scatterData = new ScatterData(scatterDataSet);
        scatterChart.setData(scatterData);
        Description description = new Description();
        description.setText("");
        scatterChart.setDescription( description );
        scatterDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setValueTextColor(Color.BLACK);
        scatterDataSet.setValueTextSize(10f);
        scatterDataSet.setScatterShapeSize(40f);
    }
}
