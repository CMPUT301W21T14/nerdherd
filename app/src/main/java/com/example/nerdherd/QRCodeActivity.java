package com.example.nerdherd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGEncoder;
import androidx.drawerlayout.widget.DrawerLayout;

// https://medium.com/analytics-vidhya/creating-a-barcode-scanner-using-android-studio-71cff11800a2
public class QRCodeActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    private static final int REQUEST_CAMERA_PERMISSION = 201;

    private TextView barcodeText;
    private String barcodeData;
    private QRResult result;

    private Button addTrialButton;

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

        addTrialButton = findViewById(R.id.btn_add_trial);
        addTrialButton.setVisibility(View.GONE);

        addTrialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send this somewhere to add result to a trial result to an experiment;
            }
        });
        requestCamera();
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
                                Log.d("Test:", barcodeData);
                                result = QRHelper.processQRTextString(barcodeData);
                            } else {
                                // If it's not a QR, assume a barcode.
                                // 1). See if barcode is mapped to a trial already
                                // If it is, get result, otherwise ignore it (we can register them in trial activity)
                                Log.d("Okay?: ", barcodeData);
                                if(QRHelper.getBarcodeMapping(barcodeData) == null) {
                                    // We have a new barcode, ask to register it? Or do this in Trials?
                                    addTrialButton.setVisibility(View.GONE);
                                } else {
                                    result = QRHelper.processQRTextString(QRHelper.getBarcodeMapping(barcodeData));
                                }
                            }
                            if(result != null) {
                                barcodeText.setText(result.trialOperationString());
                                addTrialButton.setVisibility(View.VISIBLE);
                            } else {
                                barcodeText.setText("Barcode not associated with any experiment!");
                                addTrialButton.setVisibility(View.GONE);
                            }
                        }
                    }, 500);
                } else {
                    barcodeText.setText("No Barcode Found");
                    addTrialButton.setVisibility(View.GONE);
                }
            }
        });
    }
}