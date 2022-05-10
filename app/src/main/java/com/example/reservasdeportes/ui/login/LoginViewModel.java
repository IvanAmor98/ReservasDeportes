package com.example.reservasdeportes.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import com.example.reservasdeportes.controller.HttpService;
import com.example.reservasdeportes.model.LoggedUserDTO;
import com.example.reservasdeportes.services.UserService;
import com.example.reservasdeportes.controller.ServerCallback;
import com.example.reservasdeportes.R;

import org.json.JSONObject;

public class LoginViewModel extends ViewModel {

    private final String TAG = LoginViewModel.class.toString();
    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    private final UserService loginService = new UserService();

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }
    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password, Context context, String TAG) {
        loginService.login(email, password, context, TAG, new ServerCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if (result.getBoolean("success")) {
                        loginResult.setValue(new LoginResult(new LoggedUserDTO(
                                result.getJSONObject("successData").getString("_id"),
                                result.getJSONObject("successData").getString("email"),
                                result.getJSONObject("successData").getString("username"),
                                result.getJSONObject("successData").getString("token"),
                                result.getJSONObject("successData").getBoolean("admin")
                        )));
                    } else {
                        Log.e("LoginResponse", result.getString("errorData"));
                        loginResult.setValue(new LoginResult(R.string.login_failed));
                    }
                } catch (Exception e) {
                    Log.e("LoginResponse", e.getMessage());
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                }
            }

            @Override
            public void onError(String error) {
                Log.e("LoginResponse", error);
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        });
    }

    public void loginDataChanged(String email) {
        Integer checkEmail = isEmailValid(email) ? null : R.string.invalid_email;

        loginFormState.setValue(checkEmail == null ?
                new LoginFormState(true) :
                new LoginFormState(checkEmail)
        );
    }

    public void cancelRequest(Context context, String TAG) {
        HttpService.cancelRequest(context, TAG);
    }

    private boolean isEmailValid(String email) {
        return email != null &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                !email.trim().isEmpty();
    }
}