package com.laurensius_dede_suhardiman.ekgapps.model;

import java.io.Serializable;

public class Patient implements Serializable {

    String id;
    String name;
    String age;
    String gender;
    String address;
    String status;
    String datetime;

    public Patient(
            String id,
            String name,
            String age,
            String gender,
            String address,
            String status,
            String datetime
    ){
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.status = status;
        this.datetime = datetime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getStatus() {
        return status;
    }

    public String getDatetime() {
        return datetime;
    }
}
