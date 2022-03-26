package com.example.reservasdeportes.ui.booking;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class ReservedTime {
    private final int timeFrom;
    private int timeTo;

    public ReservedTime(int timeFrom) {
        this.timeFrom = timeFrom;
    }

    public ReservedTime(int timeFrom, int timeTo) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    public int getTimeFrom() { return timeFrom; }

    public int getTimeTo() { return timeTo; }

    public void setTimeTo(int timeTo) { this.timeTo = timeTo; }
}
