package com.example.reservasdeportes.ui.paypal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.PaypalActivityBinding;
import com.example.reservasdeportes.model.LoggedUserDTO;
import com.example.reservasdeportes.services.BookingService;
import com.example.reservasdeportes.model.BookingDTO;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.ProcessingInstruction;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PaypalActivity extends AppCompatActivity {

    private final String TAG = PaypalActivity.class.toString();
    private final float PRICE_FIVE_MINUTES = 0.5F;
    private BookingDTO bookingDTO;
    private BookingService bookingService;
    private LoggedUserDTO loggedUserDTO;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaypalActivityBinding binding = PaypalActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingService = new BookingService();
        bookingDTO = getIntent().getParcelableExtra("bookingDTO");
        loggedUserDTO = getIntent().getParcelableExtra("loggedUserDTO");

        binding.detailStart.setText(String.format(Locale.getDefault(), "%02d:%02d", bookingDTO.getTimeFrom(), 0));
        binding.detailEnd.setText(String.format(Locale.getDefault(), "%02d:%02d", bookingDTO.getTimeTo(), 0));

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.clear();
        Calendar calendarTo = Calendar.getInstance();
        calendarTo.clear();

        calendarFrom.set(Calendar.HOUR_OF_DAY, bookingDTO.getTimeFrom());
        calendarFrom.set(Calendar.MINUTE, 0);

        calendarTo.set(Calendar.HOUR_OF_DAY, bookingDTO.getTimeTo());
        calendarTo.set(Calendar.MINUTE, 0);

        int count = 0;
        while (calendarFrom.compareTo(calendarTo) != 0) {
            calendarFrom.add(Calendar.MINUTE, 5);
            count++;
        }
        float price = count * PRICE_FIVE_MINUTES;

        binding.detailPrice.setText(String.format(Locale.getDefault(), "%1$,.2f€", price));

        binding.payButton.setup(
                createOrderActions -> {
                    ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                    purchaseUnits.add(
                            new PurchaseUnit.Builder()
                                    .amount(
                                            new Amount.Builder()
                                                    .currencyCode(CurrencyCode.EUR)
                                                    .value(String.valueOf(price))
                                                    .build()
                                    )
                                    .build()
                    );
                    Order order = new Order(
                            OrderIntent.CAPTURE,
                            new AppContext.Builder()
                                    .userAction(UserAction.PAY_NOW)
                                    .build(),
                            purchaseUnits,
                            ProcessingInstruction.ORDER_COMPLETE_ON_PAYMENT_APPROVAL
                    );
                    createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                },
                approval -> approval.getOrderActions().capture(result -> {
                    bookingService.updatePaidById(this, TAG, loggedUserDTO.getToken(), bookingDTO.getId(), new ServerCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                if (result.getBoolean("updated")) {
                                    Toast.makeText(PaypalActivity.this, getString(R.string.payment_success), Toast.LENGTH_LONG).show();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(PaypalActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(PaypalActivity.this, "ERROR: " + error, Toast.LENGTH_SHORT).show();
                            bookingService.updatePaidById(PaypalActivity.this, TAG, loggedUserDTO.getToken(), bookingDTO.getId(), this);
                        }
                    });
                }), null,
                () -> Log.d("OnCancel", "Buyer cancelled the PayPal experience."),
                errorInfo -> Toast.makeText(PaypalActivity.this, "ERROR: " + errorInfo, Toast.LENGTH_SHORT).show()
        );
    }
}