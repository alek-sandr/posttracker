package com.kodingen.cetrin.posttracker;

import android.text.format.Time;
import android.widget.TextView;

public class BarcodeInfo {

    private String barcode = "";
    private String description = "";
    private String code = "0";
    private String lastOfficeIndex = "";
    private String eventDate = "";
    private String lastOffice = "";
    private String eventDescription = "";
    private String lastCheck = "";
    private long sendDate;
    private int maxDeliveryDays;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String value) {
        this.barcode = value != null ? value.trim().toUpperCase() : "";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value != null ? value.trim() : "0";
    }

    public String getLastOfficeIndex() {
        return lastOfficeIndex;
    }

    public void setLastOfficeIndex(String value) {
        this.lastOfficeIndex = value != null ? value.trim() : "";
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String value) {
        this.eventDate = value != null ? value.trim() : "";
    }

    public String getLastOffice() {
        return lastOffice;
    }

    public void setLastOffice(String value) {
        this.lastOffice = value != null ? value.trim() : "";
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String value) {
        this.eventDescription = value != null ? value.trim() : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description != null ? description.trim() : "";
    }

    public void setLastCheck(String date) {
        this.lastCheck = date != null ? date.trim() : "";
    }

    public String getLastCheck() {
        return lastCheck;
    }

    public int getMaxDeliveryDays() {
        return maxDeliveryDays;
    }

    public void setMaxDeliveryDays(int maxDeliveryDays) {
        this.maxDeliveryDays = maxDeliveryDays;
    }

    public long getSendDate() {
        return sendDate;
    }

    public String getSendDateString() {
        if (sendDate == 0) {
            return "0";
        }
        Time date = new Time();
        date.set(sendDate);
        return date.format("%d.%M.%Y");
    }

    public void setSendDate(Long milliseconds) {
        this.sendDate = milliseconds;
    }

    public int daysToDeadline() {
        Time now = new Time();
        now.setToNow();
        return (int) (sendDate + maxDeliveryDays * 86400000 - now.toMillis(true)) / 86400000;
    }
}
