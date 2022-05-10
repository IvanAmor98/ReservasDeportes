package com.example.reservasdeportes.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

//Clase de almacenamiento de datos de usuario
public class LoggedUserDTO implements Parcelable {
    private final String _id;
    private final String displayName;
    private final String email;
    private final String token;
    private final boolean admin;

    //Constructor
    public LoggedUserDTO(String _id, String email, String displayName, String token, boolean admin) {
        this._id = _id;
        this.email = email;
        this.displayName = displayName;
        this.token = token;
        this.admin = admin;
    }

    //Constructor de la interfaz parcelable (para poder deserializarlo)
    protected LoggedUserDTO(Parcel in) {
        _id = in.readString();
        displayName = in.readString();
        email = in.readString();
        token = in.readString();
        admin = in.readInt() == 1;
    }

    //Getters
    public String getId() { return _id; }

    public String getEmail() { return email; }

    public String getDisplayName() { return displayName; }

    public String getToken() { return token; }

    public boolean isAdmin() { return admin; }

    //Metodo de la interfaz parcelable
    public static final Creator<LoggedUserDTO> CREATOR = new Creator<LoggedUserDTO>() {
        @Override
        public LoggedUserDTO createFromParcel(Parcel in) { return new LoggedUserDTO(in); }

        @Override
        public LoggedUserDTO[] newArray(int size) { return new LoggedUserDTO[size]; }
    };

    //Metodo de la interfaz parcelable
    @Override
    public int describeContents() { return 0; }

    //Serializador de la interfaz parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(displayName);
        dest.writeString(email);
        dest.writeString(token);
        dest.writeInt(admin ? 1 : 0);
    }
}