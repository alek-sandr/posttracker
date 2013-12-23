package com.kodingen.cetrin.posttracker;

import android.os.AsyncTask;

class TrackTask extends AsyncTask<String, Integer, BarcodeInfo> {
    private TrackInfoReceiver receiver;

    public TrackTask(TrackInfoReceiver receiver) {
        super();
        this.receiver = receiver;
    }

    @Override
    protected BarcodeInfo doInBackground(String... params) {
        if (params.length == 2) {
            return PostTracker.track(params[0], params[1]);
        }
        if (params.length == 3) {
            return PostTracker.track(params[0], params[1], params[2]);
        }
        throw new IllegalArgumentException();
    }

    @Override
    protected void onPostExecute(BarcodeInfo barcodeInfo) {
        receiver.onInfoReceived(barcodeInfo);
    }
}
