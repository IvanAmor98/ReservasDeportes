package com.example.reservasdeportes.ui.signup;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.reservasdeportes.R;
import com.example.reservasdeportes.services.FacilityService;
import com.example.reservasdeportes.services.UserService;
import com.example.reservasdeportes.controller.ServerCallback;

import org.json.JSONObject;


public class SignupViewModel extends ViewModel {

    private final MutableLiveData<SignupFormState> signupFormState = new MutableLiveData<>();
    private final MutableLiveData<SignupResult> signupResult = new MutableLiveData<>();

    private final UserService loginService = new UserService();

    LiveData<SignupFormState> getSignupFormState() {
        return signupFormState;
    }
    LiveData<SignupResult> getSignupResult() { return signupResult; }

    public void signup(String email, String username, String password, Context context, String TAG) {
        loginService.signup(email, username, password, context, TAG, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if (result.getBoolean("alreadyExists")) {
                        signupResult.setValue(new SignupResult(null, R.string.signup_failed_exists));
                    } else {
                        signupResult.setValue(new SignupResult(R.string.signup_success, null));
                    }
                } catch (Exception e) {
                    Log.e("SignupResponse", e.getMessage());
                    signupResult.setValue(new SignupResult(null, R.string.signup_failed));
                }
            }

            @Override
            public void onError(String error) {
                Log.e("SignupResponse", error);
                signupResult.setValue(new SignupResult(null, R.string.signup_failed));
            }
        });
    }

    public void signupDataChanged(String email, String username, String password) {
        Integer checkEmail = isEmailValid(email) ? null : R.string.invalid_email;
        Integer checkUsername = isUsernameValid(username) ? null : R.string.invalid_username;
        Integer checkPassword = isPasswordValid(password) ? null : R.string.invalid_password;

        if (!(checkEmail != null || checkUsername != null || checkPassword != null)) {
            signupFormState.setValue(new SignupFormState(true));
        } else {
            signupFormState.setValue(new SignupFormState(checkEmail, checkUsername, checkPassword));
        }
    }

    public void cancelRequest(Context context, String TAG) {
        FacilityService.cancelRequest(context, TAG);
    }

    private boolean isEmailValid(String email) {
        return email != null &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                !email.trim().isEmpty();
    }

    private boolean isUsernameValid(String username) {
        return username != null && username.trim().length() >= 3;
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 5;
    }
}
