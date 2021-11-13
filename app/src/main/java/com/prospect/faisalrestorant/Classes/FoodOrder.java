package com.prospect.faisalrestorant.Classes;

public class FoodOrder {
    String key;
    String foodname;
    Long price;
    Long totalPrice;
    String quantity;
    String Date;


    public FoodOrder( ) {

    }
    public FoodOrder(String key,
                     String foodname,
                     Long price,
                     Long totalPrice,
                     String quantity,
                     String Date
    ) {
        this.key=key;
        this.foodname=foodname;
        this.price=price;
        this.totalPrice=totalPrice;
        this.quantity=quantity;
        this.Date=Date;

    }

    public String getKey() {
        return key;
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

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
