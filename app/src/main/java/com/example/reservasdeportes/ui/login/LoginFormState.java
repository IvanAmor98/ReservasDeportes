package com.example.reservasdeportes.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private final Integer emailError;
    private final boolean isDataValid;

    LoginFormState(@Nullable Integer emailError) {
        this.emailError = emailError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.emailError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() { return emailError; }

    boolean isDataValid() { return isDataValid; }
}