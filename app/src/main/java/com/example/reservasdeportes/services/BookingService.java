package com.example.reservasdeportes.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.reservasdeportes.controller.RequestQueueManager;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.ui.booking.BookingDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class BookingService {

    private final String URL = "http://10.0.2.2:8080/api/booking";

    public void saveAppointment(Context context, String tag, BookingDTO bookingDTO, ServerCallback serverCallback) {

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
            bookingData.put("payed", bookingDTO.isPayed());

            String requestBody = bookingData.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, null,
                    response -> {
                        try {
                            serverCallback.onSuccess(response.getJSONObject("bookingResult"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> serverCallback.onError("ERROR: " + error)){

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody.getBytes(StandardCharsets.UTF_8);
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }
            };

            jsonObjectRequest.setTag(tag);
            RequestQueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReservedDates(Context context, String tag, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/getDates";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, null,
                    response -> {
                        try {
                            serverCallback.onSuccess(response.getJSONObject("bookingResult"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> serverCallback.onError("ERROR: " + error)){

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            jsonObjectRequest.setTag(tag);
            RequestQueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReservedTimes(Context context, String tag, int[] selectedDate, ServerCallback serverCallback) {
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

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, null,
                    response -> {
                        try {
                            serverCallback.onSuccess(response.getJSONObject("bookingResult"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> serverCallback.onError("ERROR: " + error)){

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody.getBytes(StandardCharsets.UTF_8);
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }
            };

            jsonObjectRequest.setTag(tag);
            RequestQueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAllByUser(Context context, String tag, String userId, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/getAllByUser";

            JSONObject bookingData = new JSONObject();
            bookingData.put("userId", userId);

            String requestBody = bookingData.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, null,
                    response -> {
                        try {
                            serverCallback.onSuccess(response.getJSONObject("bookingResult"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> serverCallback.onError("ERROR: " + error)){

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody.getBytes(StandardCharsets.UTF_8);
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }
            };

            jsonObjectRequest.setTag(tag);
            RequestQueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Context context, String tag, String bookingId, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/deleteById";

            JSONObject bookingData = new JSONObject();
            bookingData.put("_id", bookingId);

            String requestBody = bookingData.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, null,
                    response -> {
                        try {
                            serverCallback.onSuccess(response.getJSONObject("bookingResult"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> serverCallback.onError("ERROR: " + error)){

                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }

                @Override
                public byte[] getBody() {
                    try {
                        return requestBody.getBytes(StandardCharsets.UTF_8);
                    } catch (Exception uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s",
                                requestBody, "utf-8");
                        return null;
                    }
                }
            };

            jsonObjectRequest.setTag(tag);
            RequestQueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
