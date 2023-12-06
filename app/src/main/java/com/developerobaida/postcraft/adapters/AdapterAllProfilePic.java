package com.developerobaida.postcraft.adapters;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.activities.ShowImage;
import com.developerobaida.postcraft.activities.ViewBookmark;
import com.developerobaida.postcraft.listeners.OnProfileItemClickListener;
import com.developerobaida.postcraft.model.BookmarkItem;
import com.developerobaida.postcraft.model.ItemAllProfilePic;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdapterAllProfilePic extends RecyclerView.Adapter {
    private boolean isAdded = false;
    ArrayList<ItemAllProfilePic> arrayList;
    Context context;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId = currentUser != null ? currentUser.getUid() : "NULL";
    DatabaseReference bookmarksRef;
    ItemAllProfilePic itemAllProfilePic;
    OnProfileItemClickListener listener;

    //-----------------------------------
    public void setFilter(ArrayList<ItemAllProfilePic> newList) {
        arrayList = new ArrayList<>();
        arrayList.addAll(newList);
        notifyDataSetChanged();
    }
    //------------------------------------

    public AdapterAllProfilePic(ArrayList<ItemAllProfilePic> arrayList, Context context, OnProfileItemClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.all_profile_item,parent,false);

        return new AllProfile(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AllProfile holder1 = (AllProfile) holder;
        itemAllProfilePic = arrayList.get(position);


        if (itemAllProfilePic.getBookmark().contains("0")){
            holder1.fav.setImageResource(R.drawable.favorite_border_24);
        } else  {
            holder1.fav.setImageResource(R.drawable.favorite_24);
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
            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder1.image_profile.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            saveImage(bitmap);
        });

        holder1.share.setOnClickListener(v -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) holder1.image_profile.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            shareImageAndText(bitmap);
        });
        Picasso.get().load(itemAllProfilePic.getImage()).placeholder(R.drawable.material_design_default).into(holder1.image_profile);
        holder1.image_profile.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShowImage.class);
            intent.putExtra("image",""+itemAllProfilePic.getImage());
            intent.putExtra("title",""+itemAllProfilePic.getTitle());
            intent.putExtra("type",false);
            context.startActivity(intent);
        });

        holder1.fav.setOnClickListener(v -> {

//            isAdded = !isAdded;
//
//            if(isAdded){
//                holder1.fav.setImageResource(R.drawable.favorite_24);
//                favControl(itemAllProfilePic.getId(),"1");
//                Toast.makeText(context,"Added",Toast.LENGTH_SHORT).show();
//                Log.d("FAV",""+position);
//            }else {
//                holder1.fav.setImageResource(R.drawable.favorite_border_24);
//                favControl(itemAllProfilePic.getId(),"0");
//                Toast.makeText(context,"Removed",Toast.LENGTH_SHORT).show();
//                Log.d("FAV2",""+position);
//            }

            listener.onProfileItemClick(itemAllProfilePic, holder1.fav, position);


//            if (itemAllProfilePic.getBookmark().contains("0")){
//                holder1.fav.setImageResource(R.drawable.favorite_24);
//                favControl(itemAllProfilePic.getId(),"1");
//                Toast.makeText(context,"Added",Toast.LENGTH_SHORT).show();
//                Log.d("FAV",""+position);
//
//            } else {
//                holder1.fav.setImageResource(R.drawable.favorite_border_24);
//                favControl(itemAllProfilePic.getId(),"0");
//                Toast.makeText(context,"Removed",Toast.LENGTH_SHORT).show();
//                Log.d("FAV2",""+position);
//            }



//            if (userId.contains("NULL")){
//                Toast.makeText(context,"Please Sign In",Toast.LENGTH_SHORT).show();
//            }else {
//
//
//                holder1.fav.setImageResource(R.drawable.favorite_24);
//                bookmarksRef = FirebaseDatabase.getInstance().getReference("users/" + userId + "/bookmarks");
//
//                String bookmarkId = bookmarksRef.push().getKey();
//                Map<String, Object> bookmarkData = new HashMap<>();
//                bookmarkData.put("title", itemAllProfilePic.getTitle());
//                bookmarkData.put("url", itemAllProfilePic.getImage());
//                bookmarkData.put("type", itemAllProfilePic.getType());
//                bookmarkData.put("timestamp", ServerValue.TIMESTAMP);
//                itemAllProfilePic.setBookmarked(true);
//                bookmarkData.put("isBookmarked",itemAllProfilePic.getIsBookmarked());
//
//                bookmarksRef.child(bookmarkId).setValue(bookmarkData);
//                Toast.makeText(context,"Bookmarked",Toast.LENGTH_SHORT).show();
//            }



        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    public class AllProfile extends RecyclerView.ViewHolder{
        ImageView image_profile,fav,download,share;
        TextView marquee;
        public AllProfile(@NonNull View itemView) {
            super(itemView);
            image_profile = itemView.findViewById(R.id.image_profile);
            fav = itemView.findViewById(R.id.fav);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            marquee = itemView.findViewById(R.id.marquee);
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
    private void favControl(String id,String bookmark){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://developerobaida.com/post_bank/fav_update.php", response -> {
            Log.d("Response fav","Response");
        }, error -> {
            Log.d("Error fav","error");
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> stringMap = new HashMap<>();
                stringMap.put("bookmark",bookmark);
                stringMap.put("id",id);
                return stringMap;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
