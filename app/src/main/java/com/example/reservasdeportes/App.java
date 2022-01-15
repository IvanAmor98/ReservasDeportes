package com.example.reservasdeportes;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import androidx.annotation.RequiresApi;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

public class App extends Application {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();

        CheckoutConfig config = new CheckoutConfig(
                this,
                "Ac6oij-apUQJNymvU99naxJNkxlzqBrM5QCMrAFxGvJ5_Bn8ZZMvey4V7xHMWAPdjSTthekb1AAGEJz0",
                Environment.SANDBOX,
                String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
                CurrencyCode.EUR,
                UserAction.PAY_NOW
        );
        PayPalCheckout.setConfig(config);
    }
}
