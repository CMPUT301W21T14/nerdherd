package com.example.nerdherd;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Database.MockDatabaseAdapater;
import com.example.nerdherd.Model.Experiment;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.QRCodes.QRHelper;

import org.junit.Test;
import static org.junit.Assert.*;

public class testQRValidity {
    private ExperimentManager eMgr;
    private DatabaseAdapter dbAdapter;

    public testQRValidity() {
        dbAdapter = new MockDatabaseAdapater();
        LocalUser.setMockDBUsed();

        eMgr = ExperimentManager.getInstance();

        eMgr.setDatabaseAdapter(dbAdapter);

        eMgr.init();
    }

    @Test
    public void testQRInputValidity() {
        Experiment experiment = eMgr.getExperiment(MockDatabaseAdapater.MOCK_EXISTING_EXPERIMENT_ID);
        assertNotNull(experiment);

        String validStringFormatBinom = experiment.getExperimentId()+":1";
        String validStringFormatCount = experiment.getExperimentId()+":1";
        String validStringFormatMeasure = experiment.getExperimentId()+":43.5";
        String validStringFormatNonNeg = experiment. getExperimentId()+":24";
        String invalidStringFormatNonNeg = experiment.getExperimentId()+":-3";
        String invalidStringFormat = experiment.getExperimentId()+":asd";

        experiment.setType(Experiment.EXPERIMENT_TYPE_BINOMIAL);
        assertNotNull(QRHelper.processQRTextString(validStringFormatBinom));
        assertNull(QRHelper.processQRTextString(invalidStringFormat));

        experiment.setType(Experiment.EXPERIMENT_TYPE_COUNT);
        assertNotNull(QRHelper.processQRTextString(validStringFormatCount));
        assertNull(QRHelper.processQRTextString(invalidStringFormat));

        experiment.setType(Experiment.EXPERIMENT_TYPE_MEASUREMENT);
        assertNotNull(QRHelper.processQRTextString(validStringFormatMeasure));
        assertNull(QRHelper.processQRTextString(invalidStringFormat));

        experiment.setType(Experiment.EXPERIMENT_TYPE_NON_NEGATIVE);
        assertNotNull(QRHelper.processQRTextString(validStringFormatNonNeg));
        assertNull(QRHelper.processQRTextString(invalidStringFormatNonNeg));
        assertNull(QRHelper.processQRTextString(invalidStringFormat));
    }
}
