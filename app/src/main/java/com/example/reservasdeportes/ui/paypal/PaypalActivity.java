package com.example.reservasdeportes.ui.paypal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.reservasdeportes.databinding.PaypalActivityBinding;
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

import java.util.ArrayList;

public class PaypalActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BookingDTO bookingDTO = getIntent().getParcelableExtra("bookingDTO");

        PaypalActivityBinding binding = PaypalActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        PayPalButton paymentButton = binding.payButton;

        paymentButton.setup(
                createOrderActions -> {
                    ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                    purchaseUnits.add(
                            new PurchaseUnit.Builder()
                                    .amount(
                                            new Amount.Builder()
                                                    .currencyCode(CurrencyCode.EUR)
                                                    .value("10.00")
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
                    Log.i("CaptureOrder", String.format("CaptureOrderResult: %s", result));
                }), null,
                () -> Log.d("OnCancel", "Buyer cancelled the PayPal experience."),
                errorInfo -> Log.d("OnError", String.format("Error: %s", errorInfo))

        );
    }
}