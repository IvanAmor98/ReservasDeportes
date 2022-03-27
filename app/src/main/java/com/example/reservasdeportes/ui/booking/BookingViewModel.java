package com.example.reservasdeportes.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.reservasdeportes.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class BookingViewModel {
    private final String TAG = BookingViewModel.class.toString();
    private final MutableLiveData<BookingFormState> bookingFormState = new MutableLiveData<>();
    private final int timesLength;


    public BookingViewModel(int timesLength) {
        this.timesLength = timesLength;
        bookingFormState.setValue(new BookingFormState(false, false, false));
    }

    LiveData<BookingFormState> getBookingFormState() { return bookingFormState; }

    public void bookingDateChanged(String date, String[] availableTimes) {
        Integer checkDate = hasAvailableTimes(availableTimes) ? null : R.string.no_bookings_available;

        if (checkDate != null) {
            bookingFormState.setValue(new BookingFormState(checkDate, null, null));
        } else {
            bookingFormState.setValue(new BookingFormState(true, false, false));
        }
    }

    public void timeFromChanged(String selectedItem) {
        if (!selectedItem.equals(""))
            bookingFormState.setValue(new BookingFormState(true, true, false));
    }

    public void timeToChanged(String selectedItem) {
        if (!selectedItem.equals(""))
            bookingFormState.setValue(new BookingFormState(true, true, true));
    }

    private boolean hasAvailableTimes(String[] availableTimes) {
        return availableTimes.length > 1;
    }

}
