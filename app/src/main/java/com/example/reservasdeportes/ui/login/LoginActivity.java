package com.example.reservasdeportes.ui.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.databinding.LoginActivityBinding;
import com.example.reservasdeportes.model.LoggedUserDTO;
import com.example.reservasdeportes.services.UserService;
import com.example.reservasdeportes.ui.AdminMenuActivity;
import com.example.reservasdeportes.ui.MainMenuActivity;
import com.example.reservasdeportes.ui.signup.SignupActivity;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.toString();
    private LoginViewModel loginViewModel;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;

    private final ActivityResultLauncher<IntentSenderRequest> loginResultHandler = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
        // handle intent result here
        if (result.getResultCode() == RESULT_OK) Log.d(TAG, "RESULT_OK.");
        if (result.getResultCode() == RESULT_CANCELED) Log.d(TAG, "RESULT_CANCELED.");
        if (result.getResultCode() == RESULT_FIRST_USER) Log.d(TAG, "RESULT_FIRST_USER.");
        try {
            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
            String idToken = credential.getGoogleIdToken();
            String username = credential.getId();
            String password = credential.getPassword();

            if (idToken !=  null) {
                // Got an ID token from Google. Use it to authenticate
                // with your backend.
                new UserService().googleLogin(idToken, username, password, this, TAG, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            LoggedUserDTO userData = new LoggedUserDTO(
                                    result.getJSONObject("successData").getString("_id"),
                                    result.getJSONObject("successData").getString("email"),
                                    result.getJSONObject("successData").getString("username"),
                                    result.getJSONObject("successData").getString("token"),
                                    false);
                            saveLoginData(userData);
                            startMainActivity(userData);
                        } catch (Exception e) {
                            Toast.makeText(LoginActivity.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(LoginActivity.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, error);
                    }
                });
            } else if (password != null) {
                // Got a saved username and password. Use them to authenticate
                // with your backend.
                Log.d(TAG, "Got password.");
            }
        } catch (ApiException e) {
            switch (e.getStatusCode()) {
                case CommonStatusCodes.CANCELED:
                    Log.d(TAG, "One-tap dialog was closed.");
                    // Don't re-prompt the user.
                    //showOneTapUI = false;
                    break;
                case CommonStatusCodes.NETWORK_ERROR:
                    Log.d(TAG, "One-tap encountered a network error.");
                    // Try again or just ignore.
                    break;
                default:
                    Log.d(TAG, "Couldn't get credential from result."
                            + e.getLocalizedMessage());
                    break;
            }
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId("398083326352-mcce8uhaf9golv4h6avm65uneijtljt7.apps.googleusercontent.com")
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                // Automatically sign in when exactly one credential is retrieved.
                .setAutoSelectEnabled(true)
                .build();

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
        final SignInButton signInButton = binding.signInButton;

        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(v -> oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    try {
                        loginResultHandler.launch(new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build());
                    } catch(android.content.ActivityNotFoundException e){
                        e.printStackTrace();
                        Log.e(TAG, "Error: " + e.getLocalizedMessage());
                    }
                })
                .addOnFailureListener(this, e -> {
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Toast.makeText(this, "Hubo un error", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onCreate: " + e.getMessage(), null);
                }));

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

    private void startMainActivity(LoggedUserDTO loggedUserDTO) {
        Intent intent;
        if (loggedUserDTO.isAdmin()) {
            intent = new Intent(this, AdminMenuActivity.class);
        } else  {
            intent = new Intent(this, MainMenuActivity.class);
        }
        intent.putExtra("loggedUserDTO", loggedUserDTO);
        startActivity(intent);
        finish();
    }

    private void checkLoggedIn() {
        SharedPreferences sharedPreferences = getSharedPreferences("logData", MODE_PRIVATE);

        long timeOfLastLogin = sharedPreferences.getLong("lastLogin", 0);
        long currentTime = System.currentTimeMillis();

        if(currentTime - timeOfLastLogin < 5*60*1000) startMainActivity(new LoggedUserDTO(
                sharedPreferences.getString("_id", ""),
                sharedPreferences.getString("email", ""),
                sharedPreferences.getString("user", ""),
                sharedPreferences.getString("token", ""),
                sharedPreferences.getBoolean("admin", false)
                ));
    }

    private void saveLoginData(LoggedUserDTO loggedUserDTO) {
        SharedPreferences.Editor editor = getSharedPreferences("logData", MODE_PRIVATE).edit();

        editor.putString("_id", loggedUserDTO.getId());
        editor.putString("email", loggedUserDTO.getEmail());
        editor.putString("user", loggedUserDTO.getDisplayName());
        editor.putString("token", loggedUserDTO.getToken());
        editor.putBoolean("admin", loggedUserDTO.isAdmin());
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
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.SCHEDULE_EXACT_ALARM
                }, 1);
            }
        }
    }
}