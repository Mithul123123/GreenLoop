package com.greenloop.model;

public class DeliveryAgent extends Person {
    private String vehicleNumber;

    public DeliveryAgent() {
    }

    public DeliveryAgent(int id, String name, String contactNumber, String vehicleNumber) {
        super(id, name, contactNumber);
        this.vehicleNumber = vehicleNumber;
    }

    @Override
    public String displayDetails() {
        return "Delivery Agent - " + super.displayDetails() + ", Vehicle: " + vehicleNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }
}
