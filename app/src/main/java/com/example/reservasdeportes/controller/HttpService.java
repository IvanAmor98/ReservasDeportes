package com.example.reservasdeportes.controller;

import android.content.Context;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpService {

    //private static final String URL = "http://ec2-18-168-204-141.eu-west-2.compute.amazonaws.com/api";
    private static final String URL = "http://10.0.2.2:8080/api";

    public static void addPetition(Context context, String TAG, @Nullable String token, String endpoint, String requestBody, ServerCallback serverCallback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL + endpoint, null,
                response -> {
                    try {
                        serverCallback.onSuccess(response.getJSONObject("result"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> serverCallback.onError("ERROR: " + error)){

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                if (token != null) headers.put("authorization", "Bearer " + token);
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

        jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 5;
            }

            @Override
            public void retry(VolleyError error) {
                error.printStackTrace();
            }
        });

        jsonObjectRequest.setTag(TAG);
        RequestQueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);

    }

    public static void cancelRequest(Context context, String TAG) {
        RequestQueueManager.getInstance(context).getRequestQueue().cancelAll(TAG);
    }
}
