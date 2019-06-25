package com.example.pricecompare;

public class Products {

    String imageLogo;
    String imageProduct;
    String ProductDescription;
    String Price;

    public Products(String ProductDes,String price){
        ProductDescription=ProductDes;
        Price=price;
    }

    public Products(String ProductDes,String price,String imageP){
        ProductDescription=ProductDes;
        Price=price;
        imageProduct=imageP;
    }

    public String getImageLogo() {
        return imageLogo;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public String getPrice() {
        return Price;
    }
}
