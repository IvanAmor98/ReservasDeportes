package com.example.reservasdeportes.ui.booking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.BookingListActivityBinding;
import com.example.reservasdeportes.model.BookingDTO;
import com.example.reservasdeportes.model.FacilityTypes;
import com.example.reservasdeportes.services.BookingService;
import com.example.reservasdeportes.model.LoggedUserDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingListActivity extends AppCompatActivity {

    private final String TAG = BookingListActivity.class.toString();
    private final BookingService bookingService = new BookingService();

    private final ArrayList<BookingDTO> bookings = new ArrayList<>();
    private BookingListAdapter adapter;
    protected LoggedUserDTO loggedUserDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BookingListActivityBinding binding = BookingListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loggedUserDTO = getIntent().getParcelableExtra("loggedUserDTO");

        adapter = new BookingListAdapter(this, R.layout.booking_list_row, bookings);
        ListView lvBookings = binding.bookingList;
        lvBookings.setAdapter(adapter);

        updateItems();
    }

    private void updateItems() {
        bookings.clear();
        bookingService.getAllByUser(this, TAG, loggedUserDTO.getToken(), loggedUserDTO.getId(), new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jArray = result.getJSONArray("bookings");
                    for (int i = 0; i < jArray.length(); i++){
                        JSONObject object = jArray.getJSONObject(i);
                        Calendar from = Calendar.getInstance();
                        Calendar to = Calendar.getInstance();
                        from.setTimeInMillis(object.getLong("timeFrom"));
                        to.setTimeInMillis(object.getLong("timeTo"));
                        bookings.add(new BookingDTO(
                                object.getString("_id"),
                                object.getString("user"),
                                object.getString("facility"),
                                object.getString("facilityName"),
                                FacilityTypes.values()[object.getInt("type")],
                                new int[]{from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH)},
                                from.get(Calendar.HOUR_OF_DAY),
                                to.get(Calendar.HOUR_OF_DAY),
                                object.getBoolean("paid")
                        ));
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(BookingListActivity.this, "ERROR: " + error, Toast.LENGTH_LONG).show();
            }

        });
    }
}