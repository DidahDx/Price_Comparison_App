package com.example.pricecompare.data.model;

public class Products {


    private String imageLogo;
    private String imageProduct;
    private String productDescription;
    private  String newProductLabel;
    private String priceOld;
    private String priceNew;
    private String urlLink;
    private String discountPercentage;
    private String imgLogoDescrption;

    public boolean isImageChanged;

    private Products(){
    }


    public Products(String ProductDes,String priceOld,String imageP,String urlLink,String imageLogo,String PriceNew,String dis,
                    String imgLogoDes){
        imgLogoDescrption=imgLogoDes;
        discountPercentage=dis;
        productDescription =ProductDes;
        this.priceOld =priceOld;
        this.priceNew =PriceNew;
        imageProduct=imageP;
        this.urlLink=urlLink;
        this.imageLogo=imageLogo;
    }

    public Products(String ProductDes,String priceOld,String imageP,String urlLink,String imageLogo,String PriceNew,String dis){
        discountPercentage=dis;
        productDescription =ProductDes;
        this.priceOld =priceOld;
        this.priceNew =PriceNew;
        imageProduct=imageP;
        this.urlLink=urlLink;
        this.imageLogo=imageLogo;
    }


    public boolean isImageChanged() {
        return isImageChanged;
    }

    public void setImageChanged(boolean imageChanged) {
        isImageChanged = imageChanged;
    }

    public String getImageLogo() {
        return imageLogo;
    }

    public String getImageProduct() {
        return imageProduct;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getUrlLink() {
        return urlLink;
    }

    public String getNewProductLabel() {
        return newProductLabel;
    }

    public String getPriceOld() {
        return priceOld;
    }

    public String getPriceNew() {
        return priceNew;
    }

    public String getDiscountPercentage() {
        return discountPercentage;
    }

    public String getImgLogoDescrption() {
        return imgLogoDescrption;
    }
}