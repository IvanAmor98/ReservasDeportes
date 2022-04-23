package com.example.reservasdeportes.controller;

import org.json.JSONObject;

//Callback personalizado para las peticiones HTTP
public interface ServerCallback {
    void onSuccess(JSONObject result);
    void onError(String error);
}
