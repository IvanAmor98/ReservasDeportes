package com.example.reservasdeportes.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.ui.login.LoginActivity;

//Clase que maneja las notificaciones de alarma
public class NotificationService extends Service {
    @Override
    public void onCreate() {
        //Crea el intent que se lanzara al pulsar el boton de abrir
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        //Crea la notificacion
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Channel1")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_text))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(R.drawable.mdtp_done_background_color, getString(R.string.button_action_open), pendingIntent);

        //Lanza la notificacion
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}