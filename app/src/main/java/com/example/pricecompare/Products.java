package com.example.pricecompare;

public class Products {



    String imageLogo;
    String imageProduct;
    String ProductDescription;
    String Price;



    String urlLink;

    public Products(String ProductDes,String price){
        ProductDescription=ProductDes;
        Price=price;
    }



    public Products(String ProductDes,String price,String urlLink){
        ProductDescription=ProductDes;
        Price=price;
        this.urlLink=urlLink;
    }

    public Products(String ProductDes,String price,String imageP,String urlLink){
        ProductDescription=ProductDes;
        Price=price;
        imageProduct=imageP;
        this.urlLink=urlLink;
    }
    public Products(String ProductDes,String price,String imageP,String urlLink,String imageLogo){
        ProductDescription=ProductDes;
        Price=price;
        imageProduct=imageP;
        this.urlLink=urlLink;
        this.imageLogo=imageLogo;
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

    public String getUrlLink() {
        return urlLink;
    }
}
