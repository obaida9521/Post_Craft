package com.developerobaida.postcraft.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.model.ItemImageBackground;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterBackgroundImage extends RecyclerView.Adapter<AdapterBackgroundImage.ImageViewHolder> {
    ArrayList<ItemImageBackground> arrayList;
    Context context;
    public interface OnItemClickListener {void onItemClick(ItemImageBackground item);}
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener listener) {this.onItemClickListener = listener;}
    public AdapterBackgroundImage(ArrayList<ItemImageBackground> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.background_image,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ItemImageBackground background = arrayList.get(position);

        Picasso.get().load(background.getImage()).into(holder.image);
        holder.setSelected(background.isSelected());

        holder.image.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(background);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        RelativeLayout itm;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            itm = itemView.findViewById(R.id.itm);
        }
        public void setSelected(boolean isSelected) {
            image.setSelected(isSelected);

            if (isSelected) itm.setBackgroundResource(R.drawable.shape_selected);
            else itm.setBackgroundResource(0);
        }
    }
}
