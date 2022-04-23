package com.example.reservasdeportes.services;

import android.content.Context;

import com.example.reservasdeportes.controller.HttpService;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.model.BookingDTO;
import com.example.reservasdeportes.model.FacilityTypes;

import org.json.JSONObject;

import java.util.Calendar;

//Clase que maneja peticiones relacionadas con las reservas
public class BookingService {

    //Ruta por defecto
    private final String URL = "/booking";

    //Guarda una nueva reserva
    public void saveAppointment(Context context, String TAG, String token, BookingDTO bookingDTO, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/newBooking";

            //Pasa las fechas seleccionadas a calendar para poder obtener las fechas en milisegundos (UNIX)
            Calendar calendarTimeFrom = Calendar.getInstance();
            calendarTimeFrom.clear();
            calendarTimeFrom.set(bookingDTO.getDate()[0], bookingDTO.getDate()[1], bookingDTO.getDate()[2], bookingDTO.getTimeFrom(), 0);

            Calendar calendarTimeTo = (Calendar) calendarTimeFrom.clone();
            calendarTimeTo.set(Calendar.HOUR_OF_DAY, bookingDTO.getTimeTo());
            calendarTimeTo.set(Calendar.MINUTE, 0);

            //Crea un objeto JSON con los datos de la peticion
            JSONObject bookingData = new JSONObject();
            bookingData.put("userId", bookingDTO.getUserId());
            bookingData.put("facilityId", bookingDTO.getFacilityId());
            bookingData.put("facilityName", bookingDTO.getFacilityName());
            bookingData.put("type", bookingDTO.getType().getValue());
            bookingData.put("timeFrom", calendarTimeFrom.getTimeInMillis());
            bookingData.put("timeTo", calendarTimeTo.getTimeInMillis());
            bookingData.put("paid", bookingDTO.isPaid());

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = bookingData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Obtiene todas las reservas del dia dado
    public void getReservedTimes(Context context, String TAG, String token, int[] selectedDate, FacilityTypes selectedType, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/getReservedTimes";

            //Pasa las fechas seleccionadas a calendar para poder obtener las fechas en milisegundos (UNIX)
            Calendar from = Calendar.getInstance();
            from.clear();
            from.set(selectedDate[0], selectedDate[1], selectedDate[2], 9, 0, 0);

            Calendar to = Calendar.getInstance();
            to.clear();
            to.set(selectedDate[0], selectedDate[1], selectedDate[2], 23, 0, 0);

            //Crea un objeto JSON con los datos de la peticion
            JSONObject bookingData = new JSONObject();
            bookingData.put("type", selectedType.getValue());
            bookingData.put("from", from.getTimeInMillis());
            bookingData.put("to", to.getTimeInMillis());

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = bookingData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Obtiene todas las reservas de un usuario
    public void getAllByUser(Context context, String TAG, String token, String userId, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/getAllByUser";

            //Crea un objeto JSON con los datos de la peticion
            JSONObject bookingData = new JSONObject();
            bookingData.put("userId", userId);

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = bookingData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Borra una reserva con el id dado
    public void deleteById(Context context, String TAG, String token, String bookingId, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/deleteById";

            //Crea un objeto JSON con los datos de la peticion
            JSONObject bookingData = new JSONObject();
            bookingData.put("_id", bookingId);

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = bookingData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Actualiza una reserva con el id dado como pagada
    public void updatePaidById(Context context, String TAG, String token, String bookingId, ServerCallback serverCallback) {
        try {
            //Asigna la ruta del endpoint
            String endpoint = URL + "/updatePaidById";

            //Crea un objeto JSON con los datos de la peticion
            JSONObject bookingData = new JSONObject();
            bookingData.put("_id", bookingId);
            bookingData.put("paid", true);

            //Pasa el JSON a string para que pueda ser enviado
            String requestBody = bookingData.toString();

            //Añade la peticion mediante el gestor de peticiones
            HttpService.addPetition(context, TAG, token, endpoint, requestBody, serverCallback);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
