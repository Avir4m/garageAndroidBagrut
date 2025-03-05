package com.example.garage.models;

public class Vehicle {
    private String make, model, type, year, ownerId;

    public Vehicle() {
    }

    public Vehicle(String make, String model, String type, String year, String ownerId) {
        this.make = make;
        this.model = model;
        this.type = type;
        this.year = year;
        this.ownerId = ownerId;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getType() {
        return type;
    }

    public String getYear() {
        return year;
    }

    public String getOwnerId() {
        return ownerId;
    }
}