package com.greenloop.model;

public class Client extends Person {
    private String email;

    public Client() {
    }

    public Client(int id, String name, String contactNumber, String email) {
        super(id, name, contactNumber);
        this.email = email;
    }

    @Override
    public String displayDetails() {
        return "Client - " + super.displayDetails() + ", Email: " + email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
