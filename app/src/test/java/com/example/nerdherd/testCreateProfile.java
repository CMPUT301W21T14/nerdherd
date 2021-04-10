package com.example.nerdherd;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.FirestoreAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Database.MockDatabaseAdapater;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;

import org.junit.Test;
import static org.junit.Assert.*;

public class testCreateProfile {
    private ExperimentManager eMgr;
    private ProfileManager pMgr;

    public testCreateProfile() {
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

    @Test
    public void testProfileCreation() {
        // MockDatabase has 5 profiles by default.
        assertEquals(5, pMgr.getProfileList().size());

        // Create a new profile - username and contact info generated - user can change if they want
        String newId = pMgr.createNewProfile();

        // Verify count went up
        assertEquals(6, pMgr.getProfileList().size());

        // Verify this profile now exists
        UserProfile profile = pMgr.getProfileList().get(Integer.parseInt(newId));
        assertEquals(profile.getUserId(), newId);
    }
}
