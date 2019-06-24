package com.example.pricecompare;

public class Products {

    int imageLogo;
    int imageProduct;
    String ProductDescription;
    String Price;


    public Products(String ProductDes,String price){
        ProductDescription=ProductDes;
        Price=price;
    }

    public int getImageLogo() {
        return imageLogo;
    }

    public int getImageProduct() {
        return imageProduct;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public String getPrice() {
        return Price;
    }
}
