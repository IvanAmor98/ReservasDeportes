package com.example.reservasdeportes.ui.login;

import androidx.annotation.Nullable;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedUserData success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) { this.error = error; }

    LoginResult(@Nullable LoggedUserData success) { this.success = success; }

    @Nullable
    LoggedUserData getSuccess() { return success; }

    @Nullable
    Integer getError() { return error; }
}