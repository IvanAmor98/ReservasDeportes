package com.example.reservasdeportes.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.ui.booking.BookingListActivity;
import com.example.reservasdeportes.ui.facility.FacilityListActivity;
import com.example.reservasdeportes.ui.login.LoggedUserData;
import com.example.reservasdeportes.ui.login.LoginActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.reservasdeportes.databinding.MainMenuActivityBinding;

public class MainMenuActivity extends AppCompatActivity {

    private MainMenuActivityBinding binding;
    private LoggedUserData loggedUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = MainMenuActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loggedUserData = getIntent().getParcelableExtra("loginUserData");

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(loggedUserData.getDisplayName());

        Button btnListFacilities = binding.contentScrolling.btnListFacilities;
        Button btnListBookings = binding.contentScrolling.btnListBookings;

        btnListFacilities.setOnClickListener(v -> {

            /*Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode("Polideportivo Lalo García, Valladolid, España"));
            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            intent.setPackage("com.google.android.apps.maps");*/

            Intent intent = new Intent(this, FacilityListActivity.class);
            startActivity(intent);
        });

        btnListBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingListActivity.class);
            intent.putExtra("loggedUserData", loggedUserData);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("logData", MODE_PRIVATE).edit();
        editor.clear().apply();
        Toast.makeText(getApplicationContext(), "Bye " + loggedUserData.getDisplayName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}