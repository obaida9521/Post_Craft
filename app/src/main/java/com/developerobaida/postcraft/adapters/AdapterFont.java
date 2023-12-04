package com.developerobaida.postcraft.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.model.ItemFont;
import java.util.ArrayList;

public class AdapterFont extends RecyclerView.Adapter<AdapterFont.FontHolder>{
    ArrayList<ItemFont> arrayList;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(ItemFont item);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public AdapterFont(ArrayList<ItemFont> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public FontHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_font,parent,false);
        return new FontHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FontHolder holder, int position) {
        ItemFont itemFont = arrayList.get(position);


        Typeface typeface = Typeface.createFromAsset(context.getAssets(), ""+itemFont.getFont());
        holder.tvFont.setTypeface(typeface);


        holder.setSelected(itemFont.isSelected());

        holder.tvFont.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(itemFont);
                notifyDataSetChanged();
            }
        });

        String[] parts = itemFont.getFont().split("\\.");
        String fontName = parts[0];
        holder.tvFont.setText(fontName);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class FontHolder extends RecyclerView.ViewHolder{
        TextView tvFont;
        RelativeLayout itm;
        public FontHolder(@NonNull View itemView) {
            super(itemView);
            tvFont = itemView.findViewById(R.id.tvFont);
            itm = itemView.findViewById(R.id.itm);
        }
        public void setSelected(boolean isSelected) {
            tvFont.setSelected(isSelected);

            if (isSelected) itm.setBackgroundResource(R.drawable.shape_selected);
            else itm.setBackgroundResource(0);
        }
    }
}
