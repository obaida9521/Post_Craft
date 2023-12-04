package com.developerobaida.postcraft.model;

public class BookmarkItem {
    String title, url, timestamp,type,bookmarkId,userId;

    public BookmarkItem(String title, String url, long timestamp,String type,String bookmarkId,String userId) {
        this.title = title;
        this.url = url;
        this.timestamp = String.valueOf(timestamp);
        this.type = type;
        this.bookmarkId = bookmarkId;
        this.userId =userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(String bookmarkId) {
        this.bookmarkId = bookmarkId;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
