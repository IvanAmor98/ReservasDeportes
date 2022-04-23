package com.example.reservasdeportes.services;

import android.content.Context;

import com.example.reservasdeportes.controller.HttpService;
import com.example.reservasdeportes.controller.ServerCallback;

import org.json.JSONObject;

//Clase que maneja las peticiones relacionadas con los usuarios
public class UserService {

    //Ruta por defecto
    private final String URL = "/user";

    //Comprueba que las credenciales dadas sean validas
    public void login(String email, String password, Context context, String TAG, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/login";

            //Crea un objeto JSON con los datos de la peticion
            JSONObject loginData = new JSONObject();
            loginData.put("email", email);
            loginData.put("password", password);

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = loginData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, null, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Comprueba que las credenciales devueltas por google sean validas
    public void googleLogin(String googleToken, String email, String password, Context context, String TAG, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/googleLogin";

            //Crea un objeto JSON con los datos de la peticion
            JSONObject loginData = new JSONObject();
            loginData.put("googleToken", googleToken);
            loginData.put("email", email);
            loginData.put("password", password);

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = loginData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, null, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Registra un nuevo usuario
    public void signup(String email, String username, String password, Context context, String TAG, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/signup";

            //Crea un objeto JSON con los datos de la peticion
            JSONObject signupData = new JSONObject();
            signupData.put("email", email);
            signupData.put("username", username);
            signupData.put("password", password);

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = signupData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, null, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
