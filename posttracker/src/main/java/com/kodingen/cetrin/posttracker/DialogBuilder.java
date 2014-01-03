package com.kodingen.cetrin.posttracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DialogBuilder {
    static AlertDialog getEditDialog(final Context ctx, final BarcodeInfo codeInfo,
                                     final DialogResultReceiver drs, final Executor<BarcodeInfo> ex) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        final View dialogView = inflater.inflate(R.layout.save_code_dialog, null);
        TextView dialogTrackCode = (TextView) dialogView.findViewById(R.id.dialodTrackCode);
        dialogTrackCode.setText(codeInfo.getBarcode());
        final EditText description = (EditText) dialogView.findViewById(R.id.edDescription);
        description.setText(codeInfo.getDescription());
        final EditText sendDate = (EditText) dialogView.findViewById(R.id.edSendDate);
        sendDate.setText(codeInfo.getSendDateString());
        final EditText maxDays = (EditText) dialogView.findViewById(R.id.edMaxDays);
        maxDays.setText("" + codeInfo.getMaxDeliveryDays());
        final int defBackground = sendDate.getDrawingCacheBackgroundColor();
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setBackgroundColor(defBackground);
            }
        };
        sendDate.setOnClickListener(ocl);
        maxDays.setOnClickListener(ocl);

        final AlertDialog dialog = new AlertDialog.Builder(ctx)
                //.setTitle("Enter description")
                .setView(dialogView)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button posButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (posButton == null) return;
                posButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        codeInfo.setDescription(description.getText().toString());
                        String sDateString = sendDate.getText().toString();
                        long sendDateMillis = parseDateString(sDateString);
                        if (sendDateMillis < 0) {
                            sendDate.setBackgroundColor(Color.YELLOW);
                            Toast.makeText(ctx, R.string.wrong_date, Toast.LENGTH_LONG).show();
                            return;
                        }
                        codeInfo.setSendDate(sendDateMillis);
                        String mDaysString = maxDays.getText().toString();
                        int mDays;
                        if (mDaysString.equals("")) {
                            mDays = 0;
                        } else {
                            mDays = Integer.parseInt(mDaysString);
                        }
                        if (mDays < 0) {
                            maxDays.setBackgroundColor(Color.YELLOW);
                            Toast.makeText(ctx, R.string.wrong_day, Toast.LENGTH_LONG).show();
                            return;
                        }
                        codeInfo.setMaxDeliveryDays(mDays);
                        boolean result = ex.execute(codeInfo);
//                        DBHelper dbHelper = new DBHelper(ctx);
//                        dbHelper.open();
//                        boolean result = dbHelper.addTrackCode(codeInfo);
                        if (result) {
                            drs.onSuccess();
                        } else {
                            drs.onFail();
                        }
//                        dbHelper.close();
                        dialog.dismiss();
                    }
                });
            }
        });
        return dialog;
    }

    private static long parseDateString(String dateString) {
        if (dateString.equals("") || dateString.equals("0")) {
            return 0L;
        }
        Time sDate = new Time();
        String[] dateComponents = dateString.split("\\.");
        int day, month, year;
        if (dateComponents.length != 3) {
            return -1L;
        } else {
            try {
                day = Integer.parseInt(dateComponents[0]);
                month = Integer.parseInt(dateComponents[1]) - 1;
                year = Integer.parseInt(dateComponents[2]);
            } catch (NumberFormatException nfe) {
                return -1L;
            }
        }
        if (day < 1 || day > 31 || month < 0 || month > 11 || year < 0) {
            return -1L;
        }
        sDate.set(day, month, year);
        return sDate.toMillis(false);
    }
}
