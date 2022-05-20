package com.example.reservasdeportes.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.model.FacilityTypes;

public class BookingViewModel {
    private final String TAG = BookingViewModel.class.toString();
    private final MutableLiveData<BookingFormState> bookingFormState = new MutableLiveData<>();
    private final int timesLength;


    public BookingViewModel(int timesLength) {
        this.timesLength = timesLength;
        bookingFormState.setValue(new BookingFormState(false, false, false, false));
    }

    LiveData<BookingFormState> getBookingFormState() { return bookingFormState; }

    public void bookingDateChanged(String date) {
        bookingFormState.setValue(new BookingFormState(true, false, false, false));
    }

    public void typeChanged(FacilityTypes type, String[] availableTimes) {
        Integer checkTimes = hasAvailableTimes(availableTimes) ? null : R.string.no_bookings_available;

        if (checkTimes != null) {
            bookingFormState.setValue(new BookingFormState(null, checkTimes, null, null));
        } else {
            bookingFormState.setValue(new BookingFormState(true, true, false, false));
        }
    }

    public void timeFromChanged(String selectedItem) {
        bookingFormState.setValue(new BookingFormState(true, true, !selectedItem.equals(""), false));
    }

    public void timeToChanged(String selectedItem) {
        bookingFormState.setValue(new BookingFormState(true, true, true, !selectedItem.equals("")));
    }

    private boolean hasAvailableTimes(String[] availableTimes) {
        return availableTimes.length > 1;
    }

}
