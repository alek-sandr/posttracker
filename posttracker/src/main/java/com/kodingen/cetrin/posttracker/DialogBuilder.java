package com.kodingen.cetrin.posttracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogBuilder {
    static AlertDialog getEditDialog(final Activity activity, final BarcodeInfo codeInfo, final DialogResultReceiver drs) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        final View dialogView = inflater.inflate(R.layout.save_code_dialog, null);
        TextView dialogTrackCode = (TextView) dialogView.findViewById(R.id.dialodTrackCode);
        dialogTrackCode.setText(codeInfo.getBarcode());
        final EditText description = (EditText) dialogView.findViewById(R.id.edDescription);
        final EditText sendDate = (EditText) dialogView.findViewById(R.id.edSendDate);
        final EditText maxDays = (EditText) dialogView.findViewById(R.id.edMaxDays);
        final int defBackground = sendDate.getDrawingCacheBackgroundColor();
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(defBackground);
            }
        };
        sendDate.setOnClickListener(ocl);
        maxDays.setOnClickListener(ocl);

        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Enter description")
                .setView(dialogView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        codeInfo.setDescription(description.getText().toString());
                        Time sDate = new Time();
                        String[] dateComponents = sendDate.getText().toString().split("\\.");
                        int day = 0, month = 0, year = 0;
                        boolean wrongDate = false;
                        try {
                            day = Integer.parseInt(dateComponents[0]);
                            month = Integer.parseInt(dateComponents[1]) - 1;
                            year = Integer.parseInt(dateComponents[2]);
                        } catch (NumberFormatException nfe) {
                            wrongDate = true;
                        }
                        if (day < 1 || day > 31 || month < 0 || month > 11 || year < 0) {
                            wrongDate = true;
                        }
                        if (wrongDate) {
                            sendDate.setBackgroundColor(Color.YELLOW);
                            Toast.makeText(activity, "Wrong date!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        sDate.set(day, month, year);
                        codeInfo.setSendDate(sDate.toMillis(false));
                        int mDays = Integer.parseInt(maxDays.getText().toString());
                        if (mDays < 0) {
                            maxDays.setBackgroundColor(Color.YELLOW);
                            Toast.makeText(activity, "Wrong days number!", Toast.LENGTH_LONG).show();
                            return;
                        }
                        codeInfo.setMaxDeliveryDays(mDays);
                        DBHelper dbHelper = new DBHelper(activity);
                        dbHelper.open();
                        boolean result = dbHelper.addTrackCode(codeInfo);
                        if (result) {
                            drs.onSuccess();
                        } else {
                            drs.onFail();
                        }
                        dbHelper.close();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        return dialog;
    }
}
