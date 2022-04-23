package com.example.reservasdeportes.ui.facility;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.model.FacilityDTO;
import com.example.reservasdeportes.model.FacilityTypes;
import com.example.reservasdeportes.services.FacilityService;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.FacilityListActivityBinding;
import com.example.reservasdeportes.model.LoggedUserDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FacilityListActivity extends AppCompatActivity {

    private final String TAG = FacilityListActivity.class.toString();
    private final FacilityService facilityService = new FacilityService();

    ArrayList<FacilityDTO> facilities = new ArrayList<>();
    LoggedUserDTO loggedUserDTO;
    FacilityListAdapter adapter;
    ListView lvFacilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacilityListActivityBinding binding = FacilityListActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loggedUserDTO = getIntent().getParcelableExtra("loggedUserDTO");

        adapter = new FacilityListAdapter(this, R.layout.facility_list_row, facilities);
        lvFacilities = binding.facilityList;
        lvFacilities.setAdapter(adapter);

        facilityService.getFacilityList(this, TAG, loggedUserDTO.getToken(), new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    JSONArray jArray = result.getJSONArray("facilityListResult");
                    for (int i = 0; i < jArray.length(); i++){
                        JSONObject object = jArray.getJSONObject(i);
                        facilities.add(new FacilityDTO(
                                object.getString("_id"),
                                object.getString("name"),
                                object.getString("country"),
                                object.getString("state"),
                                jsonArrayToArray(object.getJSONArray("types")),
                                Float.parseFloat(object.getString("latitude")),
                                Float.parseFloat(object.getString("longitude")))
                        );
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(FacilityListActivity.this, "ERROR: " + error, Toast.LENGTH_LONG).show();
            }
        });

    }

    private FacilityTypes[] jsonArrayToArray(JSONArray array) {
        FacilityTypes[] facilityTypes = new FacilityTypes[array.length()];
        for (int i = 0; i < array.length(); i++) {
            try {
                facilityTypes[i] = FacilityTypes.values()[array.getInt(i)];
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return facilityTypes;
    }
}