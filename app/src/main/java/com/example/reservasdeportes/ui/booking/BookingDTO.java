package com.example.reservasdeportes.ui.booking;

import android.os.Parcel;
import android.os.Parcelable;

public class BookingDTO implements Parcelable {
    private final String _id;
    private String userId;
    private String facilityId;
    private String facilityName;
    private int[] date;
    private int[] timeFrom;
    private int[] timeTo;
    private boolean payed;

    public BookingDTO() { _id = null; }

    public BookingDTO(String id, String userId, String facilityId, String facilityName, int[] date, int[] timeFrom, int[] timeTo, boolean payed) {
        this._id = id;
        this.userId = userId;
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.payed = payed;
    }


    protected BookingDTO(Parcel in) {
        _id = in.readString();
        userId = in.readString();
        facilityId = in.readString();
        facilityName = in.readString();
        date = in.createIntArray();
        timeFrom = in.createIntArray();
        timeTo = in.createIntArray();
        payed = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(userId);
        dest.writeString(facilityId);
        dest.writeString(facilityName);
        dest.writeIntArray(date);
        dest.writeIntArray(timeFrom);
        dest.writeIntArray(timeTo);
        dest.writeInt(payed ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BookingDTO> CREATOR = new Creator<BookingDTO>() {
        @Override
        public BookingDTO createFromParcel(Parcel in) {
            return new BookingDTO(in);
        }

        @Override
        public BookingDTO[] newArray(int size) {
            return new BookingDTO[size];
        }
    };

    public String getId() {
        return _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() { return facilityName; }

    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public int[] getDate() { return date; }

    public void setDate(int[] date) { this.date = date; }

    public int[] getTimeFrom() { return timeFrom; }

    public void setTimeFrom(int[] timeFrom) { this.timeFrom = timeFrom; }

    public int[] getTimeTo() { return timeTo; }

    public void setTimeTo(int[] timeTo) { this.timeTo = timeTo; }

    public boolean isPayed() { return payed; }

    public void setPayed(boolean payed) { this.payed = payed; }
}
