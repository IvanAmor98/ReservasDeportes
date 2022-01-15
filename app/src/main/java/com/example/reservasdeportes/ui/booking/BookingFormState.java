package com.example.reservasdeportes.ui.booking;

import androidx.annotation.Nullable;

public class BookingFormState {
    @Nullable
    private final Integer dateError;
    @Nullable
    private final Integer timeFromError;
    @Nullable
    private final Integer timeToError;
    private final boolean isDateValid;
    private final boolean isTimeFromValid;
    private final boolean isTimeToValid;

    BookingFormState(@Nullable Integer dateError,
                    @Nullable Integer timeFromError,
                     @Nullable Integer timeToError) {
        this.dateError = dateError;
        this.timeFromError = timeFromError;
        this.timeToError = timeToError;
        this.isDateValid = false;
        this.isTimeFromValid = false;
        this.isTimeToValid = false;
    }

    BookingFormState(boolean isDateValid, boolean isTimeFromValid, boolean isTimeToValid) {
        this.dateError = null;
        this.timeFromError = null;
        this.timeToError = null;
        this.isDateValid = isDateValid;
        this.isTimeFromValid = isTimeFromValid;
        this.isTimeToValid = isTimeToValid;
    }

    @Nullable
    Integer getDateError() { return dateError; }

    @Nullable
    Integer getTimeFromError() { return timeFromError; }

    @Nullable
    Integer getTimeToError() { return timeToError; }

    public boolean isDateValid() {
        return isDateValid;
    }

    public boolean isTimeFromValid() {
        return isTimeFromValid;
    }

    public boolean isTimeToValid() {
        return isTimeToValid;
    }
}
