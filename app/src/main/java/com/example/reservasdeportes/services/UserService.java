package com.example.reservasdeportes.services;

import android.content.Context;

import com.example.reservasdeportes.controller.HttpService;
import com.example.reservasdeportes.controller.ServerCallback;

import org.json.JSONObject;

public class UserService {

    private final String URL = "http://10.0.2.2:8080/api/user";

    public void login(String email, String password, Context context, String TAG, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/login";

            JSONObject loginData = new JSONObject();
            loginData.put("email", email);
            loginData.put("password", password);

            String requestBody = loginData.toString();

            HttpService.addPetition(context, TAG, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signup(String email, String username, String password, Context context, String TAG, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/signup";

            JSONObject signupData = new JSONObject();
            signupData.put("email", email);
            signupData.put("username", username);
            signupData.put("password", password);

            String requestBody = signupData.toString();

            HttpService.addPetition(context, TAG, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
