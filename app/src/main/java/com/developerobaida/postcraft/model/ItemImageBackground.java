package com.developerobaida.postcraft.model;

public class ItemImageBackground {
    String image,category;
    private boolean isSelected;

    public ItemImageBackground(String image, String category) {
        this.image = image;
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
