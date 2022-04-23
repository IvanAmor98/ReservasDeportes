package com.example.reservasdeportes.services;

import android.content.Context;

import com.example.reservasdeportes.controller.HttpService;
import com.example.reservasdeportes.controller.ServerCallback;

import org.json.JSONObject;

//Clase que maneja peticiones relacionadas con las instalaciones
public class FacilityService {

    //Ruta por defecto
    private final String URL = "/facility";

    //Obtiene la lista de todas las instalaciones
    public void getFacilityList(Context context, String TAG, String token, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/list";

            //Crea un objeto JSON con los datos de la peticion
            JSONObject loginData = new JSONObject();
            loginData.put("sender", TAG);

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = loginData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
