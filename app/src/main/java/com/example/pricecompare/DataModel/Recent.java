package com.example.pricecompare.DataModel;

public class Recent {

   private String name;
    private int recentImage;
    private int leftImage;

    public Recent(){
    }

//    public Recent(String name, int recentImage) {
//        this.name = name;
//        this.recentImage = recentImage;
//    }

    public Recent(String name, int recentImage, int leftImage) {
        this.name = name;
        this.recentImage = recentImage;
        this.leftImage = leftImage;
    }

    public String getName() {
        return name;
    }

    public int getRecentImage() {
        return recentImage;
    }

    public int getLeftImage() {
        return leftImage;
    }
}