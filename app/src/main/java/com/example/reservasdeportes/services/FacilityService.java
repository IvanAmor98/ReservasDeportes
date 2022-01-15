package com.example.reservasdeportes.services;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.reservasdeportes.controller.RequestQueueManager;
import com.example.reservasdeportes.controller.ServerCallback;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class FacilityService {

    private final String URL = "http://10.0.2.2:8080/api/facility";

    public void getFacilityById(Context context, String tag, String facilityId, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/getById";

            JSONObject loginData = new JSONObject();
            loginData.put("id", facilityId);

            String requestBody = loginData.toString();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, null,
                    response -> serverCallback.onSuccess(response),
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

    public void getFacilityList(Context context, String tag, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/list";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endpoint, null,
                    response -> serverCallback.onSuccess(response),
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

    public static void cancelRequest(Context context, String TAG) {
        RequestQueueManager.getInstance(context).getRequestQueue().cancelAll(TAG);
    }
}
