package com.greenloop.model;

public class Product {
    private int productId;
    private String productName;
    private double price;
    private String ecoRating;

    public Product() {
    }

    public Product(int productId, String productName, double price, String ecoRating) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.ecoRating = ecoRating;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getEcoRating() {
        return ecoRating;
    }

    public void setEcoRating(String ecoRating) {
        this.ecoRating = ecoRating;
    }

    @Override
    public String toString() {
        return productId + " - " + productName;
    }
}
