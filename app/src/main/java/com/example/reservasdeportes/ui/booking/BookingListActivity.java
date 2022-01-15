package com.example.reservasdeportes.ui.booking;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.BookingListActivityBinding;
import com.example.reservasdeportes.services.BookingService;
import com.example.reservasdeportes.ui.facility.FacilityDTO;
import com.example.reservasdeportes.ui.facility.FacilityListActivity;
import com.example.reservasdeportes.ui.login.LoggedUserData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class BookingListActivity extends AppCompatActivity {

    private final String TAG = BookingListActivity.class.toString();
    private final BookingService bookingService = new BookingService();

    private BookingListActivityBinding binding;

    ArrayList<BookingDTO> bookings = new ArrayList<>();
    BookingListAdapter adapter;
    ListView lvBookings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = BookingListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoggedUserData loggedUserData = getIntent().getParcelableExtra("loggedUserData");

        adapter = new BookingListAdapter(this, R.layout.booking_list_row, bookings);
        lvBookings = binding.bookingList;
        lvBookings.setAdapter(adapter);

        bookingService.getAllByUser(this, TAG, loggedUserData.getId(), new ServerCallback() {
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
                                new int[]{from.get(Calendar.YEAR), from.get(Calendar.MONTH), from.get(Calendar.DAY_OF_MONTH)},
                                new int[]{from.get(Calendar.HOUR_OF_DAY), from.get(Calendar.MINUTE)},
                                new int[]{to.get(Calendar.HOUR_OF_DAY), to.get(Calendar.MINUTE)},
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