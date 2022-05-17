package com.example.reservasdeportes;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

//Clase que configura la aplicacion
public class App extends Application {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();

        //Crea la configuracion para el correcto funcionamiento del api de paypal
        CheckoutConfig config = new CheckoutConfig(
                this,
                getString(R.string.paypal_api_key),
                Environment.SANDBOX,
                String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
                CurrencyCode.EUR,
                UserAction.PAY_NOW
        );
        PayPalCheckout.setConfig(config);
    }
}
