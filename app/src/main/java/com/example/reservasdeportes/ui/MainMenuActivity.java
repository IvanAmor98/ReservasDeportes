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
import android.widget.Switch;
import android.widget.Toast;

import com.example.reservasdeportes.databinding.MainMenuActivityBinding;

public class MainMenuActivity extends AppCompatActivity {

    private LoggedUserData loggedUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivityBinding binding = MainMenuActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loggedUserData = getIntent().getParcelableExtra("loggedUserData");

        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(loggedUserData.getDisplayName());

        Button btnListFacilities = binding.contentScrolling.btnListFacilities;
        Button btnListBookings = binding.contentScrolling.btnListBookings;

        btnListFacilities.setOnClickListener(v -> {
            Intent intent = new Intent(this, FacilityListActivity.class);
            intent.putExtra("loggedUserData", loggedUserData);
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
        switch(item.getItemId()){
            case R.id.menu_about:
                about();
                return true;
            case R.id.menu_help:
                help();
                return true;
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void about() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void help() {

    }

    private void logout() {
        SharedPreferences.Editor editor = getSharedPreferences("logData", MODE_PRIVATE).edit();
        editor.clear().apply();
        Toast.makeText(getApplicationContext(), "Bye " + loggedUserData.getDisplayName(), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}