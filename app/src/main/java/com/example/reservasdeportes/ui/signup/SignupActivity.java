package com.example.reservasdeportes.ui.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.reservasdeportes.databinding.SignupActivityBinding;

public class SignupActivity extends AppCompatActivity {

    private final String TAG = SignupActivity.class.toString();
    private SignupViewModel signupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SignupActivityBinding binding = SignupActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signupViewModel = new SignupViewModel();

        final EditText email = binding.emailInput;
        final EditText username = binding.userInput;
        final EditText password = binding.passInput;
        final Button signupButton = binding.btnSignup;
        final ProgressBar progressBar = binding.loading;

        signupViewModel.getSignupFormState().observe(this, signupFormState -> {
            if (signupFormState == null) {
                return;
            }

            signupButton.setEnabled(signupFormState.isDataValid());

            if (signupFormState.getEmailError() != null) {
                email.setError(getString(signupFormState.getEmailError()));
            }
            if (signupFormState.getUsernameError() != null) {
                username.setError(getString(signupFormState.getUsernameError()));
            }
            if (signupFormState.getPasswordError() != null) {
                password.setError(getString(signupFormState.getPasswordError()));
            }
        });

        signupViewModel.getSignupResult().observe(this, signupResult -> {
            if (signupResult == null) {
                return;
            }
            progressBar.setVisibility(View.GONE);
            if (signupResult.getError() != null) {
                email.setText("");
                username.setText("");
                password.setText("");
                Toast.makeText(getApplicationContext(), signupResult.getError(), Toast.LENGTH_SHORT).show();
            }
            if (signupResult.getSuccess() != null) {
                Toast.makeText(getApplicationContext(), signupResult.getSuccess(), Toast.LENGTH_LONG).show();

                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                signupViewModel.signupDataChanged(
                        email.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString());
            }
        };

        email.addTextChangedListener(afterTextChangedListener);
        username.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);

        password.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                signupButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                signupViewModel.signup(email.getText().toString(),
                        username.getText().toString(),
                        password.getText().toString(),
                        SignupActivity.this,
                        TAG);
            }
            return false;
        });


        signupButton.setOnClickListener(v -> {
            signupButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            signupViewModel.signup(email.getText().toString(),
                    username.getText().toString(),
                    password.getText().toString(),
                    this,
                    TAG);
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        signupViewModel.cancelRequest(this, TAG);

    }
}