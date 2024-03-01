package com.developerobaida.postcraft.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.activities.EditPostActivity;
import com.developerobaida.postcraft.activities.ShowImage;
import com.developerobaida.postcraft.model.BookmarkItem;
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
    public interface OnFavClickListener {
        void onFavClick(int position, int type);
    }
    private OnFavClickListener favClickListener;
    public void updateData(ArrayList<BookmarkItem> newData) {
        arrayList.clear();
        arrayList.addAll(newData);
        notifyDataSetChanged();
    }

    public AdapterBookmark(Context context, ArrayList<BookmarkItem> arrayList, OnFavClickListener listener) {
        this.context = context;
        this.arrayList = arrayList;
        this.favClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType==POST){
            View v = LayoutInflater.from(context).inflate(R.layout.all_post_item,parent,false);
            return new PostBm(v);
        } else if (viewType==PROFILE) {
            View v = LayoutInflater.from(context).inflate(R.layout.recent_profilepic_item,parent,false);
            return new ProfileBm(v);
        }else{
            View v = LayoutInflater.from(context).inflate(R.layout.item_wallpaper,parent,false);
            return new WallpaperBm(v);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {



        if (holder.getItemViewType()==POST){
            ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            item = arrayList.get(position);
            PostBm postBm = (PostBm) holder;
            postBm.fav.setImageResource(R.drawable.favorite_24);

            //====================================
            int sizes = Integer.parseInt(item.getSize());
            double percentageToSubtract = 0.50;
            double subtractedValue = sizes - (sizes * percentageToSubtract);
            sizes = (int) subtractedValue;

            //====================================
            //====================================
            float dxs = Float.parseFloat(item.getDx());
            float rPercentage = 0.50f;
            float rSubtracted = dxs - (dxs * rPercentage);
            dxs = rSubtracted;
            //====================================
            float dys = Float.parseFloat(item.getDy());
            float dPercentage = 0.50f;
            float dSubtracted = dys - (dys * dPercentage);
            dys = dSubtracted;
            //====================================

            Typeface typeface = Typeface.createFromAsset(context.getAssets(), ""+item.getFont());
            postBm.text_caption.setShadowLayer(Float.parseFloat(item.getRadius()),dxs,dys, Color.parseColor(item.getShadowColor()));
            postBm.text_caption.setText(item.getStatus());

            postBm.text_caption.setTextSize(TypedValue.COMPLEX_UNIT_SP,Float.parseFloat(String.valueOf(sizes)));
            postBm.text_caption.setTextColor(Color.parseColor(item.getColor()));
            postBm.text_caption.setTypeface(typeface);

            Picasso.get().load(item.getImage()).placeholder(R.drawable.material_design_default).into(postBm.image_background);

            postBm.copy_text.setOnClickListener(v -> {
                ClipData data = ClipData.newPlainText("Text",postBm.text_caption.getText().toString());
                manager.setPrimaryClip(data);

                Toast.makeText(context,"Copied to clipboard",Toast.LENGTH_SHORT).show();
            });
            postBm.fav.setOnClickListener(v -> {
                postBm.fav.setImageResource(R.drawable.favorite_border_24);
                favClickListener.onFavClick(position,0);
            });
            postBm.download.setOnClickListener(v -> {
                Bitmap combinedBitmap = createCombinedBitmap(postBm.image_background, postBm.text_caption);
                saveCombinedImage(combinedBitmap);
            });

            postBm.share.setOnClickListener(v -> {
                Bitmap combinedBitmap = createCombinedBitmap(postBm.image_background, postBm.text_caption);
                shareImageAndText(combinedBitmap);
            });
            postImg(postBm,position);
        }else if (holder.getItemViewType()==PROFILE){

            ProfileBm profile = (ProfileBm) holder;
            item = arrayList.get(position);
            profile.favImg.setImageResource(R.drawable.favorite_24);
            profile.marquee.setSelected(true);
            profile.marquee.setText(item.getTitle());

            profile.download.setOnClickListener(v ->{
                BitmapDrawable bitmapDrawable = (BitmapDrawable) profile.profilePic.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                saveImage(bitmap);
            });

            profile.share.setOnClickListener(v -> {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) profile.profilePic.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(bitmap);
            });

            Picasso.get().load(item.getUrl()).placeholder(R.drawable.material_design_default).into(profile.profilePic);

            profile.profilePic.setOnClickListener(v -> {
                Intent intent = new Intent(context, ShowImage.class);
                intent.putExtra("image",""+item.getUrl());
                intent.putExtra("title",""+item.getTitle());
                intent.putExtra("type",false);
                context.startActivity(intent);
            });

            profile.fav.setOnClickListener(v -> {
                profile.favImg.setImageResource(R.drawable.favorite_border_24);
                favClickListener.onFavClick(position,1);
            });
            profileImg(profile,position);

        }else {
            WallpaperBm wallpaperBm = (WallpaperBm) holder;
            item = arrayList.get(position);

            wallpaperBm.imgFav.setImageResource(R.drawable.favorite_24);
            Picasso.get().load(item.getUrl()).into(wallpaperBm.wallpaper);

            wallpaperBm.marquee.setSelected(true);
            wallpaperBm.marquee.setText(item.getTitle());

            wallpaperBm.wallpaper.setOnClickListener(v -> {
                Intent intent = new Intent(context, ShowImage.class);
                intent.putExtra("image",""+item.getUrl());
                intent.putExtra("title",""+item.getTitle());
                intent.putExtra("type",true);
                context.startActivity(intent);
            });

            wallpaperBm.download.setOnClickListener(v ->{
                BitmapDrawable bitmapDrawable = (BitmapDrawable) wallpaperBm.wallpaper.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                saveImage(bitmap);
            });

            wallpaperBm.share.setOnClickListener(v -> {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) wallpaperBm.wallpaper.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(bitmap);
            });

            wallpaperBm.fav.setOnClickListener(v -> {
                wallpaperBm.imgFav.setImageResource(R.drawable.favorite_border_24);
                favClickListener.onFavClick(position,2);
            });
            wallpaperImg(wallpaperBm,position);

        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PostBm extends RecyclerView.ViewHolder{

        ImageView image_background,copy_text,fav,download,share,edit;
        TextView text_caption;
        public PostBm(@NonNull View itemView) {
            super(itemView);
            fav = itemView.findViewById(R.id.fav);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            image_background = itemView.findViewById(R.id.image_background);
            text_caption = itemView.findViewById(R.id.text_caption);
            copy_text = itemView.findViewById(R.id.copy_text);
            edit = itemView.findViewById(R.id.edit);
        }
    }
    public class ProfileBm extends RecyclerView.ViewHolder{
        RelativeLayout fav,download,share;
        ImageView profilePic,favImg;
        TextView marquee;
        public ProfileBm(@NonNull View itemView) {
            super(itemView);
            fav = itemView.findViewById(R.id.fav);
            share = itemView.findViewById(R.id.share);
            download = itemView.findViewById(R.id.download);
            profilePic = itemView.findViewById(R.id.profilePic);
            marquee = itemView.findViewById(R.id.marquee);
            favImg = itemView.findViewById(R.id.favImg);
        }
    }
    public class WallpaperBm extends RecyclerView.ViewHolder{
        ImageView wallpaper,imgFav;
        TextView marquee;
        RelativeLayout fav,download,share;
        public WallpaperBm(@NonNull View itemView) {
            super(itemView);
            fav = itemView.findViewById(R.id.fav);
            imgFav = itemView.findViewById(R.id.imgFav);
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
    //================== status saving ==============
    private Bitmap createCombinedBitmap(ImageView backgroundImageView, TextView captionTextView) {
        Bitmap backgroundBitmap = Bitmap.createBitmap(backgroundImageView.getWidth(),backgroundImageView.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBitmap);
        backgroundImageView.draw(canvas);

        captionTextView.measure(
                View.MeasureSpec.makeMeasureSpec(backgroundImageView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(backgroundImageView.getHeight(), View.MeasureSpec.EXACTLY)
        );
        captionTextView.layout(0, 0, captionTextView.getMeasuredWidth(), captionTextView.getMeasuredHeight());

        int x = (backgroundImageView.getWidth() - captionTextView.getMeasuredWidth()) / 2;
        int y = (backgroundImageView.getHeight() - captionTextView.getMeasuredHeight()) / 2;

        canvas.translate(x, y);
        captionTextView.draw(canvas);

        return backgroundBitmap;
    }

    private void saveCombinedImage(Bitmap combinedBitmap) {

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
    //================== status saving ==============


    void profileImg(ProfileBm profile,int p) {
        if (p >= 0 && p < arrayList.size()) {
            BookmarkItem clickedItem = arrayList.get(p);
            profile.profilePic.setOnClickListener(v -> {
                Intent intent = new Intent(context, ShowImage.class);
                intent.putExtra("image",""+clickedItem.getUrl());
                intent.putExtra("title",""+clickedItem.getTitle());
                intent.putExtra("type",false);
                context.startActivity(intent);
            });
        }
    }
    void wallpaperImg(WallpaperBm wallpaper,int p) {
        if (p >= 0 && p < arrayList.size()) {
            BookmarkItem clickedItem = arrayList.get(p);
            wallpaper.wallpaper.setOnClickListener(v -> {
                Intent intent = new Intent(context, ShowImage.class);
                intent.putExtra("image",""+clickedItem.getUrl());
                intent.putExtra("title",""+clickedItem.getTitle());
                intent.putExtra("type",true);
                context.startActivity(intent);
            });
        }
    }
    void postImg(PostBm postBm,int p) {
        if (p >= 0 && p < arrayList.size()) {
            BookmarkItem clickedItem = arrayList.get(p);
            postBm.edit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditPostActivity.class);
                intent.putExtra("status",clickedItem.getStatus());
                intent.putExtra("image", ""+clickedItem.getImage());
                intent.putExtra("font",""+clickedItem.getFont());
                intent.putExtra("size",""+clickedItem.getSize());
                intent.putExtra("color",""+clickedItem.getColor());
                intent.putExtra("dx",""+clickedItem.getDx());
                intent.putExtra("dy",""+clickedItem.getDy());
                intent.putExtra("radius",""+clickedItem.getRadius());
                intent.putExtra("shadowColor",""+clickedItem.getShadowColor());
                context.startActivity(intent);
            });
        }
    }
}
