package com.example.reservasdeportes.controller;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueManager {
    private static RequestQueueManager instance;
    private static Context context;
    private RequestQueue requestQueue;

    private RequestQueueManager(Context ctx) {
        context = ctx;
        requestQueue = getRequestQueue();
    }

    public static RequestQueueManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new RequestQueueManager(ctx);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }
}
