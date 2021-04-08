package com.example.nerdherd.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.example.nerdherd.GPSTracker;
import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.QRResult;
import com.example.nerdherd.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * This stores all local data needed by the user along with some convenience data
 */
public class LocalUser implements ProfileManager.ProfileCreateEventListener {
    public static Integer[] imageList= {R.drawable.zelda, R.drawable.link, R.drawable.mipha, R.drawable.urbosa, R.drawable.riju, R.drawable.revali, R.drawable.daruk, R.drawable.impa, R.drawable.purah, R.drawable.purah_6_years_old, R.drawable.yunobo, R.drawable.king_rhoam, R.drawable.sidon};
    public static ArrayList<Integer> imageArray = new ArrayList(Arrays.asList(imageList));
    private static Set<String> custom_barcodes;
    private static Set<String> subscribed_experiments;
    private static HashMap<String, String> registeredBarcodeMap = new HashMap<>();
    private static String userId;
    private static Context appContext;
    private static LocalUser instance = null;
    private static Location lastLocation = null;
    private static boolean mockDBUsed = false;

    private LocalUser() {

    }

    public static LocalUser getInstance() {
        if( instance == null ) {
            instance = new LocalUser();
        }
        return instance;
    }

    public static void setContext(Context context) {
        appContext = context;
    }

    public static void addSubscribedExperiment(String experimentId) {
        subscribed_experiments.add(experimentId);
        saveLocalData();
    }

    public static void removeSubscribedExperiment(String experimentId) {
        subscribed_experiments.remove(experimentId);
        saveLocalData();
    }

    public static Set<String> getSubscribedExperiments() {
        return subscribed_experiments;
    }

    public static boolean isSubscribed(String experimentId) {
        return subscribed_experiments.contains(experimentId);
    }

    public static void loadLocalData() {
        if(appContext == null) {
            // Cannot load data without settting appContext
            return;
        }

        SharedPreferences prefs = appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        Set<String> subs = prefs.getStringSet("subscriptions", new HashSet<String>());
        Set<String> barc = prefs.getStringSet("barcodes", new HashSet<String>());

        subscribed_experiments = new HashSet<>();
        subscribed_experiments.addAll(subs);

        custom_barcodes = new HashSet<>();
        custom_barcodes.addAll(barc);

        Log.d("subs:", subscribed_experiments.toString());
        userId = prefs.getString("userId", "-1");
        if(userId.equals("-1")) {
            ProfileManager pMgr = ProfileManager.getInstance();
            pMgr.addOnCreateListener(instance);
            userId = pMgr.createNewProfile();
        }
        Log.d("LocalUser", userId);
        generateBarcodeMap();
    }

    public static void saveLocalData() {
        if(appContext == null) {
            // Cannot save data without settting appContext
            Log.d("appContext:", "isFucked");
            return;
        }
        SharedPreferences prefs = appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("userId", userId);
        edit.putStringSet("subscriptions", subscribed_experiments);
        edit.putStringSet("barcodes", custom_barcodes);
        edit.commit();
    }

    ///////////////////////////////

    private static void generateBarcodeMap() {
        for(String b : custom_barcodes) {
            String[] components = b.split(":");
            if(components.length != 3) {
                continue;
            }

            addRegisteredBarcode(components[0], components[1]+":"+components[2]);
        }
    }

    private static String getAssociatedDataForBarcode(String barcodeData) {
        for(String b : custom_barcodes) {
            String[] components = b.split(":");
            if(components.length != 3) {
                // uhoh
                continue;
            }
            if(components[0].equals(barcodeData)) {
                return components[1]+':'+components[2];
            }
        }
        return null;
    }

    public static void addCustomBarcode(String barcodeData, String experimentId, String trialResult) {
        String data = barcodeData+":"+experimentId+":"+trialResult;
        if(addRegisteredBarcode(barcodeData, experimentId+":"+trialResult)) {
            custom_barcodes.add(data);
        }
    }

    public static String getBarcodeMapping(String barcodeData) {
        return registeredBarcodeMap.get(barcodeData);
    }

    public static boolean addRegisteredBarcode(String barcodeData, String codeData) {
        if(registeredBarcodeMap.containsKey(barcodeData)) {
            return false;
        }
        registeredBarcodeMap.put(barcodeData, codeData);
        return true;
    }

    ///////////////////////////////////////

    public static void setLastLocation(Location location) {
        lastLocation = location;
    }

    public static Location getLastLocation() {
        return lastLocation;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setMockDBUsed() {
        mockDBUsed = true;
    }

    @Override
    public void onCreateProfileFailed(UserProfile failedToCreate) {
        // ?? Nothing we can really do except try again
        Log.d("LocalUser", "New Profile Failed!! IMPORTANT!!");
    }

    @Override
    public void onCreateProfileSuccess(UserProfile createdProfile) {
        saveLocalData();
        ProfileManager pMgr = ProfileManager.getInstance();
        pMgr.removeOnCreateListener(this);
        Log.d("Created: ", createdProfile.toString());
    }
}
