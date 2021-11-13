package com.prospect.faisalrestorant.Classes;

public class FoodOderValid {
    String key;
    String keyorder;
    String foodname;
    Long price;
    Long totalPrice;
    String quantity;
    String Date;


    public FoodOderValid( ) {

    }
    public FoodOderValid(String key,
                         String keyorder,
                         String foodname,
                         Long price,
                         Long totalPrice,
                         String quantity,
                         String Date
    ) {
        this.key=key;
        this.keyorder=keyorder;
        this.foodname=foodname;
        this.price=price;
        this.totalPrice=totalPrice;
        this.quantity=quantity;
        this.Date=Date;

    }

    public FoodOderValid(String foodname, String foodname1) {
        this.foodname=foodname;
        this.foodname=foodname1;
    }


    public String getKeyorder() {
        return keyorder;
    }

    public void setKeyorder(String keyorder) {
        this.keyorder = keyorder;
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
