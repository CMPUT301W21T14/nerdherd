package com.example.nerdherd.Database;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.example.nerdherd.Model.UserProfile;
import com.example.nerdherd.ObjectManager.ProfileManager;
import com.example.nerdherd.R;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This stores all local data needed by the user along with some convenience data
 * Contains the userId of the phone, custom barcode mappings, and subscribed experiments
 */
public class LocalUser implements ProfileManager.ProfileCreateEventListener {

    // Forgive me for this, there's a better place to store this but I got lazy.
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

    /**
     * Get instance of the singleton
     * @return
     *      LocalUser - instance of singleton
     */
    public static LocalUser getInstance() {
        if( instance == null ) {
            instance = new LocalUser();
        }
        return instance;
    }

    /**
     * Set the context of the application, needed for saving shared preferences (Local storage)
     * @param context
     *  Context - Context class - usually from MainActivity
     */
    public static void setContext(Context context) {
        appContext = context;
    }

    /**
     * Add an experiment to subscribe to + save it locally
     * @param experimentId
     *      String - experimentId to save to subscription list
     */
    public static void addSubscribedExperiment(String experimentId) {
        subscribed_experiments.add(experimentId);
        saveLocalData();
    }

    /**
     * Remove an experiment to subscribe to + save it locally
     * @param experimentId
     *      String - experimentId to remove from  subscription list
     */
    public static void removeSubscribedExperiment(String experimentId) {
        subscribed_experiments.remove(experimentId);
        saveLocalData();
    }

    /**
     * When location is needed, use the users last known location to avoid issues.
     * Potentially inaccurate.
     * @return
     *      GeoPoint - Firebase.GeoPoint object containing the Longitude and Lattitude of the last location.
     */
    public static GeoPoint getLastLocationGeo() {
        if(lastLocation == null) {
            return null;
        }
        return new GeoPoint(lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    /**
     * Returns the String set of subscribed Experiment Id's
     * @return
     *      Set<String> - HashSet of Strings containing experiment Id's user is subscribed to
     */
    public static Set<String> getSubscribedExperiments() {
        return subscribed_experiments;
    }

    /**
     * Checks whether or not the user is subscribed to the given experimentId
     * @param experimentId
     *  String - experimentId from Experiment to check
     * @return
     *  Boolean - True if subscribed, False if unsubscribed
     */
    public static boolean isSubscribed(String experimentId) {
        return subscribed_experiments.contains(experimentId);
    }

    /**
     * Loads mock data for testing/offline development
     */
    private static void loadMockData() {
        custom_barcodes = new HashSet<>();
        subscribed_experiments = new HashSet<>();
        registeredBarcodeMap = new HashMap<>();
        userId = "0";
    }

    /**
     * Loads/Initializes all local data in shared preferences unless mockDBUsed=True
     */
    public static void loadLocalData() {
        if(mockDBUsed) {
            loadMockData();
            return;
        }

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

    /**
     * Saves all local data back to the shared preferences files unless mockDBUsed==True
     */
    public static void saveLocalData() {
        if(mockDBUsed) {
            return;
        }

        if(appContext == null) {
            // Cannot save data without settting appContext
            Log.d("appContext:", "isFucked");
            return;
        }

        buildBarcodeStringSey();

        SharedPreferences prefs = appContext.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("userId", userId);
        edit.putStringSet("subscriptions", subscribed_experiments);
        edit.putStringSet("barcodes", custom_barcodes);
        edit.commit();
    }

    /* *********************************************************
     ******** Helper functions
     ***********************************************************/

    /**
     * Helper function to get stored String set from the format
     * barcodeData:experimentId:outcome to HashMap<barcodeData, "experimentId:outcome">
     */
    private static void generateBarcodeMap() {
        for(String b : custom_barcodes) {
            String[] components = b.split(":");
            if(components.length != 3) {
                continue;
            }

            addRegisteredBarcode(components[0], components[1]+":"+components[2], 0, false);
        }
    }

    private static void buildBarcodeStringSey() {
        for(Map.Entry entry : registeredBarcodeMap.entrySet() ) {
            custom_barcodes.add(entry.getKey()+":"+entry.getValue());
            Log.d("Adding: ", entry.getKey()+":"+entry.getValue());
        }
    }

    /**
     * Helper function to get the QR code equivalant data from a barcode we have mapped
     * to a specific trial:outcome
     * @param barcodeData
     *      String - data from a barcode from a book from home
     * @return
     *      String - string value of the QR code data we associate it with "experimentId:outcome"
     */
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

    /**
     * Adds a custom barcode to our local storage
     * @param barcodeData
     *      String - barcode of the book/object scanned
     * @param experimentId
     *      String - experimentId to associate the barcode with
     * @param trialResult
     *      String - trial outcome to associate the barcode with
     */
    public static void addCustomBarcode(String barcodeData, String experimentId, String trialResult) {
        String data = barcodeData+":"+experimentId+":"+trialResult;
        if(addRegisteredBarcode(barcodeData, experimentId+":"+trialResult, 0, true)) {
            custom_barcodes.add(data);
        }
    }

    /**
     * Generic function to get the experimentId:outcome from a specific barcodeData
     * @param barcodeData
     *      String - barcode data retreived from codescanner
     * @return
     *      String - data we want to map the barcode to
     */
    public static String getBarcodeMapping(String barcodeData) {
        return registeredBarcodeMap.get(barcodeData);
    }

    /**
     * Generic function to help with experiment->qr->barcode mapping
     * @param barcodeData
     *      String - barCode data as the key
     * @return
     */
    public static Boolean hasBarcodeMapping(String barcodeData) {
        return registeredBarcodeMap.containsKey(barcodeData);
    }

    /**
     * Adds a barcode to our local storage which we will now associate with a certain experiment+outcome
     * @param barcodeData
     *      String - barcode data retreieved from the codescanner
     * @param codeData
     *      String - new code data in the format "experimentId:outcome"
     * @return
     *      Boolean - returns true if barcode doesn't already exist. Can't have a barcode trigger multiple trial outcomes
     */
    public static boolean addRegisteredBarcode(String barcodeData, String codeData, int overwrite, boolean save) {
        if(registeredBarcodeMap.containsKey(barcodeData)) {
            if(overwrite == 1) {
                registeredBarcodeMap.remove(barcodeData);
            } else {
                return false;
            }
        }
        Log.d("BarcodeAdd: ", barcodeData+":"+codeData);
        registeredBarcodeMap.put(barcodeData, codeData);
        if(save) {
            saveLocalData();
        }
        return true;
    }

    ///////////////////////////////////////

    /**
     * sets the last location whenever we get a location update - for use with trials
     * @param location
     */
    public static void setLastLocation(Location location) {
        lastLocation = location;
    }

    /**
     * gets the last known location of our user
     * @return
     */
    public static Location getLastLocation() {
        return lastLocation;
    }

    /**
     * Gets the UserId which will be associated with a UserProfile in the database
     * @return
     *      String - UserId of the application user
     */
    public static String getUserId() {
        return userId;
    }

    /**
     * When set to true, does not save any data, and loads all empty maps for subscribed experiments
     * custom barcode maps, and userId=0
     */
    public static void setMockDBUsed() {
        mockDBUsed = true;
    }

    /**
     * Called when a profile fails to be created on the first run of the application
     * @param failedToCreate
     *      UserProfile - this is the UserProfile object we attempted to create
     */
    @Override
    public void onCreateProfileFailed(UserProfile failedToCreate) {
        // ?? Nothing we can really do except try again
        Log.d("LocalUser", "New Profile Failed!! IMPORTANT!!");
    }

    /**
     * Called when a profile is successfully created on the first run of the application
     * @param createdProfile
     *      UserProfile - object that was successfully written to the database
     */
    @Override
    public void onCreateProfileSuccess(UserProfile createdProfile) {
        saveLocalData();
        ProfileManager pMgr = ProfileManager.getInstance();
        pMgr.removeOnCreateListener(this);
        Log.d("Created: ", createdProfile.toString());
    }
}
