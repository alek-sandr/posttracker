package com.kodingen.cetrin.posttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class TrackCodeInfo extends Activity implements TrackInfoReceiver {
    public static final String ACTION_SHOWINFO = "com.kodingen.cetrin.posttracker.intent.action.showinfo";
    public static final String ACTION_TRACKANDSHOW = "com.kodingen.cetrin.posttracker.intent.action.trackandshow";
    public static final String TRACKCODE = "track";

    private String trackCode;
    private String lang;
    private BarcodeInfo info;
    private boolean codeInDB = false;
    private DBHelper dbHelper;
    private AlertDialog saveDialog;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_code_info);
        progress = (ProgressBar) findViewById(R.id.pbTracking);
        Intent intent = getIntent();
        trackCode = intent.getStringExtra(TRACKCODE);
        lang = "uk";
        String action = intent.getAction();

        dbHelper = new DBHelper(this);
        dbHelper.open();
        codeInDB = dbHelper.isCodeInDB(trackCode);

        if (codeInDB) { // disable save button and get info from DB
            Button btnSave = (Button) findViewById(R.id.btnSaveCode);
            btnSave.setVisibility(View.GONE);
            info = dbHelper.getInfo(trackCode);
        } else { // create new Barcodeinfo
            info = new BarcodeInfo();
            info.setBarcode(trackCode);
        }
        dbHelper.close();
        if (action.equals(ACTION_TRACKANDSHOW)) { // check trackcode for changes
            progress.setVisibility(View.VISIBLE);
            new TrackTask(trackCode, lang, this).execute();
        } else { // just display information
            displayInfo(info);
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        final View dialogView = inflater.inflate(R.layout.save_code_dialog, null);

        TextView dialogTrackCode = (TextView) dialogView.findViewById(R.id.dialodTrackCode);
        final TextView dialogDescription = (EditText) dialogView.findViewById(R.id.edDescription);
        dialogTrackCode.setText(trackCode);
        final Activity activity = this;
        saveDialog = new AlertDialog.Builder(this)
                .setTitle("Enter description")
                .setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        info.setDescription(dialogDescription.getText().toString());
                        dbHelper.open();
                        boolean result = dbHelper.addTrackCode(info);
                        if (result) {
                            Toast.makeText(activity, getString(R.string.code_saved), Toast.LENGTH_LONG).show();
                            activity.findViewById(R.id.btnSaveCode).setEnabled(false);
                        } else {
                            Toast.makeText(activity, getString(R.string.code_save_failed), Toast.LENGTH_LONG).show();
                        }
                        dbHelper.close();
                    }
                })
                .setNegativeButton("Cancel", null).create();
    }

    private void displayInfo(BarcodeInfo codeInfo) {
        if (codeInfo == null) {
            Toast.makeText(this, getString(R.string.noDataReceived), Toast.LENGTH_LONG).show();
            return;
        }
        TextView tvTrackCode = (TextView) findViewById(R.id.tvTrackCode);
        tvTrackCode.setText(getString(R.string.barcode) + " " + codeInfo.getBarcode());
        TextView tvDescription = (TextView) findViewById(R.id.tvDescr);
        tvDescription.setText(getString(R.string.description) + " " + codeInfo.getEventDescription());
        TextView tvLastOffice = (TextView) findViewById(R.id.tvLastoffice);
        tvLastOffice.setText(getString(R.string.lastOffice) + " " + codeInfo.getLastOffice());
        TextView tvLastIndex = (TextView) findViewById(R.id.tvLastindex);
        tvLastIndex.setText(getString(R.string.lastIndex) + " " + codeInfo.getLastOfficeIndex());
//        TextView tvCode = (TextView) findViewById(R.id.tvCode);
//        tvCode.setText(getString(R.string.code) + " " + info.getCode());
        TextView tvDate = (TextView) findViewById(R.id.tvInfodate);
        tvDate.setText(getString(R.string.date) + " " + codeInfo.getEventDate());
        TextView tvLastCheck = (TextView) findViewById(R.id.tvLastCheck);
        tvLastCheck.setText(getString(R.string.lastcheck) + " " + codeInfo.getLastCheck());
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
    public void onInfoReceived(BarcodeInfo newInfo) {
        progress = (ProgressBar) findViewById(R.id.pbTracking);
        progress.setVisibility(View.GONE);
        if (newInfo == null) {
            Toast.makeText(this, R.string.noDataReceived, Toast.LENGTH_LONG).show();
            return;
        }
        if (!info.getCode().equals(newInfo.getCode())) { // status changed
            info.setCode(newInfo.getCode());
            info.setEventDate(newInfo.getEventDate());
            info.setLastOfficeIndex(newInfo.getLastOfficeIndex());
            info.setLastOffice(newInfo.getLastOffice());
            info.setEventDescription(newInfo.getEventDescription());
        }
        info.setLastCheck(newInfo.getLastCheck());
        displayInfo(info);
        if (codeInDB) {
            dbHelper.open();
            dbHelper.updateTrackInfo(info);
            dbHelper.close();
        }
    }

    public void retrack(View v) {
        ProgressBar progress = (ProgressBar) findViewById(R.id.pbTracking);
        progress.setVisibility(View.VISIBLE);
        new TrackTask(trackCode, lang, this).execute();
    }

    public void saveCodeDialog(View v) {
        saveDialog.show();
    }
}
