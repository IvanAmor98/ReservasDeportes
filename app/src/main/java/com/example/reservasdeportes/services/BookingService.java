package com.example.reservasdeportes.services;

import android.content.Context;

import com.example.reservasdeportes.controller.HttpService;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.ui.booking.BookingDTO;

import org.json.JSONObject;

import java.util.Calendar;

public class BookingService {

    private final String URL = "/booking";

    public void saveAppointment(Context context, String TAG, String token, BookingDTO bookingDTO, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/newBooking";

            Calendar calendarTimeFrom = Calendar.getInstance();
            calendarTimeFrom.clear();
            calendarTimeFrom.set(bookingDTO.getDate()[0], bookingDTO.getDate()[1], bookingDTO.getDate()[2], bookingDTO.getTimeFrom()[0], bookingDTO.getTimeFrom()[1]);

            Calendar calendarTimeTo = (Calendar) calendarTimeFrom.clone();
            calendarTimeTo.set(Calendar.HOUR_OF_DAY, bookingDTO.getTimeTo()[0]);
            calendarTimeTo.set(Calendar.MINUTE, bookingDTO.getTimeTo()[1]);

            JSONObject bookingData = new JSONObject();
            bookingData.put("userId", bookingDTO.getUserId());
            bookingData.put("facilityId", bookingDTO.getFacilityId());
            bookingData.put("facilityName", bookingDTO.getFacilityName());
            bookingData.put("timeFrom", calendarTimeFrom.getTimeInMillis());
            bookingData.put("timeTo", calendarTimeTo.getTimeInMillis());
            bookingData.put("paid", bookingDTO.isPaid());

            String requestBody = bookingData.toString();

            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReservedDates(Context context, String TAG, String token, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/getDates";

            JSONObject bookingData = new JSONObject();
            String requestBody = bookingData.toString();

            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReservedTimes(Context context, String TAG, String token, int[] selectedDate, ServerCallback serverCallback) {
        try {
            String endpoint = URL + "/getReservedTimes";

            Calendar from = Calendar.getInstance();
            from.clear();
            from.set(selectedDate[0], selectedDate[1], selectedDate[2], 9, 0, 0);

            Calendar to = Calendar.getInstance();
            to.clear();
            to.set(selectedDate[0], selectedDate[1], selectedDate[2], 21, 0, 0);

            JSONObject bookingData = new JSONObject();
            bookingData.put("from", from.getTimeInMillis());
            bookingData.put("to", to.getTimeInMillis());

            String requestBody = bookingData.toString();

            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllByUser(Context context, String TAG, String token, String userId, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/getAllByUser";

            JSONObject bookingData = new JSONObject();
            bookingData.put("userId", userId);

            String requestBody = bookingData.toString();

            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Context context, String TAG, String token, String bookingId, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/deleteById";

            JSONObject bookingData = new JSONObject();
            bookingData.put("_id", bookingId);

            String requestBody = bookingData.toString();

            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePaidById(Context context, String TAG, String token, String bookingId, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/updatePaidById";

            JSONObject bookingData = new JSONObject();
            bookingData.put("_id", bookingId);
            bookingData.put("paid", true);

            String requestBody = bookingData.toString();

            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
