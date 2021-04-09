package com.example.nerdherd;

import com.example.nerdherd.Database.DatabaseAdapter;
import com.example.nerdherd.Database.FirestoreAdapter;
import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.Database.MockDatabaseAdapater;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.ObjectManager.ProfileManager;

import org.junit.Test;

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
        assert(pMgr.getProfileList().size() == 5);
        String newId = pMgr.createNewProfile();
        assert(pMgr.getProfileList().size() == 6);
        UserProfile profile = pMgr.getProfileList().get(Integer.parseInt(newId));
        assert(profile.getUserId()==newId);
    }
}
