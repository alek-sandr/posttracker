package com.kodingen.cetrin.posttracker;

import android.os.AsyncTask;

class TrackTask extends AsyncTask<String, Integer, BarcodeInfo> {

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
}
