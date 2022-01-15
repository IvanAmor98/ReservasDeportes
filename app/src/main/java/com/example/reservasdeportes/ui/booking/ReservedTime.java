package com.example.reservasdeportes.ui.booking;

import androidx.annotation.Nullable;

import java.util.Arrays;

public class ReservedTime {
    private final int[] timeFrom;
    @Nullable
    private int[] timeTo;

    public ReservedTime(int[] timeFrom, @Nullable int[] timeTo) {
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
    }

    public int[] getTimeFrom() {
        return timeFrom;
    }

    @Nullable
    public int[] getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(@Nullable int[] timeTo) {
        this.timeTo = timeTo;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) { return true; }
        if (!(o instanceof ReservedTime)) { return false; }

        ReservedTime compare = (ReservedTime) o;
        return Arrays.equals(this.timeFrom, compare.timeFrom) &&
                Arrays.equals(this.timeTo, compare.timeTo);
    }

    public int compareTimeFrom(ReservedTime compare) {
        if (Arrays.equals(this.timeFrom, compare.timeFrom)) {
            return 0;
        }
        if (this.timeFrom[0] > compare.timeFrom[0] || (this.timeFrom[0] == compare.timeFrom[0] && this.timeFrom[1] > compare.timeFrom[1])) {
            return 1;
        } else {
            return -1;
        }
    }

    public Integer compareTimeTo(ReservedTime compare) {
        if (this.timeTo != null && compare.timeTo != null) {
            if (Arrays.equals(this.timeTo, compare.timeTo)) {
                return 0;
            }
            if (this.timeTo[0] > compare.timeTo[0] || (this.timeTo[0] == compare.timeTo[0] && this.timeTo[1] > compare.timeTo[1])) {
                return 1;
            } else {
                return -1;
            }
        }
        return null;
    }

    public Integer compareTimeEndTimeStart(ReservedTime compare) {
        if (this.timeTo != null) {
            if (Arrays.equals(this.timeTo, compare.timeFrom)) {
                return 0;
            }
            if (this.timeTo[0] > compare.timeFrom[0] || (this.timeTo[0] == compare.timeFrom[0] && this.timeTo[1] > compare.timeFrom[1])) {
                return 1;
            } else {
                return -1;
            }
        }
        return null;
    }
}
