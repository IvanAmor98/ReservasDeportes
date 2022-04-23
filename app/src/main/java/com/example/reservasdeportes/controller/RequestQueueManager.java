package com.example.reservasdeportes.controller;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

//Singleton que gestiona la cola de peticiones HTTP
public class RequestQueueManager {
    @SuppressLint("StaticFieldLeak")
    private static RequestQueueManager instance;
    private final Context context;
    private RequestQueue requestQueue;

    //Contructor privado
    private RequestQueueManager(Context ctx) {
        context = ctx;
        requestQueue = getRequestQueue();
    }

    //Getter estatico, si ya hay una estancia de esta clase la devuelve, si no, crea una y la devuelve
    public static RequestQueueManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new RequestQueueManager(ctx);
        }
        return instance;
    }

    //Devuelve la cola de peticiones
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
}
