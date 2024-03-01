package com.developerobaida.postcraft.model;

public class ItemFont {
    String font,category;
    private boolean isSelected;

    public ItemFont(String font, String category) {
        this.font = font;
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

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
