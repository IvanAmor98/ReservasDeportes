package com.example.reservasdeportes.ui.facility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reservasdeportes.model.FacilityDTO;
import com.example.reservasdeportes.ui.MapsFragment;
import com.example.reservasdeportes.R;
import com.example.reservasdeportes.ui.booking.BookingActivity;

import java.util.List;

public class FacilityListAdapter extends ArrayAdapter<FacilityDTO> {

    private final int resourceLayout;


    private final ActivityResultLauncher<Intent> startForResult;

    public FacilityListAdapter(Context context, int resource, List<FacilityDTO> items) {
        super(context, resource, items);
        this.resourceLayout = resource;

        startForResult = ((AppCompatActivity) getContext()).registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK)
                Toast.makeText(getContext(), "Reserva pagada", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(resourceLayout, null);
        }

        FacilityDTO facilityDTO = getItem(position);

        if (facilityDTO != null) {
            TextView name = view.findViewById(R.id.listViewName);
            Button btnBook = view.findViewById(R.id.listViewBook);
            Button btnFind = view.findViewById(R.id.listViewFind);

            if (name != null) {
                name.setText(facilityDTO.getName());
            }

            if (btnBook != null) {
                btnBook.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), BookingActivity.class);
                    intent.putExtra("facilityDTO", facilityDTO);
                    intent.putExtra("loggedUserDTO", ((FacilityListActivity) getContext()).loggedUserDTO);
                    this.startForResult.launch(intent);
                });
            }

            if (btnFind != null) {
                btnFind.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), MapsFragment.class);
                    intent.putExtra("facilityDTO", facilityDTO);
                    getContext().startActivity(intent);
                });
            }
        }
        return view;
    }
}
