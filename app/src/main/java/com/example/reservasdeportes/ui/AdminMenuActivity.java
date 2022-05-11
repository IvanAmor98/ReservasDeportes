package com.example.reservasdeportes.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.reservasdeportes.controller.QrCodeScannerActivity;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.AdminMenuActivityBinding;
import com.example.reservasdeportes.model.LoggedUserDTO;
import com.example.reservasdeportes.services.BookingService;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminMenuActivity extends AppCompatActivity {

    private LoggedUserDTO loggedUserDTO;

    private final String TAG = AdminMenuActivity.class.toString();

    private final ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            String bookingId = result.getData().getStringExtra("data");
            new BookingService().updateCheckedById(this, TAG, loggedUserDTO.getToken(), bookingId, new ServerCallback() {
                @Override
                public void onSuccess(JSONObject result) {
                    try {
                        if (result.getBoolean("updated")) {
                            Toast.makeText(AdminMenuActivity.this, "Reserva comenzada", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(AdminMenuActivity.this, "Hubo un error", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(AdminMenuActivity.this, "Hubo un error", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(AdminMenuActivity.this, "Hubo un error", Toast.LENGTH_SHORT).show();
                }
            });
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdminMenuActivityBinding binding = AdminMenuActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Button button =  binding.button;

        loggedUserDTO = getIntent().getParcelableExtra("loggedUserDTO");

        button.setOnClickListener(v -> {
            Intent intent = new Intent(this, QrCodeScannerActivity.class);
            startForResult.launch(intent);
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[] {
                        Manifest.permission.CAMERA
                }, 1);
            }
        }
    }
}