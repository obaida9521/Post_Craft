package com.developerobaida.postcraft.model;

public class ItemRecentProfilePic {
    String image,title,category;

    public ItemRecentProfilePic(String image, String title, String category) {
        this.image = image;
        this.title = title;
        this.category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
