package com.kodingen.cetrin.posttracker;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        EditText editText = (EditText) findViewById(R.id.trackCode);
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    track(v);
                    return true;
                }
                return false;
            }
        });
    }

    public void track(View v) {
        EditText edTrackCode = (EditText) findViewById(R.id.trackCode);
        String trackCode = edTrackCode.getText().toString().trim().toUpperCase();
        if (trackCode.length() != 13) {
            Toast.makeText(this, getString(R.string.wrongTrackCode), Toast.LENGTH_LONG).show();
            return;
        }
        BarcodeInfo info = null;
        try {
            info = new TrackTask().execute(trackCode, "uk").get();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } catch (ExecutionException e) {
            //e.printStackTrace();
        }
        if (info == null) {
            Toast.makeText(this, getString(R.string.noDataReceived), Toast.LENGTH_LONG).show();
            return;
        }
        TextView tvBarcode = (TextView) findViewById(R.id.tvBarcode);
        tvBarcode.setText(getString(R.string.barcode) + " " + info.getBarcode());
        TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvDescription.setText(getString(R.string.description) + " " + info.getEventDescription().trim());
        TextView tvLastOffice = (TextView) findViewById(R.id.tvLastOffice);
        tvLastOffice.setText(getString(R.string.lastOffice) + " " + info.getLastOffice());
        TextView tvLastIndex = (TextView) findViewById(R.id.tvLastIndex);
        tvLastIndex.setText(getString(R.string.lastIndex) + " " + info.getLastOfficeIndex());
//        TextView tvCode = (TextView) findViewById(R.id.tvCode);
//        tvCode.setText(getString(R.string.code) + " " + info.getCode());
        TextView tvDate = (TextView) findViewById(R.id.tvDate);
        tvDate.setText(getString(R.string.date) + " " + info.getEventDate());
    }
}
