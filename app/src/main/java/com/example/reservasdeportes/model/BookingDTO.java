package com.example.reservasdeportes.model;

import android.os.Parcel;
import android.os.Parcelable;

//Clase de almacenamiento de datos de reservas
public class BookingDTO implements Parcelable {
    private String _id;
    private String userId;
    private String facilityId;
    private String facilityName;
    private FacilityTypes type;
    private int[] date;
    private int timeFrom;
    private int timeTo;
    private boolean paid;

    //Constructor por defecto
    public BookingDTO() { _id = null; }

    //Constructor
    public BookingDTO(
            String id,
            String userId,
            String facilityId,
            String facilityName,
            FacilityTypes type,
            int[] date,
            int timeFrom,
            int timeTo,
            boolean paid
    ) {
        this._id = id;
        this.userId = userId;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.type = type;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.paid = paid;
    }

    //Constructor de la interfaz parcelable (para poder deserializarlo)
    protected BookingDTO(Parcel in) {
        _id = in.readString();
        userId = in.readString();
        facilityId = in.readString();
        facilityName = in.readString();
        type = FacilityTypes.values()[in.readInt()];
        date = in.createIntArray();
        timeFrom = in.readInt();
        timeTo = in.readInt();
        paid = in.readInt() == 1;
    }

    //Serializador de la interfaz parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(userId);
        dest.writeString(facilityId);
        dest.writeString(facilityName);
        dest.writeInt(type.getValue());
        dest.writeIntArray(date);
        dest.writeInt(timeFrom);
        dest.writeInt(timeTo);
        dest.writeInt(paid ? 1 : 0);
    }

    //Metodo de la interfaz parcelable
    @Override
    public int describeContents() { return 0; }

    //Metodo de la interfaz parcelable
    public static final Creator<BookingDTO> CREATOR = new Creator<BookingDTO>() {
        @Override
        public BookingDTO createFromParcel(Parcel in) { return new BookingDTO(in); }

        @Override
        public BookingDTO[] newArray(int size) { return new BookingDTO[size]; }
    };


    //Getters/Setters
    public String getId() { return _id; }

    public void setId(String id) { this._id = id; }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getFacilityId() { return facilityId; }

    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }

    public String getFacilityName() { return facilityName; }

    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public FacilityTypes getType() { return type; }

    public void setType(FacilityTypes type) { this.type = type; }

    public int[] getDate() { return date; }

    public void setDate(int[] date) { this.date = date; }

    public int getTimeFrom() { return timeFrom; }

    public void setTimeFrom(int timeFrom) { this.timeFrom = timeFrom; }

    public int getTimeTo() { return timeTo; }

    public void setTimeTo(int timeTo) { this.timeTo = timeTo; }

    public boolean isPaid() { return paid; }

    public void setPaid(boolean paid) { this.paid = paid; }
}
