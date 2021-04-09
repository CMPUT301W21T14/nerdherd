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
public class ExperimentIntentTest{

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
    public void createExperiment() {
        solo.assertCurrentActivity("Wrong Activity", SearchExperimentActivity.class);
        solo.clickOnButton("Search");
        solo.clickOnButton("Create Experiment");
        solo.assertCurrentActivity("Wrong Activity", CreateExperimentActivity.class);

        solo.enterText((EditText) solo.getView(R.id.exp_title), "Test Experiment");
        solo.enterText((EditText) solo.getView(R.id.experiment_description_editText), "Intent Test");
        solo.enterText((EditText) solo.getView(R.id.trial_number_editText), "5");
        solo.clickOnButton("Create");
        solo.waitForText("Create Experiment");
        solo.assertCurrentActivity("Wrong Activity", SearchExperimentActivity.class);

        assertFalse(solo.searchText("Intent Test"));
        solo.enterText((EditText) solo.getView(R.id.keyword_edit), "Intent");
        solo.clickOnButton("Search");
        assertTrue(solo.waitForText("Intent Test", 1, 1000));

        solo.enterText((EditText) solo.getView(R.id.keyword_edit), "Binomial");
        solo.clickOnButton("Search");
        assertTrue(solo.waitForText("Intent Test", 1, 1000));

        solo.enterText((EditText) solo.getView(R.id.keyword_edit), "experiment");
        solo.clickOnButton("Search");
        assertTrue(solo.waitForText("Intent Test", 1, 1000));
    }

    /**
     * Test Asking Questions and Replying
     */
    @Test
    public void askQuestionsAndReply() {
        solo.assertCurrentActivity("Wrong Activity", SearchExperimentActivity.class);
        solo.clickOnButton("Search");
        solo.clickOnButton("Create Experiment");
        solo.assertCurrentActivity("Wrong Activity", CreateExperimentActivity.class);

        solo.enterText((EditText) solo.getView(R.id.exp_title), "Test Experiment");
        solo.enterText((EditText) solo.getView(R.id.experiment_description_editText), "Intent Test");
        solo.enterText((EditText) solo.getView(R.id.trial_number_editText), "5");
        solo.clickOnButton("Create");
        solo.waitForText("Create Experiment");
        solo.assertCurrentActivity("Wrong Activity", SearchExperimentActivity.class);

        solo.enterText((EditText) solo.getView(R.id.keyword_edit), "Intent");
        solo.clickOnButton("Search");
        assertTrue(solo.waitForText("Intent Test", 1, 1000));

        solo.clickOnText("Intent Test");
        solo.assertCurrentActivity("Wrong Activity", ExperimentViewActivity.class);
        solo.clickOnButton("Questions");
        solo.assertCurrentActivity("Wrong Activity", QuestionsActivity.class);

        solo.enterText((EditText) solo.getView(R.id.question_input), "Is this an Intent Test?");
        solo.clickOnButton("Confirm");
        assertTrue(solo.waitForText("Is this an Intent Test?", 1, 1000));
        solo.clickOnText("Is this an Intent Test?");

        solo.assertCurrentActivity("Wrong Activiy", QuestionViewActivity.class);
        solo.enterText((EditText) solo.getView(R.id.reply_input), "yes");
        solo.clickOnButton("Confirm");
        assertTrue(solo.waitForText("yes", 1, 1000));
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
