package com.example.nerdherd;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Database.MockDatabaseAdapater;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.Trial;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

import static com.example.nerdherd.Database.MockDatabaseAdapater.MOCK_EXISTING_EXPERIMENT_ID;
import static org.junit.Assert.*;

public class testAddTrial {
    private ExperimentManager eMgr;

    public testAddTrial() {
        DatabaseAdapter dbAdapter = new MockDatabaseAdapater();
        LocalUser.setMockDBUsed();

        eMgr = ExperimentManager.getInstance();

        eMgr.setDatabaseAdapter(dbAdapter);

        eMgr.init();

        LocalUser lu = LocalUser.getInstance();
        lu.loadLocalData();
    }

    @Test
    public void testAddTrial() {
        // MockDatabase has 10 experiments by default
        Experiment mockExperiment = eMgr.getExperiment(MOCK_EXISTING_EXPERIMENT_ID);
        // Know for sure experimentId="0" exists
        assert(mockExperiment!=null);

        // Create mock location for our trial
        GeoPoint mockLocation = new GeoPoint(53.5461, 113.4938);
        Trial mockTrial = new Trial(LocalUser.getUserId(), 5, mockLocation, Timestamp.now());

        // Make sure there are currently no trials in the experiment
        assertEquals(0, eMgr.getTrialCount(MOCK_EXISTING_EXPERIMENT_ID));
        assertEquals(0, mockExperiment.getTrials().size());

        // Add trial to experiment
        eMgr.addTrialToExperiment(mockExperiment.getExperimentId(), mockTrial);

        // Make sure one is added afterwards
        assertEquals(1, mockExperiment.getTrials().size());

        // Make sure the correct trial is there
        assertTrue(mockExperiment.getTrials().contains(mockTrial));
    }
}
