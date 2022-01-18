package com.example.reservasdeportes.ui.signup;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class SignupFormState {
    @Nullable
    private final Integer emailError;
    @Nullable
    private final Integer userNameError;
    @Nullable
    private final Integer passwordError;
    private final boolean isDataValid;

    SignupFormState(@Nullable Integer emailError, @Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.emailError = emailError;
        this.userNameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    SignupFormState(boolean isDataValid) {
        this.emailError = null;
        this.userNameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getEmailError() { return emailError; }

    @Nullable
    Integer getUsernameError() { return userNameError; }

    @Nullable
    Integer getPasswordError() { return passwordError; }

    boolean isDataValid() { return isDataValid; }
}