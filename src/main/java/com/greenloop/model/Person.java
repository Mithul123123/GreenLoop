package com.greenloop.model;

public class Person {
    private int id;
    private String name;
    private String contactNumber;

    public Person() {
    }

    public Person(int id, String name, String contactNumber) {
        this.id = id;
        this.name = name;
        this.contactNumber = contactNumber;
    }

    public String displayDetails() {
        return "ID: " + id + ", Name: " + name + ", Contact: " + contactNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
