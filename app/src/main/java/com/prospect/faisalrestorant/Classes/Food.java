package com.prospect.faisalrestorant.Classes;

public class Food {
    String key;
    String foodname;
    Long price;
    String Date;

    public Food( ) {

    }
    public Food(String key,
                          String foodname,
                          Long price,
                          String Date
    ) {
        this.key=key;
        this.foodname=foodname;
        this.price=price;
        this.Date=Date;

    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getKey() {
        return key;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFoodname() {
        return foodname;
    }

    public void setFoodname(String foodname) {
        this.foodname = foodname;
    }

    }
