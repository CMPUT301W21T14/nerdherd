package com.example.nerdherd.UserInterfaceQRCodes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.example.nerdherd.Database.LocalUser;
import com.example.nerdherd.MenuController;
import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.example.nerdherd.QRCodes.QRHelper;
import com.example.nerdherd.QRCodes.QRResult;
import com.example.nerdherd.R;
import com.example.nerdherd.UserInterfaceExperiments.ExperimentViewActivity;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;

import androidx.drawerlayout.widget.DrawerLayout;


/**
 * This activity allows the user to Scan a QR or Barcode, and use it to add a trial to the associated experiment
 */
public class QRCodeActivity extends AppCompatActivity implements LocationListener {
    // taken from https://medium.com/analytics-vidhya/creating-a-barcode-scanner-using-android-studio-71cff11800a2
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    public static final int PERMISSIONS_REQUEST_LOCATION = 99;

    private TextView barcodeText;
    private String barcodeData;
    private QRResult result;

    private Button addTrialButton;
    private Button viewExperimentButton;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private MenuController menuController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_scan);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.qr_layout);
        navigationView = findViewById(R.id.navigator);

        setSupportActionBar(toolbar);

        menuController = new MenuController(QRCodeActivity.this, toolbar, navigationView, drawerLayout);
        menuController.useMenu(false);

        surfaceView = findViewById(R.id.surface_view);
        barcodeText = findViewById(R.id.barcode_text);
        viewExperimentButton = findViewById(R.id.btn_view_experiment_qr);

        addTrialButton = findViewById(R.id.btn_add_qr_trial);
        addTrialButton.setVisibility(View.GONE);
        viewExperimentButton.setVisibility(View.GONE);

        addTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExperimentManager eMgr = ExperimentManager.getInstance();
                if(!eMgr.addTrialToExperiment(result.getExperimentId(), eMgr.generateTrial(Float.valueOf(result.getResult()), LocalUser.getLastLocation()))) {
                    Log.d("eMgr", "Invalid experimentId");
                } else {
                    Toast.makeText(getApplicationContext(), "Trial added successfully!", Toast.LENGTH_LONG).show();
                }
            }
        });

        viewExperimentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QRCodeActivity.this, ExperimentViewActivity.class);
                intent.putExtra("experimentId", result.getExperimentId());
                startActivity(intent);
            }
        });

        initLocationUpdates();
        requestCamera();
    }

    private void beginLocationUpdates() {
        // https://stackoverflow.com/questions/1513485/how-do-i-get-the-current-gps-location-programmatically-in-android
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(QRCodeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, this);
        }
    }

    private void requestCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initialiseDetectorsAndSources();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(QRCodeActivity.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }

    // https://stackoverflow.com/questions/10311834/how-to-check-if-location-services-are-enabled
    public void initLocationUpdates() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {

            return;
        }
        beginLocationUpdates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initialiseDetectorsAndSources();
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initialiseDetectorsAndSources() {

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .setRequestedFps(1)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(QRCodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(QRCodeActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                // Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    barcodeText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Barcode b = barcodes.valueAt(0);
                            barcodeData = b.displayValue;

                            result = null;
                            // If it's a QR code, test to see if it contains valid data for experiment:trial format
                            // result == null if not
                            if(b.format == Barcode.QR_CODE) {
                                result = QRHelper.processQRTextString(barcodeData);
                            } else {
                                // If it's not a QR, assume a barcode.
                                // 1). See if barcode is mapped to a trial already
                                // If it is, get result, otherwise ignore it (we can register them in trial activity)
                                if(LocalUser.getBarcodeMapping(barcodeData) == null) {
                                    // We have a new barcode, ask to register it? Or do this in Trials?
                                    addTrialButton.setVisibility(View.GONE);
                                } else {
                                    result = QRHelper.processQRTextString(LocalUser.getBarcodeMapping(barcodeData));
                                }
                            }
                            if(result != null) {
                                ExperimentManager eMgr = ExperimentManager.getInstance();
                                String experimentTitle = eMgr.getExperiment(result.getExperimentId()).getTitle();
                                String displayString = "Add trial to "+experimentTitle+" with result "+result.getResult();
                                barcodeText.setText(displayString);
                                addTrialButton.setVisibility(View.VISIBLE);
                                viewExperimentButton.setVisibility(View.VISIBLE);
                            } else {
                                barcodeText.setText("Barcode/QR not associated with any experiment! Or invalid trial outcome");
                                addTrialButton.setVisibility(View.GONE);
                            }
                        }
                    }, 500);
                } else {
                    barcodeText.setText("No Barcode/QR Found");
                    addTrialButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LocalUser.setLastLocation(location);
    }
}