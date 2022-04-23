package com.example.reservasdeportes.ui.facility;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.reservasdeportes.model.FacilityDTO;
import com.example.reservasdeportes.ui.MapsFragment;
import com.example.reservasdeportes.R;
import com.example.reservasdeportes.ui.booking.BookingActivity;

import java.util.List;

public class FacilityListAdapter extends ArrayAdapter<FacilityDTO> {

    private final int resourceLayout;
    private final Context mContext;

    public FacilityListAdapter(Context context, int resource, List<FacilityDTO> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
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
                    Intent intent = new Intent(mContext, BookingActivity.class);
                    intent.putExtra("facilityDTO", facilityDTO);
                    intent.putExtra("loggedUserDTO", ((FacilityListActivity)mContext).loggedUserDTO);
                    mContext.startActivity(intent);
                });
            }

            if (btnFind != null) {
                btnFind.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, MapsFragment.class);
                    intent.putExtra("facilityDTO", facilityDTO);
                    mContext.startActivity(intent);
                });
            }
        }
        return view;
    }
}
