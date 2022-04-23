package com.example.reservasdeportes.ui.login;

import androidx.annotation.Nullable;

import com.example.reservasdeportes.model.LoggedUserDTO;

/**
 * Authentication result : success (user details) or error message.
 */
class LoginResult {
    @Nullable
    private LoggedUserDTO success;
    @Nullable
    private Integer error;

    LoginResult(@Nullable Integer error) { this.error = error; }

    LoginResult(@Nullable LoggedUserDTO success) { this.success = success; }

    @Nullable
    LoggedUserDTO getSuccess() { return success; }

    @Nullable
    Integer getError() { return error; }
}