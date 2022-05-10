package com.example.reservasdeportes.model;

//Enumerador tipos de instalaciones disponibles
public enum FacilityTypes {
    Football(0),
    Basketball(1),
    Volleyball(2),
    Badminton(3),
    Tenis(4);

    //Getter/Setter
    private int value;
    FacilityTypes(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
