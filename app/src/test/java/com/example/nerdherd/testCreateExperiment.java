package com.example.nerdherd;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Database.MockDatabaseAdapater;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.Model.Region;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.UserInterfaceExperiments.CreateExperimentActivity;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;
import org.junit.runner.Description;

import static com.example.nerdherd.Database.MockDatabaseAdapater.MOCK_NON_EXISTANT_EXPERIMENT_ID;
import static org.junit.Assert.*;

public class testCreateExperiment {
    private ExperimentManager eMgr;
    private DatabaseAdapter dbAdapter;
    public testCreateExperiment() {
        dbAdapter = new MockDatabaseAdapater();
        LocalUser.setMockDBUsed();

        eMgr = ExperimentManager.getInstance();

        eMgr.setDatabaseAdapter(dbAdapter);

        eMgr.init();

        LocalUser lu = LocalUser.getInstance();
        lu.loadLocalData();
    }

    @Test
    public void testIntegerValidation() {
        CreateExperimentActivity mockActivity = new CreateExperimentActivity();

        assertTrue(mockActivity.validateInteger("25"));
        assertFalse(mockActivity.validateInteger("a word"));
        assertFalse(mockActivity.validateInteger("-25"));
        assertTrue(mockActivity.validateInteger("0"));
    }

    @Test
    public void testCreateNewExperiment() {

        // Generate an experiment that does not exist!

        // Make sure experiment does not exist!
        Experiment experiment = eMgr.getExperiment(MOCK_NON_EXISTANT_EXPERIMENT_ID);
        assertNull(experiment);

        //String title, String description, int type, int minTrials, Region region, boolean locationRequired, boolean publish, ExperimentCreateEventListener listener
        String mockTitle = "mockTitle";
        String mockDescription = "mockDescription";
        int mockType = Experiment.EXPERIMENT_TYPE_MEASUREMENT;
        int mockMinTrials = 47;
        Region mockRegion = new Region("Region description", new GeoPoint(43, 111), 100);

        // Since the new experiment ID's are handled by the dbAdapter it's hard to get them here
        // The internals of the mockDB adpater just increase the experimentId by 1 each time
        String next_experiment_id = dbAdapter.getNewExperimentId();
        next_experiment_id = String.valueOf( Integer.parseInt( next_experiment_id ) + 1 );
        // We now know the next created experimentId will be next_experiment_id

        // This creates
        eMgr.createExperiment(mockTitle, mockDescription, mockType, mockMinTrials, mockRegion, false, false, null);

        //  Make an experiment with expected Id exists
        Experiment createdExperiment = eMgr.getExperiment(next_experiment_id);
        assertNotNull(createdExperiment);

        // And that it is our experiment

        assertTrue(createdExperiment.getTitle().equals(mockTitle));
        assertTrue(createdExperiment.getDescription().equals(mockDescription));
        assertEquals(createdExperiment.getMinimumTrials(), mockMinTrials);
    }

}
