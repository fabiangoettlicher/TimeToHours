package com.fabian.timetohours.Data;

/**
 * Created by Fabian on 25.08.2017.
 */

public class TrackingEntry {
    private String date;
    private String time;
    private String message;

    public TrackingEntry(String date, String time, String message) {
        this.date = date;
        this.time = time;
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
