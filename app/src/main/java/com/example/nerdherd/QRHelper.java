package com.example.nerdherd;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.google.firebase.Timestamp;
import com.google.zxing.WriterException;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRHelper {
    public static HashMap<String, String> registeredBarcodeMap = new HashMap<>();

    public QRHelper() {

    }

    public static String getBarcodeMapping(String barcodeData) {
        return registeredBarcodeMap.get(barcodeData);
    }

    /**
     * Barcode will be associated with a certain experiment / outcome. in barcode data format experimentid:outcome
     * @param barcodeData
     *      String - data you get when you scan the specific barcode
     * @param codeData
     *      String - data you want to associate with that barcode instead
     * @return
     *      Boolean - True if register success (no previous mapping), False if previous mapping exists
     */
    public static boolean addRegisteredBarcode(String barcodeData, String codeData) {
        if(registeredBarcodeMap.containsKey(barcodeData)) {
            return false;
        }
        registeredBarcodeMap.put(barcodeData, codeData);
        return true;
    }

    /**
     * Get the image of a QR code from a text string
     * @param qrData
     *      String - text to encode to QR code
     * @return
     *      Bitmap - Image generated (Can be used in ImageView.setImageBitmap)
     */
    public static Bitmap generateQRCode(String qrData) {
        Bitmap bitmap = null;
            QRGEncoder qrgEncoder = new QRGEncoder(
                    qrData, null,
                    QRGContents.Type.TEXT,
                    250);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
            } catch (WriterException e) {
                Log.v("GenerateQRCode", e.toString());
            }
        return bitmap;
    }

    public static boolean saveQRCode(Bitmap bitmap) {
        return false;
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

            if(!isValidExperimentTrial(experimentId, trialResult)) {
                return null;
            }
            return new QRResult(experimentId, trialResult);
        }
        return null;
    }
}
