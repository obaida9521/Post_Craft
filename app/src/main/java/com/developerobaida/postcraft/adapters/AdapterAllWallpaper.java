package com.developerobaida.postcraft.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.activities.ShowImage;
import com.developerobaida.postcraft.database.DatabaseHelper;
import com.developerobaida.postcraft.model.ItemAllWallpaper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class AdapterAllWallpaper extends RecyclerView.Adapter{
    ArrayList<ItemAllWallpaper> arrayList;
    Context context;
    DatabaseHelper database;
    int serverid;

    public interface WallFavClickListener {
        void wallFavClick(int position, boolean isFav);
    }
    private WallFavClickListener favClickListener;
    //-----------------------------------
    public void setFilter(ArrayList<ItemAllWallpaper> newList) {
        arrayList = new ArrayList<>();
        arrayList.addAll(newList);
        notifyDataSetChanged();
    }
    //------------------------------------
    public AdapterAllWallpaper(ArrayList<ItemAllWallpaper> arrayList, Context context,WallFavClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.favClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_wallpaper,parent,false);
        return new AllVideos(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AllVideos holders = (AllVideos) holder;
        ItemAllWallpaper allWallpaper = arrayList.get(position);

        database = new DatabaseHelper(context);
        Cursor cursor = database.getWall_fav();
        while (cursor.moveToNext()){
            serverid = cursor.getInt(4);
            if (serverid == Integer.parseInt(allWallpaper.getId())) {
                Log.d("id : ",allWallpaper.getId());
                holders.imgFav.setImageResource(R.drawable.favorite_24);
                allWallpaper.setBookmarked(true);
                break;
            }
        }

        Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_animation_from_bottom);
        holder.itemView.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {holder.itemView.clearAnimation();}
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        holders.download.setOnClickListener(v ->{
            BitmapDrawable bitmapDrawable = (BitmapDrawable) holders.wallpaper.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            saveImage(bitmap);
        });

        holders.share.setOnClickListener(v -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) holders.wallpaper.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            shareImageAndText(bitmap);
        });

        Picasso.get().load(allWallpaper.getWallpaper()).into(holders.wallpaper);
        holders.marquee.setSelected(true);
        holders.marquee.setText(allWallpaper.getTitle());
        holders.wallpaper.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowImage.class);
            intent.putExtra("image",""+allWallpaper.getWallpaper());
            intent.putExtra("title",""+allWallpaper.getTitle());
            intent.putExtra("type",true);
            context.startActivity(intent);
        });

        if (allWallpaper.getIsBookmarked()){
            holders.fav.setOnClickListener(v -> {
                holders.imgFav.setImageResource(R.drawable.favorite_border_24);
                allWallpaper.setBookmarked(false);
                favClickListener.wallFavClick(position,false);
            });
        }else {
            holders.fav.setOnClickListener(v -> {
                holders.imgFav.setImageResource(R.drawable.favorite_24);
                allWallpaper.setBookmarked(true);
                Toast.makeText(context,"Added to favourite",Toast.LENGTH_SHORT).show();
                favClickListener.wallFavClick(position,true);
            });
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class AllVideos extends RecyclerView.ViewHolder{

        ImageView wallpaper,imgFav;
        TextView marquee;
        RelativeLayout fav,download,share;
        public AllVideos(@NonNull View itemView) {
            super(itemView);
            wallpaper = itemView.findViewById(R.id.wallpaper);
            marquee = itemView.findViewById(R.id.marquee);
            fav = itemView.findViewById(R.id.fav);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            imgFav = itemView.findViewById(R.id.imgFav);
        }
    }

    //====================================----------share----------==================
    private void shareImageAndText(Bitmap bitmap){
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Sharing Images");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject");
        intent.setType("image/");
        context.startActivity(Intent.createChooser(intent,"Share via"));
    }
    private Uri getImageToShare (Bitmap bitmap){
        File imageFolder = new File(context.getCacheDir(),"images");
        Uri uri = null;

        try {
            imageFolder.mkdir();
            File file = new File(imageFolder,"shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(context,"com.developerobaida.postcraft", file);
        } catch (Exception e){
            Toast.makeText(context, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }
    //====================================----------share----------==================

    private void saveImage(Bitmap combinedBitmap) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PostCraft");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        ContentResolver resolver = context.getContentResolver();

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            combinedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            if (outputStream != null) outputStream.close();
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, values, null, null);
            Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error found.\nImage not saved", Toast.LENGTH_SHORT).show();
        }
    }
}
