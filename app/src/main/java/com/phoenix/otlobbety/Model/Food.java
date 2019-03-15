package com.phoenix.otlobbety.Model;

public class Food {

    private String Name ;
    private String Image ;
    private String Description ;
    private String MenuId ;
    private String Discount ;
    private String Price ;

    public Food() {
    }

    public Food(String name, String image, String description, String menuId, String discount, String price) {
        Name = name;
        Image = image;
        Description = description;
        MenuId = menuId;
        Discount = discount;
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}