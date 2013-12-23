package com.kodingen.cetrin.posttracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TrackCodeInfo extends Activity implements TrackInfoReceiver {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_code_info);
        Intent intent = getIntent();
        String trackCode = intent.getStringExtra("track");
        String lang = "uk";
        new TrackTask(this).execute(trackCode, lang);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.track_code_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoReceived(BarcodeInfo info) {
        if (info == null) {
            Toast.makeText(this, getString(R.string.noDataReceived), Toast.LENGTH_LONG).show();
            return;
        }
        TextView tvTrackCode = (TextView) findViewById(R.id.tvTrackCode);
        tvTrackCode.setText(getString(R.string.barcode) + " " + info.getBarcode());
        TextView tvDescription = (TextView) findViewById(R.id.tvDescr);
        tvDescription.setText(getString(R.string.description) + " " + info.getEventDescription().trim());
        TextView tvLastOffice = (TextView) findViewById(R.id.tvLastoffice);
        tvLastOffice.setText(getString(R.string.lastOffice) + " " + info.getLastOffice());
        TextView tvLastIndex = (TextView) findViewById(R.id.tvLastindex);
        tvLastIndex.setText(getString(R.string.lastIndex) + " " + info.getLastOfficeIndex());
//        TextView tvCode = (TextView) findViewById(R.id.tvCode);
//        tvCode.setText(getString(R.string.code) + " " + info.getCode());
        TextView tvDate = (TextView) findViewById(R.id.tvInfodate);
        tvDate.setText(getString(R.string.date) + " " + info.getEventDate());
    }
}
