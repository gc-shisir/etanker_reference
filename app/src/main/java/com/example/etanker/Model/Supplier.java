package com.example.etanker.Model;

public class Supplier {
    private String name;
    private String email;
    private String phone_number;
    private String tanker_count;
    private String location;
    private String latitude;
    private String longitude;

    public Supplier() {
    }

    public Supplier(String name, String email, String phone_number, String tanker_count, String location, String latitude, String longitude) {
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.tanker_count = tanker_count;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getTanker_count() {
        return tanker_count;
    }

    public void setTanker_count(String tanker_count) {
        this.tanker_count = tanker_count;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }
}
