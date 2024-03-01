package com.developerobaida.postcraft.model;

public class ItemAllWallpaper {
    String wallpaper,title,category,type,id;
    boolean isBookmarked;

    public ItemAllWallpaper(String wallpaper, String title, String category,String type,String id) {
        this.wallpaper = wallpaper;
        this.title = title;
        this.category = category;
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
