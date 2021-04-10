package com.example.nerdherd;

import com.example.nerdherd.Database.LocalUser;

import org.junit.Test;
import static org.junit.Assert.*;

public class testSubscribeExperiment {
    LocalUser lu = LocalUser.getInstance();
    private String mockExperimentId = "Any String Works";


    public testSubscribeExperiment() {
        LocalUser.setMockDBUsed();
    }

    @Test
    public void subscribeToExperiment() {
        // Make sure they aren't subscribed
        assertFalse(LocalUser.isSubscribed(mockExperimentId));

        // Subscribe them
        LocalUser.addSubscribedExperiment(mockExperimentId);

        // Check if subcribed
        assertTrue(LocalUser.isSubscribed(mockExperimentId));
    }
}
