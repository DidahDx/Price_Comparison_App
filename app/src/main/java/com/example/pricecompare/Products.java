package com.example.pricecompare;

public class Products {



    String imageLogo;
    String imageProduct;
    String ProductDescription;


    String PriceOld;
    String PriceNew;
    String urlLink;

    public Products(String ProductDes,String price){
        ProductDescription=ProductDes;
        PriceOld=price;
    }



    public Products(String ProductDes,String price,String imageP,String urlLink,String imageLogo){
        ProductDescription=ProductDes;
        PriceOld=price;
        imageProduct=imageP;
        this.urlLink=urlLink;
        this.imageLogo=imageLogo;
    }


    public Products(String ProductDes,String priceOld,String imageP,String urlLink,String imageLogo,String PriceNew){
        ProductDescription=ProductDes;
        PriceOld=priceOld;
        this.PriceNew=PriceNew;
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

    public String getUrlLink() {
        return urlLink;
    }


    public String getPriceOld() {
        return PriceOld;
    }

    public String getPriceNew() {
        return PriceNew;
    }

}