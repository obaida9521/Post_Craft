package com.developerobaida.postcraft.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.activities.EditPostActivity;
import com.developerobaida.postcraft.database.DatabaseHelper;
import com.developerobaida.postcraft.model.ItemAllPost;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class AdapterAllPost extends RecyclerView.Adapter{
    ArrayList<ItemAllPost> arrayList;
    Context context;
    DatabaseHelper database;
    int serverid;

    public interface PostFavClickListener {
        void postFavClick(int position, boolean isFav);
    }
    private PostFavClickListener favClickListener;
    //-----------------------------------
    public void setFilter(ArrayList<ItemAllPost> newList) {
        arrayList = new ArrayList<>();
        arrayList.addAll(newList);
        notifyDataSetChanged();
    }
    //------------------------------------
    public AdapterAllPost(ArrayList arrayList, Context context, PostFavClickListener listener) {
        if (arrayList == null) this.arrayList = new ArrayList<>();
        else this.arrayList = arrayList;

        this.context = context;
        this.favClickListener = listener;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.all_post_item,parent,false);

        return new AllPostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        AllPostViewHolder holder1 = (AllPostViewHolder) holder;
        ItemAllPost itemAllPost = arrayList.get(position);
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

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), ""+itemAllPost.getFont());
        holder1.text_caption.setShadowLayer(Float.parseFloat(itemAllPost.getRadius()),Float.parseFloat(itemAllPost.getDx()),Float.parseFloat(itemAllPost.getDy()),Color.parseColor(itemAllPost.getShadowColor()));
        holder1.text_caption.setText(itemAllPost.getStatus());
        holder1.text_caption.setTextSize(TypedValue.COMPLEX_UNIT_SP,Float.parseFloat(itemAllPost.getSize()));
        holder1.text_caption.setTextColor(Color.parseColor(itemAllPost.getColor()));

        holder1.text_caption.setTypeface(typeface);

        Picasso.get().load(itemAllPost.getImage()).placeholder(R.drawable.material_design_default).into(holder1.image_background);
        holder1.edit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditPostActivity.class);
            intent.putExtra("status",itemAllPost.getStatus());
            intent.putExtra("image", ""+itemAllPost.getImage());
            intent.putExtra("font",""+itemAllPost.getFont());
            intent.putExtra("size",""+itemAllPost.getSize());
            intent.putExtra("color",""+itemAllPost.getColor());
            intent.putExtra("dx",""+itemAllPost.getDx());
            intent.putExtra("dy",""+itemAllPost.getDy());
            intent.putExtra("radius",""+itemAllPost.getRadius());
            intent.putExtra("shadowColor",""+itemAllPost.getShadowColor());
            context.startActivity(intent);

        });


        holder1.copy_text.setOnClickListener(v -> {
            ClipData data = ClipData.newPlainText("Text",holder1.text_caption.getText().toString());
            manager.setPrimaryClip(data);

            Toast.makeText(context,"Copied to clipboard",Toast.LENGTH_SHORT).show();
        });
        holder1.download.setOnClickListener(v -> {
            Bitmap combinedBitmap = createCombinedBitmap(holder1.image_background, holder1.text_caption);
            saveCombinedImage(combinedBitmap);
        });

        holder1.share.setOnClickListener(v -> {
            Bitmap combinedBitmap = createCombinedBitmap(holder1.image_background, holder1.text_caption);
            shareImageAndText(combinedBitmap);
        });

        //===============================================================================================================
        database = new DatabaseHelper(context);
        Cursor cursor = database.getPost_fav();
        while (cursor.moveToNext()){
            serverid = cursor.getInt(11);
            if (serverid == Integer.parseInt(itemAllPost.getId())) {
                Log.d("id : ",itemAllPost.getId());
                holder1.fav.setImageResource(R.drawable.favorite_24);
                itemAllPost.setBookmarked(true);
                break;
            }
        }

        if (itemAllPost.getIsBookmarked()){

            holder1.fav.setOnClickListener(v -> {
                holder1.fav.setImageResource(R.drawable.favorite_border_24);
                itemAllPost.setBookmarked(false);
                favClickListener.postFavClick(position,false);
            });

        }else {
            holder1.fav.setOnClickListener(v -> {
                holder1.fav.setImageResource(R.drawable.favorite_24);
                itemAllPost.setBookmarked(true);
                Toast.makeText(context,"Added to favourite",Toast.LENGTH_SHORT).show();
                favClickListener.postFavClick(position,true);
            });
        }
        //===============================================================================================================
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class AllPostViewHolder extends RecyclerView.ViewHolder{
        TextView text_caption;
        ImageView copy_text,fav,download,share,image_background,edit;
        public AllPostViewHolder(@NonNull View itemView) {
            super(itemView);

            text_caption = itemView.findViewById(R.id.text_caption);
            image_background = itemView.findViewById(R.id.image_background);
            copy_text = itemView.findViewById(R.id.copy_text);
            fav = itemView.findViewById(R.id.fav);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            edit = itemView.findViewById(R.id.edit);

        }
    }
    //====================================----------share----------==================
    //====================================----------share----------==================
    private void shareImageAndText(Bitmap bitmap){
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Sharing Images");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject");
        intent.setType("image/");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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
    //====================================----------share----------==================
    //====================================----------share----------==================
}