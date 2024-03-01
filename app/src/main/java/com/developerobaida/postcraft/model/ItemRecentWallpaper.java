package com.developerobaida.postcraft.model;

public class ItemRecentWallpaper {
    String wallpaper,title,category,id,type;
    boolean isBookmarked;

    public ItemRecentWallpaper(String wallpaper, String title, String category, String id, String type) {
        this.wallpaper = wallpaper;
        this.title = title;
        this.category = category;
        this.id = id;
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean getIsBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(boolean bookmarked) {
        isBookmarked = bookmarked;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
