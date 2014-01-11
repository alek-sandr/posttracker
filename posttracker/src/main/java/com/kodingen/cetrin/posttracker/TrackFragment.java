package com.kodingen.cetrin.posttracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TrackFragment extends Fragment implements View.OnClickListener {
    private EditText edTrackCode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        edTrackCode = (EditText) v.findViewById(R.id.trackCode);
        edTrackCode.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    onClick(v);
                    return true;
                }
                return false;
            }
        });
        Button btnTrack = (Button) v.findViewById(R.id.btnTrack);
        btnTrack.setOnClickListener(this);
        Button btnNotif = (Button) v.findViewById(R.id.btnNotif);
        btnNotif.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnTrack:
                String trackCode = edTrackCode.getText().toString().toUpperCase();
                if (trackCode.length() != 13) {
                    Toast.makeText(getActivity(), getString(R.string.wrongTrackCode), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(TrackCodeInfo.ACTION_TRACKANDSHOW);
                intent.putExtra(TrackCodeInfo.TRACKCODE, trackCode);
                startActivity(intent);
                break;
            case R.id.btnNotif:
                Intent intent1 = new Intent(getActivity(), TrackService.class);
                Toast.makeText(getActivity(), "start serv", Toast.LENGTH_LONG).show();
                getActivity().startService(intent1);
                break;
        }

    }
}
