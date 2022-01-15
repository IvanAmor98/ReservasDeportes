package com.example.reservasdeportes.ui.login;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Class exposing authenticated user details to the UI.
 */
public class LoggedUserData implements Parcelable {
    private final String _id;
    private final String displayName;
    private final String email;

    LoggedUserData(String _id, String email, String displayName) {
        this._id = _id;
        this.email = email;
        this.displayName = displayName;
    }

    protected LoggedUserData(Parcel in) {
        _id = in.readString();
        displayName = in.readString();
        email = in.readString();
    }

    public String getId() { return _id; }
    public String getEmail() { return email; }
    public String getDisplayName() {
        return displayName;
    }

    public static final Creator<LoggedUserData> CREATOR = new Creator<LoggedUserData>() {
        @Override
        public LoggedUserData createFromParcel(Parcel in) {
            return new LoggedUserData(in);
        }

        @Override
        public LoggedUserData[] newArray(int size) {
            return new LoggedUserData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(displayName);
        dest.writeString(email);
    }
}