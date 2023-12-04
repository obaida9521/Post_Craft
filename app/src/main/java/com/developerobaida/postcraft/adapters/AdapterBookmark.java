package com.developerobaida.postcraft.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.model.BookmarkItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class AdapterBookmark extends RecyclerView.Adapter{

    int POST = 0,PROFILE = 1,WALLPAPER = 2;
    Context context;
    ArrayList<BookmarkItem> arrayList;
    BookmarkItem item;
    DatabaseReference bookmarkRef;

    public AdapterBookmark(Context context, ArrayList<BookmarkItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==POST){
            View v = LayoutInflater.from(context).inflate(R.layout.all_post_item,parent,false);
            return new PostBm(v);
        } else if (viewType==PROFILE) {
            View v = LayoutInflater.from(context).inflate(R.layout.all_profile_item,parent,false);
            return new ProfileBm(v);
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.item_wallpaper,parent,false);
            return new WallpaperBm(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)==POST){

        } else if (getItemViewType(position)==PROFILE) {
            ProfileBm holderProfile = (ProfileBm) holder;
            item = arrayList.get(position);
            Picasso.get().load(item.getUrl()).into(holderProfile.image_profile);
            holderProfile.fav.setImageResource(R.drawable.favorite_24);
            holderProfile.marquee.setSelected(true);
            holderProfile.marquee.setText(item.getTitle());
            holderProfile.fav.setOnClickListener(v -> {

                bookmarkRef = FirebaseDatabase.getInstance().getReference("users/" + item.getUserId() + "/bookmarks/" + item.getBookmarkId());
                bookmarkRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        holderProfile.fav.setImageResource(R.drawable.favorite_border_24);
                        arrayList.remove(position);

                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        notifyDataSetChanged();
                    } else {
                        // Handle removal failure if needed
                    }
                });
            });
            holderProfile.download.setOnClickListener(v -> {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holderProfile.image_profile.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                saveImage(bitmap);
            });
            holderProfile.share.setOnClickListener(v -> {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) holderProfile.image_profile.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(bitmap);
            });
        }else {

        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PostBm extends RecyclerView.ViewHolder{

        ImageView image_background,copy_text,fav,download,share;
        TextView text_caption;
        public PostBm(@NonNull View itemView) {
            super(itemView);
            fav = itemView.findViewById(R.id.fav);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            image_background = itemView.findViewById(R.id.image_background);
            text_caption = itemView.findViewById(R.id.text_caption);
            copy_text = itemView.findViewById(R.id.copy_text);

        }
    }
    public class ProfileBm extends RecyclerView.ViewHolder{
        ImageView image_profile,fav,download,share;
        TextView marquee;
        public ProfileBm(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            fav = itemView.findViewById(R.id.fav);
            download = itemView.findViewById(R.id.download);
            marquee = itemView.findViewById(R.id.marquee);
            share = itemView.findViewById(R.id.share);
        }
    }
    public class WallpaperBm extends RecyclerView.ViewHolder{
        ImageView wallpaper,fav,download,share;
        TextView marquee;
        public WallpaperBm(@NonNull View itemView) {
            super(itemView);
            fav = itemView.findViewById(R.id.fav);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            marquee = itemView.findViewById(R.id.marquee);
            wallpaper = itemView.findViewById(R.id.wallpaper);
        }
    }

    @Override
    public int getItemViewType(int position) {
        item = arrayList.get(position);
        if (item.getType().contains("POST"))return POST;
        else if (item.getType().contains("PROFILE")) return PROFILE;
        else return  WALLPAPER;
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
