package com.example.reservasdeportes.ui.booking;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.model.BookingDTO;
import com.example.reservasdeportes.services.BookingService;
import com.example.reservasdeportes.ui.paypal.PaypalActivity;


import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class BookingListAdapter extends ArrayAdapter<BookingDTO> {

    private final String TAG = BookingListAdapter.class.toString();
    private final int resourceLayout;
    private final Context mContext;
    private final List<BookingDTO> items;
    private BookingDTO paying;

    private final ActivityResultLauncher<Intent> startForResult;

    public BookingListAdapter(Context context, int resource, List<BookingDTO> items) {
        super(context, resource, items);
        this.resourceLayout = resource;
        this.mContext = context;
        this.items = items;

        this.startForResult = ((AppCompatActivity)mContext).registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK)
            paying.setPaid(true);
            notifyDataSetChanged();
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(resourceLayout, null);
        }

        BookingDTO bookingDTO = getItem(position);

        if (bookingDTO != null) {
            TextView facilityName = view.findViewById(R.id.listFacilityName);
            TextView bookingDate = view.findViewById(R.id.listBookingDate);
            TextView timeFrom = view.findViewById(R.id.listTimeFrom);
            TextView timeTo = view.findViewById(R.id.listTimeTo);
            Button btnPay = view.findViewById(R.id.payButton);
            Button btnQR = view.findViewById(R.id.listRowQR);
            Button btnDelete = view.findViewById(R.id.listRowDelete);

            if (facilityName != null) { facilityName.setText(bookingDTO.getFacilityName()); }

            if (bookingDate != null) {
                bookingDate.setText(String.format(Locale.getDefault(), "%02d/%02d/%02d",
                    bookingDTO.getDate()[2],
                    bookingDTO.getDate()[1] + 1,
                    bookingDTO.getDate()[0]));
            }

            if (timeFrom != null) { timeFrom.setText(String.format(Locale.getDefault(), getContext().getString(R.string.from) +": %02d:%02d",
                    bookingDTO.getTimeFrom(),
                    0)); }

            if (timeTo != null) { timeTo.setText(String.format(Locale.getDefault(), getContext().getString(R.string.to) + ": %02d:%02d",
                    bookingDTO.getTimeTo(),
                    0)); }

            if (btnPay != null) {
                if (bookingDTO.isPaid()) {
                    btnPay.setVisibility(View.GONE);
                    btnDelete.setVisibility(View.GONE);
                    btnQR.setVisibility(View.VISIBLE);
                } else {
                    btnPay.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.VISIBLE);
                    btnQR.setVisibility(View.GONE);
                    btnPay.setOnClickListener(v -> {

                        Intent intent = new Intent(mContext, PaypalActivity.class);
                        intent.putExtra("bookingDTO", bookingDTO);
                        intent.putExtra("loggedUserDTO", ((BookingListActivity)mContext).loggedUserDTO);
                        paying = bookingDTO;
                        this.startForResult.launch(intent);
                    });
                }
            }

            if (btnQR != null) {
                btnQR.setOnClickListener(v -> {
                    Intent intent = new Intent(mContext, BookingQRActivity.class);
                    intent.putExtra("bookingDTO", bookingDTO);
                    mContext.startActivity(intent);
                });
            }

            if (btnDelete != null) {
                btnDelete.setOnClickListener(v ->
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.alert_title)
                            .setMessage(R.string.alert_delete)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                                new BookingService().deleteById(mContext, TAG, ((BookingListActivity)mContext).loggedUserDTO.getToken(), bookingDTO.getId(), new ServerCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        Toast.makeText(mContext, R.string.booking_delete_success, Toast.LENGTH_LONG).show();
                                        items.remove(bookingDTO);
                                        notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onError(String error) {
                                        Toast.makeText(mContext, "ERROR:" + error, Toast.LENGTH_LONG).show();
                                    }
                                }))
                            .setNegativeButton(android.R.string.no, null).show()
                );
            }

        }
        return view;
    }

}