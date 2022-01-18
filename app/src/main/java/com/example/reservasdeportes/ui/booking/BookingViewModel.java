package com.example.reservasdeportes.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.reservasdeportes.R;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingViewModel {
    private final String TAG = BookingViewModel.class.toString();
    private final MutableLiveData<BookingFormState> bookingFormState = new MutableLiveData<>();

    LiveData<BookingFormState> getBookingFormState() {
        return bookingFormState;
    }

    public void bookingDateChanged(String date, ArrayList<ReservedTime> reservedTimes) {
        Integer checkDate = hasAvailableTimes(reservedTimes) ? null : R.string.no_bookings_available;

        if (checkDate != null) {
            bookingFormState.setValue(new BookingFormState(checkDate, null, null));
        } else {
            bookingFormState.setValue(new BookingFormState(true, false, false));
        }
    }

    private boolean hasAvailableTimes(ArrayList<ReservedTime> reservedTimes) {
        if (reservedTimes.size() == 0)
            return true;

        Calendar calendarStart = Calendar.getInstance();

        Calendar calendarEnd = Calendar.getInstance();
        calendarEnd.set(Calendar.HOUR_OF_DAY, 21);
        calendarEnd.set(Calendar.MINUTE, 0);

        for (ReservedTime reservedTime : reservedTimes) {
            Calendar calendarReservedStartTime = Calendar.getInstance();
            calendarReservedStartTime.set(Calendar.HOUR_OF_DAY, reservedTime.getTimeFrom()[0]);
            calendarReservedStartTime.set(Calendar.MINUTE, reservedTime.getTimeFrom()[1]);

            calendarStart.add(Calendar.MINUTE, 30);
            if (calendarStart.compareTo(calendarReservedStartTime) <= 0) {
                return true;
            } else {
                calendarStart.set(Calendar.HOUR_OF_DAY, reservedTime.getTimeTo()[0]);
                calendarStart.set(Calendar.MINUTE, reservedTime.getTimeTo()[1]);
            }
        }

        calendarStart.add(Calendar.MINUTE, 30);
        return calendarStart.compareTo(calendarEnd) <= 0;
    }

    public void bookingTimeFromChanged(String timeFrom) {
        if (timeFrom != null && !timeFrom.equals(""))
            bookingFormState.setValue(new BookingFormState(true, true, false));
    }

    public void bookingTimeToChanged(String timeTo) {
        if (timeTo != null && !timeTo.equals(""))
            bookingFormState.setValue(new BookingFormState(true, true, true));
    }
}
