package com.example.reservasdeportes.ui.paypal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.PaypalActivityBinding;
import com.example.reservasdeportes.services.BookingService;
import com.example.reservasdeportes.ui.booking.BookingDTO;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.ProcessingInstruction;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PayPalButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class PaypalActivity extends AppCompatActivity implements ServerCallback {

    private final String TAG = PaypalActivity.class.toString();
    private final float PRICE_FIVE_MINUTES = 0.5F;
    private BookingDTO bookingDTO;
    private BookingService bookingService;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PaypalActivityBinding binding = PaypalActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookingService = new BookingService();
        bookingDTO = getIntent().getParcelableExtra("bookingDTO");

        binding.detailStart.setText(String.format(Locale.getDefault(), "%d:%d", bookingDTO.getTimeFrom()[0], bookingDTO.getTimeFrom()[1]));
        binding.detailEnd.setText(String.format(Locale.getDefault(), "%d:%d", bookingDTO.getTimeTo()[0], bookingDTO.getTimeTo()[1]));

        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.clear();
        Calendar calendarTo = Calendar.getInstance();
        calendarTo.clear();

        calendarFrom.set(Calendar.HOUR_OF_DAY, bookingDTO.getTimeFrom()[0]);
        calendarFrom.set(Calendar.MINUTE, bookingDTO.getTimeFrom()[1]);

        calendarTo.set(Calendar.HOUR_OF_DAY, bookingDTO.getTimeTo()[0]);
        calendarTo.set(Calendar.MINUTE, bookingDTO.getTimeTo()[1]);

        int count = 0;
        while (calendarFrom.compareTo(calendarTo) != 0) {
            calendarFrom.add(Calendar.MINUTE, 5);
            count++;
        }
        float price = count * PRICE_FIVE_MINUTES;

        binding.detailPrice.setText(String.format(Locale.getDefault(), "%f€", price));

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
                    bookingService.updatePaidById(this, TAG, bookingDTO.getId(), this);
                }), null,
                () -> Log.d("OnCancel", "Buyer cancelled the PayPal experience."),
                errorInfo -> Log.d("OnError", String.format("Error: %s", errorInfo))

        );


    }

    @Override
    public void onSuccess(JSONObject result) {
        Toast.makeText(PaypalActivity.this, "Pago realizado correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(String error) {
        Toast.makeText(PaypalActivity.this, "ERROR: " + error, Toast.LENGTH_SHORT).show();
        bookingService.updatePaidById(this, TAG, bookingDTO.getId(), this);
    }
}