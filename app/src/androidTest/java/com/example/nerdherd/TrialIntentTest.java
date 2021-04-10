package com.example.nerdherd;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.widget.EditText;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Database.MockDatabaseAdapater;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.UserInterfaceExperiments.CreateExperimentActivity;
import com.example.nerdherd.UserInterfaceExperiments.ExperimentViewActivity;
import com.example.nerdherd.UserInterfaceQuestions.QuestionViewActivity;
import com.example.nerdherd.UserInterfaceQuestions.QuestionsActivity;
import com.example.nerdherd.UserInterfaceSearch.SearchExperimentActivity;
import com.example.nerdherd.UserInterfaceTrials.TrialActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Test class for Experiment features
 */
@RunWith(AndroidJUnit4.class)
public class TrialIntentTest {

    private Solo solo;
    private ExperimentManager eMgr;
    private ProfileManager pMgr;

    @Rule
    public ActivityTestRule<SearchExperimentActivity> rule = new ActivityTestRule<>(SearchExperimentActivity.class, true, true);

    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        DatabaseAdapter dbAdapter = new MockDatabaseAdapater();
        LocalUser.setMockDBUsed();

        eMgr = ExperimentManager.getInstance();
        pMgr = ProfileManager.getInstance();

        eMgr.setDatabaseAdapter(dbAdapter);
        pMgr.setDatabaseAdapter(dbAdapter);

        eMgr.init();
        pMgr.init();

        LocalUser lu = LocalUser.getInstance();
        lu.loadLocalData();
    }

    /**
     * Test the creation of a new experiment
     */
    @Test
    public void createAndSearchExperiment() {
        solo.assertCurrentActivity("Wrong Activity", SearchExperimentActivity.class);
        solo.clickOnText("Search");
        solo.waitForText("Description1");
        solo.clickOnText("Description1");

        solo.assertCurrentActivity("Wrong Activity", ExperimentViewActivity.class);
        solo.waitForText("Add Trials");
        solo.clickOnButton("Add Trials");
        solo.assertCurrentActivity("Wrong Activity", TrialActivity.class);
        solo.clickOnView(solo.getView(R.id.addTrial));
        solo.waitForText("COUNT TRIALS");
        solo.clickOnButton("Add a Count");
        solo.clickOnButton("Add a Count");
        solo.clickOnButton("Add a Count");
        solo.clickOnButton("Ok");
        solo.goBack();
        solo.waitForText("nonexistenttext", 1, 3000);
    }

    /**
     * Close activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}