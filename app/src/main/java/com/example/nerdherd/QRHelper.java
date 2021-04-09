package com.example.nerdherd;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.example.nerdherd.ObjectManager.ExperimentManager;
import com.google.firebase.Timestamp;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRHelper {

    public QRHelper() {

    }

    /**
     * Get the image of a QR code from a text string
     * @param qrData
     *      String - text to encode to QR code
     * @return
     *      Bitmap - Image generated (Can be used in ImageView.setImageBitmap)
     */
    public static Bitmap generateQRCodeOld(String qrData) {
        Bitmap bitmap = null;
            QRGEncoder qrgEncoder = new QRGEncoder(
                    qrData, null,
                    QRGContents.Type.TEXT,
                    400);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
            } catch (WriterException e) {
                Log.v("GenerateQRCode", e.toString());
            }
        return bitmap;
    }

    public static Bitmap generateQRCode(String qrData) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;

        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean saveTempBitmap(Bitmap bitmap, String data) {
        if (isExternalStorageWritable()) {
            return saveImage(bitmap, data);
        }else{
            Log.d("Access", "StorageDenied");
            return false;
        }
    }

    private static boolean saveImage(Bitmap finalBitmap, String data) {

        File myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        myDir.mkdirs();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = "nerdherdQR_["+data+"]"+ timeStamp +".jpg";

        File file = new File(myDir, fname);
        if (file.exists()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static boolean saveQRCode(Bitmap bitmap, String data) {
        return saveTempBitmap(bitmap, data);
    }

    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private static boolean isValidTrialOutcome(String type, String trialResult) {
        switch(type) {
            case "Binomial Trial":
                if(trialResult.equals("0") || trialResult.equals("1"))
                    return true;
                break;
            case "Count":
                // This doesn't really need a value, we add 1 no matter what
                return true;
            case "Measurement":
                if(isNumeric(trialResult)) {
                    return true;
                }
                break;
            case "Non-Negative Integer Count":
                if(isNumeric(trialResult)) {
                    if(Double.parseDouble(trialResult) > 0) {
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    private static boolean isValidExperimentTrial(String experimentId, String trialResult) {
        for( Experiment e : GlobalVariable.experimentArrayList ) {
            if ( e.getTitle().equals(experimentId) ) {
                if(isValidTrialOutcome(e.getType(), trialResult)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static QRResult processQRTextString(String data) {
        String parts[] = data.split(":");
        if(parts[0] != null && parts[1] != null) {
            String experimentId = parts[0];
            String trialResult = parts[1];

            ExperimentManager eMgr = ExperimentManager.getInstance();
            if(eMgr.getExperiment(experimentId) == null) {
                return null;
            }
            return new QRResult(experimentId, trialResult);
        }
        return null;
    }
}
