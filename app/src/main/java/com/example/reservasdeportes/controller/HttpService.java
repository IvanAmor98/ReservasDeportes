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

    //Direccion del servidor
    //private static final String URL = "http://ec2-18-168-204-141.eu-west-2.compute.amazonaws.com/api";
    //Server Juan
    //private static final String URL = "http://ec2-18-170-117-140.eu-west-2.compute.amazonaws.com/api";
    private static final String URL = "http://10.0.2.2:8080/api";
    //private static final String URL = "http://ivansote.ddns.net:8080/api";

    //Añade una peticion a la cola con los parametros dados
    public static void addPetition(Context context, String TAG, @Nullable String token, String endpoint, String requestBody, ServerCallback serverCallback) {
        //Crea la peticion con metodo post
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL + endpoint, null,
                //Asigna el callback que se ejecutara al recibir una respuesta
                response -> {
                    try {
                        serverCallback.onSuccess(response.getJSONObject("result"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                //Asigna el callback que se ejecutara al ocurrir un error
                error -> serverCallback.onError("ERROR: " + error)){

            //Asigna los headers necesarios mas el header del token de atentificacion
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                //Si hay token lo añade
                if (token != null) headers.put("authorization", "Bearer " + token);
                return headers;
            }

            //Añade el cuerpo de la peticion
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
        //Añade la peticion generada a la cola para que sea ejecutada
        RequestQueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest);

        //Asigna reintentos y timepo de espera en caso de que la peticion falle
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

    }

    //Cancela todos los request asignados a un TAG especifico (si aun siguen en la cola)
    public static void cancelRequest(Context context, String TAG) {
        RequestQueueManager.getInstance(context).getRequestQueue().cancelAll(TAG);
    }
}
