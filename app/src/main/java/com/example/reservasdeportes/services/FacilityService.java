package com.example.reservasdeportes.services;

import android.content.Context;

import com.example.reservasdeportes.controller.HttpService;
import com.example.reservasdeportes.controller.ServerCallback;

import org.json.JSONObject;

public class FacilityService {

    private final String URL = "/facility";

    public void getFacilityById(Context context, String TAG, String facilityId, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/getById";

            JSONObject loginData = new JSONObject();
            loginData.put("id", facilityId);

            String requestBody = loginData.toString();

            HttpService.addPetition(context, TAG, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFacilityList(Context context, String TAG, ServerCallback serverCallback) {

        try {
            String endpoint = URL + "/list";

            JSONObject loginData = new JSONObject();
            loginData.put("sender", TAG);
            String requestBody = loginData.toString();

            HttpService.addPetition(context, TAG, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
