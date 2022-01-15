package com.example.reservasdeportes.ui.signup;

import androidx.annotation.Nullable;

class SignupResult {
    @Nullable
    private final Integer success;
    @Nullable
    private final Integer error;

    SignupResult(@Nullable Integer success, @Nullable Integer error) {
        this.success = success;
        this.error = error;
    }

    @Nullable
    Integer getSuccess() {
        return success;
    }

    @Nullable
    Integer getError() {
        return error;
    }
}