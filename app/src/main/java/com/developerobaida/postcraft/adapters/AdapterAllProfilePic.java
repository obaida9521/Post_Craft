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
import com.developerobaida.postcraft.model.ItemAllProfilePic;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class AdapterAllProfilePic extends RecyclerView.Adapter {
    ArrayList<ItemAllProfilePic> arrayList;
    Context context;
    ItemAllProfilePic itemAllProfilePic;
    DatabaseHelper database;
    int serverid;

    public interface ProfileFavClickListener {
        void profileFavClick(int position, boolean isFav);
    }
    private ProfileFavClickListener favClickListener;
    //-----------------------------------
    public void setFilter(ArrayList<ItemAllProfilePic> newList) {
        arrayList = new ArrayList<>();
        arrayList.addAll(newList);
        notifyDataSetChanged();
    }
    //------------------------------------

    public AdapterAllProfilePic(ArrayList<ItemAllProfilePic> arrayList, Context context, ProfileFavClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.favClickListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recent_profilepic_item,parent,false);
        return new AllProfile(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AllProfile holder1 = (AllProfile) holder;
        itemAllProfilePic = arrayList.get(position);

        database = new DatabaseHelper(context);
        Cursor cursor = database.getProfile_fav();
        while (cursor.moveToNext()){
            serverid = cursor.getInt(4);
            if (serverid == Integer.parseInt(itemAllProfilePic.getId())) {
                Log.d("id : ",itemAllProfilePic.getId());
                holder1.favImg.setImageResource(R.drawable.favorite_24);
                itemAllProfilePic.setBookmarked(true);
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

        holder1.marquee.setSelected(true);
        holder1.marquee.setText(itemAllProfilePic.getTitle());

        holder1.download.setOnClickListener(v ->{
            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder1.profilePic.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            saveImage(bitmap);
        });

        holder1.share.setOnClickListener(v -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder1.profilePic.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            shareImageAndText(bitmap);
        });
        Picasso.get().load(itemAllProfilePic.getImage()).into(holder1.profilePic);

        showImg(holder1,position);


        if (itemAllProfilePic.getIsBookmarked()){

            holder1.fav.setOnClickListener(v -> {
                holder1.favImg.setImageResource(R.drawable.favorite_border_24);
                itemAllProfilePic.setBookmarked(false);
                favClickListener.profileFavClick(position,false);
            });

        }else {
            holder1.fav.setOnClickListener(v -> {
                holder1.favImg.setImageResource(R.drawable.favorite_24);
                itemAllProfilePic.setBookmarked(true);
                Toast.makeText(context,"Added to favourite",Toast.LENGTH_SHORT).show();
                favClickListener.profileFavClick(position,true);
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class AllProfile extends RecyclerView.ViewHolder{
        RelativeLayout fav,download,share;
        ImageView profilePic,favImg;
        TextView marquee;
        public AllProfile(@NonNull View itemView) {
            super(itemView);

            fav = itemView.findViewById(R.id.fav);
            share = itemView.findViewById(R.id.share);
            download = itemView.findViewById(R.id.download);
            profilePic = itemView.findViewById(R.id.profilePic);
            marquee = itemView.findViewById(R.id.marquee);
            favImg = itemView.findViewById(R.id.favImg);
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
    void showImg(AllProfile holder1,int p){
        if (p >= 0 && p < arrayList.size()) {

            ItemAllProfilePic clickedItem = arrayList.get(p);
            holder1.profilePic.setOnClickListener(v -> {
                Intent intent = new Intent(context, ShowImage.class);
                intent.putExtra("image",""+clickedItem.getImage());
                intent.putExtra("title",""+clickedItem.getTitle());
                intent.putExtra("type",false);
                context.startActivity(intent);
            });
        }
    }
}
