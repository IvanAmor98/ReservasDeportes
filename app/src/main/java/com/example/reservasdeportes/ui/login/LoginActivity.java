package com.example.reservasdeportes.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reservasdeportes.databinding.LoginActivityBinding;
import com.example.reservasdeportes.ui.MainMenuActivity;
import com.example.reservasdeportes.ui.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.toString();
    private LoginViewModel loginViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();
        checkLoggedIn();

        LoginActivityBinding binding = LoginActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loginViewModel = new LoginViewModel();

        final EditText emailEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final TextView signupButton = binding.signup;
        final ProgressBar progressBar = binding.loading;

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());

            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
            }

        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            if (loginResult.getError() != null) {
                Toast.makeText(getApplicationContext(), loginResult.getError(), Toast.LENGTH_SHORT).show();
            }
            if (loginResult.getSuccess() != null) {
                saveLoginData(loginResult.getSuccess());
                startMainActivity(loginResult.getSuccess());
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(emailEditText.getText().toString());
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        this,
                        TAG);
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loginButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(
                    emailEditText.getText().toString(),
                    passwordEditText.getText().toString(),
                    this,
                    TAG);
        });

        signupButton.setOnClickListener(v -> {
            loginViewModel.cancelRequest(this, TAG);

            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    private void startMainActivity(LoggedUserData loggedUserData) {
        //loginViewModel.cancelRequest(this, TAG);

        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.putExtra("loginUserData", loggedUserData);
        startActivity(intent);
        finish();
    }

    private void checkLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("logData", MODE_PRIVATE);

        long timeOfLastLogin = sharedPreferences.getLong("lastLogin", 0);
        long currentTime = System.currentTimeMillis();

        if(currentTime - timeOfLastLogin < 5*60*1000) startMainActivity(new LoggedUserData(
                sharedPreferences.getString("_id", ""),
                sharedPreferences.getString("email", ""),
                sharedPreferences.getString("user", "")));
    }

    private void saveLoginData(LoggedUserData loggedUserData) {
        SharedPreferences.Editor editor = getSharedPreferences("logData", MODE_PRIVATE).edit();

        editor.putString("_id", loggedUserData.getId());
        editor.putString("email", loggedUserData.getEmail());
        editor.putString("user", loggedUserData.getDisplayName());
        editor.putLong("lastLogin", System.currentTimeMillis());

        editor.apply();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // If not, asks for permissions
                requestPermissions(new String[] {
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 1);
            }
        }
    }
}