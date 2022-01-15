package com.example.reservasdeportes.controller;

import org.json.JSONObject;

public interface ServerCallback {
    void onSuccess(JSONObject result);
    void onError(String error);
}
