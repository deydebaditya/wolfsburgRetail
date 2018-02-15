package com.wolfsburgsolutions.myapplication;

/**
 * Created by Deba on 11/26/2017.
 */

public class DataModel {

    String name;
    String brand;
    String category;
    String description;
    boolean available;
    String mrp;
    String price;

    public DataModel(String name, String brand, String category, String description, boolean available, String mrp, String price){
        this.name = name;
        this.brand = brand;
        this.category = category;
        this.description = description;
        this.available = available;
        this.mrp = mrp;
        this.price = price;
    }
    public String getName(){
        return name;
    }
    public String getBrand(){
        return brand;
    }
    public String getCategory(){
        return category;
    }
    public String getDescription(){
        return description;
    }
    public boolean getAvailable(){
        return available;
    }
    public String getMRP(){
        return mrp;
    }
    public String getPrice(){
        return price;
    }
}
