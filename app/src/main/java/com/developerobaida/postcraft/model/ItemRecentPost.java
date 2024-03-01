package com.developerobaida.postcraft.model;

public class ItemRecentPost {
    String status,image,font,category,color,size,dx,dy,radius,shadowColor,id,type;
    boolean isBookmarked;

    public ItemRecentPost(String status, String image, String font, String color,String size, String category,String dy,String dx,String radius,String shadowColor,String id,String type) {
        this.status = status;
        this.category = category;
        this.image = image;
        this.font = font;
        this.color = color;
        this.size = size;
        this.dy = dy;
        this.dx = dx;
        this.radius = radius;
        this.shadowColor = shadowColor;
        this.id = id;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getDx() {
        return dx;
    }

    public void setDx(String dx) {
        this.dx = dx;
    }

    public String getDy() {
        return dy;
    }

    public void setDy(String dy) {
        this.dy = dy;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.shadowColor = shadowColor;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
