package com.kodingen.cetrin.posttracker;

import android.os.AsyncTask;

class TrackTask extends AsyncTask<Void, Integer, BarcodeInfo> {
    private TrackInfoReceiver receiver;
    private String barcode;
    private String lang;


    public TrackTask(String barcode, String lang, TrackInfoReceiver receiver) {
        super();
        this.barcode = barcode;
        this.lang = lang;
        this.receiver = receiver;
    }

    @Override
    protected BarcodeInfo doInBackground(Void... params) {
        return PostTracker.track(barcode, lang);
    }

    @Override
    protected void onPostExecute(BarcodeInfo barcodeInfo) {
        receiver.onInfoReceived(barcodeInfo);
    }
}
