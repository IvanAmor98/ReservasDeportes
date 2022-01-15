package com.example.reservasdeportes.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.reservasdeportes.controller.HttpsTrustManager;
import com.example.reservasdeportes.controller.RequestQueueManager;
import com.example.reservasdeportes.controller.ServerCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final String URL = "http://10.0.2.2:8080/api/user";

    public void login(String email, String password, Context context, String tag, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/login";

            JSONObject loginData = new JSONObject();
            loginData.put("email", email);
            loginData.put("password", password);

            String requestBody = loginData.toString();

            HttpsTrustManager.allowAllSSL();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, null,
                    response -> {
                        try {
                            serverCallback.onSuccess(response.getJSONObject("loginResult"));
                        } catch (JSONException e) {
                            Log.e("LoginRequest", e.getMessage());
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

            jsonObjectRequest.setRetryPolicy(new RetryPolicy() {
                @Override
                public int getCurrentTimeout() {
                    return 50000;
                }

                @Override
                public int getCurrentRetryCount() {
                    return 1;
                }

                @Override
                public void retry(VolleyError error) {
                    error.printStackTrace();
                }
            });

            jsonObjectRequest.setTag(tag);
            RequestQueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signup(String email, String username, String password, Context context, String tag, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/signup";

            JSONObject signupData = new JSONObject();
            signupData.put("email", email);
            signupData.put("username", username);
            signupData.put("password", password);

            String requestBody = signupData.toString();

            HttpsTrustManager.allowAllSSL();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, null,
                    response -> {
                        try {
                            serverCallback.onSuccess(response.getJSONObject("signupResult"));
                        } catch (Exception e) {
                            serverCallback.onError("ERROR: " + e.getMessage());
                        }
                    }, error -> serverCallback.onError("ERROR: " + error)){
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
