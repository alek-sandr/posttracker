package com.kodingen.cetrin.posttracker;

public class BarcodeInfo {

    private String barcode;
    private String description;
    private String code;
    private String lastOfficeIndex;
    private String eventDate;
    private String lastOffice;
    private String eventDescription;
    private String lastCheck;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String value) {
        this.barcode = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String value) {
        this.code = value;
    }

    public String getLastOfficeIndex() {
        return lastOfficeIndex;
    }

    public void setLastOfficeIndex(String value) {
        this.lastOfficeIndex = value;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String value) {
        this.eventDate = value;
    }

    public String getLastOffice() {
        return lastOffice;
    }

    public void setLastOffice(String value) {
        this.lastOffice = value;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String value) {
        this.eventDescription = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLastCheck(String date) {
        this.lastCheck = date;
    }

    public String getLastCheck() {
        return lastCheck;
    }
}
