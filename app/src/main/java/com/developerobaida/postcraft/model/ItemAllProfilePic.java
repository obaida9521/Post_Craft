package com.developerobaida.postcraft.model;

public class ItemAllProfilePic {
    String image,title,category,type,id,bookmark;
    boolean isBookmarked;

    public ItemAllProfilePic(String image, String title, String category,String type,String id,String bookmark) {
        this.image = image;
        this.title = title;
        this.category = category;
        this.type = type;
        this.isBookmarked = isBookmarked;
        this.id = id;
        this.bookmark = bookmark;
    }

    public String getBookmark() {
        return bookmark;
    }

    public void setBookmark(String bookmark) {
        this.bookmark = bookmark;
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
