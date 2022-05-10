package com.example.reservasdeportes.model;

import android.os.Parcel;
import android.os.Parcelable;

//Clase de almacenamiento de datos de instalaciones
public class FacilityDTO implements Parcelable {
    private final String _id;
    private String name;
    private String country;
    private String state;
    private FacilityTypes[] facilityTypes;
    private float latitude;
    private float longitude;

    //Constructor
    public FacilityDTO(String _id, String name, String country, String state, FacilityTypes[] facilityTypes, float latitude, float longitude) {
        this._id = _id;
        this.name = name;
        this.country = country;
        this.state = state;
        this.facilityTypes = facilityTypes;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //Constructor de la interfaz parcelable (para poder deserializarlo)
    protected FacilityDTO(Parcel in) {
        _id = in.readString();
        name = in.readString();
        country = in.readString();
        state = in.readString();
        int[] tempTypes = new int[in.readInt()];
        in.readIntArray(tempTypes);
        facilityTypes = intArrayToTypes(tempTypes);
        latitude = in.readFloat();
        longitude = in.readFloat();
    }

    //Metodo de la interfaz parcelable
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

    //Getters/Setters
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

    public FacilityTypes[] getFacilityTypes() { return facilityTypes; }

    public void setFacilityTypes(FacilityTypes[] facilityTypes) { this.facilityTypes = facilityTypes; }

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

    //Metodo de la interfaz parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    //Serializador de la interfaz parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(state);
        dest.writeInt(facilityTypes.length);
        dest.writeIntArray(typesToIntArray(facilityTypes));
        dest.writeFloat(latitude);
        dest.writeFloat(longitude);
    }

    //Trasforma un array de enum FacilityTypes a array de int
    public int[] typesToIntArray(FacilityTypes[] types) {
        int[] ints = new int[types.length];
        for (int i = 0; i < types.length; i++) {
            ints[i] = types[i].getValue();
        }
        return ints;
    }

    //Trasforma un array de array de int a enum FacilityTypes
    public FacilityTypes[] intArrayToTypes(int[] ints) {
        FacilityTypes[] types = new FacilityTypes[ints.length];
        for (int i = 0; i < ints.length; i++) {
            types[i] = FacilityTypes.values()[ints[i]];
        }
        return types;
    }
}
