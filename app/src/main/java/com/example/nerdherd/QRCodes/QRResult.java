package com.example.nerdherd.QRCodes;

import com.google.firebase.Timestamp;

/**
 * Object representing a QRCode result associated with an experiment and trial outcome
 */
public class QRResult {
    private String experimentId;
    private String result;
    private Timestamp date;

    public QRResult(String experimentId, String result) {
        this.experimentId = experimentId;
        this.result = result;
        this.date = Timestamp.now();
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
