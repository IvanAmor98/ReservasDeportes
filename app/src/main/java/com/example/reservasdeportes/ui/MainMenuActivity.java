package com.example.reservasdeportes.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.ui.booking.BookingListActivity;
import com.example.reservasdeportes.ui.facility.FacilityListActivity;
import com.example.reservasdeportes.model.LoggedUserDTO;
import com.example.reservasdeportes.ui.login.LoginActivity;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.example.reservasdeportes.databinding.MainMenuActivityBinding;

//Pagina principal tras acceso de usuario
public class MainMenuActivity extends AppCompatActivity {

    private LoggedUserDTO loggedUserDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainMenuActivityBinding binding = MainMenuActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Obtiene el objeto de datos de usuario del intent
        loggedUserDTO = getIntent().getParcelableExtra("loggedUserDTO");

        //Define los elementos de la interfaz
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(loggedUserDTO.getDisplayName());

        Button btnListFacilities = binding.contentScrolling.btnListFacilities;
        Button btnListBookings = binding.contentScrolling.btnListBookings;

        //Define listeners de botones
        btnListFacilities.setOnClickListener(v -> {
            //Lanza un intent a la activity correspondiente
            Intent intent = new Intent(this, FacilityListActivity.class);
            intent.putExtra("loggedUserDTO", loggedUserDTO);
            startActivity(intent);
        });

        btnListBookings.setOnClickListener(v -> {
            //Lanza un intent a la activity correspondiente
            Intent intent = new Intent(this, BookingListActivity.class);
            intent.putExtra("loggedUserDTO", loggedUserDTO);
            startActivity(intent);
        });

    }

    //Crea el menu de opciones
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_menu, menu);
        return true;
    }

    //Define las acciones que se realizaran al pulsar una opcion
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

    //Lanza un intent al activity about
    private void about() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void help() {

    }

    //Cierra sesion
    private void logout() {
        //Obtiene el archivo de preferencias
        SharedPreferences.Editor editor = getSharedPreferences("logData", MODE_PRIVATE).edit();
        //Borra el contenido del archivo
        editor.clear().apply();
        Toast.makeText(getApplicationContext(), getString(R.string.logout) + loggedUserDTO.getDisplayName(), Toast.LENGTH_SHORT).show();

        //Lanza un intent al activity de login
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}