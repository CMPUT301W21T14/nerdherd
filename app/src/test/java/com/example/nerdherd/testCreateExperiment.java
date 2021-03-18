package com.example.nerdherd;

import org.junit.Test;
import static org.junit.Assert.*;

public class testCreateExperiment {

    @Test
    public void testIntegerValidation() {
        CreateExperimentActivity mockActivity = new CreateExperimentActivity();

        assertTrue(mockActivity.validateInteger("25"));
        assertFalse(mockActivity.validateInteger("a word"));
        assertFalse(mockActivity.validateInteger("-25"));
        assertFalse(mockActivity.validateInteger("0"));
    }

}
