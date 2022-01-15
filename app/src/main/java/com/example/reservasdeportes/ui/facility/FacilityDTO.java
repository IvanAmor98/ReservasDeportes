package com.example.reservasdeportes.ui.facility;

import android.os.Parcel;
import android.os.Parcelable;

public class FacilityDTO implements Parcelable {
    private final String _id;
    private String name;
    private String country;
    private String state;
    private float latitude;
    private float longitude;

    public FacilityDTO(String _id, String name, String country, String state, float latitude, float longitude) {
        this._id = _id;
        this.name = name;
        this.country = country;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    protected FacilityDTO(Parcel in) {
        _id = in.readString();
        name = in.readString();
        country = in.readString();
        state = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
    }

    public static final Creator<FacilityDTO> CREATOR = new Creator<FacilityDTO>() {
        @Override
        public FacilityDTO createFromParcel(Parcel in) {
            return new FacilityDTO(in);
        }

        @Override
        public FacilityDTO[] newArray(int size) {
            return new FacilityDTO[size];
        }
    };

    public String getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
    }
}
