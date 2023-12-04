package com.developerobaida.postcraft.listeners;

import android.widget.ImageView;

import com.developerobaida.postcraft.model.ItemAllProfilePic;

public interface OnProfileItemClickListener {
    void onProfileItemClick(ItemAllProfilePic profilePic, ImageView imageView, int position);
}
