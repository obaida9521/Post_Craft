package com.developerobaida.postcraft.slider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.developerobaida.postcraft.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{
    List<SlideItem> slideItems;
    ViewPager2 viewPager2;
    Context context;


    public SliderAdapter(List<SlideItem> slideItems, ViewPager2 viewPager2) {
        this.slideItems = slideItems;
        this.viewPager2 = viewPager2;
        this.context = viewPager2.getContext();
    }


    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_lay,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.setImage(slideItems.get(position),position);
    }

    @Override
    public int getItemCount() {
        return slideItems.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
        void setImage(SlideItem slideItem, int p){

            Picasso.get().load(slideItem.getImageUrl()).placeholder(R.drawable.material_design_default).into(imageView);

        }
    }
}
