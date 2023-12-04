package com.developerobaida.postcraft.model;

public class ItemAllWallpaper {
    String wallpaper,title,category;

    public ItemAllWallpaper(String wallpaper, String title, String category) {
        this.wallpaper = wallpaper;
        this.title = title;
        this.category = category;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
