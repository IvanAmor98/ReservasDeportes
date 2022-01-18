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

public class NotificationService extends Service {
    @Override
    public void onCreate() {
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Channel1")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentTitle("Reserva")
                .setContentText("Su reserva empieza en 30 minutos")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(R.drawable.mdtp_done_background_color, "Open", pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1000, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }
}