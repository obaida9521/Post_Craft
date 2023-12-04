package com.developerobaida.postcraft.model;

public class ItemRecentWallpaper {
    String wallpaper,title;

    public ItemRecentWallpaper(String wallpaper, String title) {
        this.wallpaper = wallpaper;
        this.title = title;
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
