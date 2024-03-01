package com.developerobaida.postcraft.model;


public class BookmarkItem {
    String title,url,type;
    String status,image,font,color,size,dx,dy,radius,shadowColor;
    int db_id,serverId;

    public BookmarkItem(String title, String url, String type, int serverId,int db_id) {
        this.title = title;
        this.url = url;
        this.type = type;
        this.serverId = serverId;
        this.db_id = db_id;
    }
    public BookmarkItem(String status, String image, String font, String color, String size, String dx, String dy, String radius, String shadowColor,String type,int serverId,int db_id) {
        this.type = type;
        this.serverId = serverId;
        this.status = status;
        this.image = image;
        this.font = font;
        this.color = color;
        this.size = size;
        this.dx = dx;
        this.dy = dy;
        this.radius = radius;
        this.shadowColor = shadowColor;
        this.db_id = db_id;
    }
    public int getDb_id() {
        return db_id;
    }

    public void setDb_id(int db_id) {
        this.db_id = db_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
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

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}