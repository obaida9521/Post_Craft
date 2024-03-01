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
import android.graphics.Matrix;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.database.DatabaseHelper;
import com.developerobaida.postcraft.model.ItemRecentPost;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class AdapterRecentPost extends RecyclerView.Adapter<AdapterRecentPost.RecentView> {
    ArrayList<ItemRecentPost> arrayList;
    Context context;
    DatabaseHelper database;
    int serverid;

    public interface RecentPostFavClickListener {
        void postFavClick(int position, boolean isFav);
    }
    private RecentPostFavClickListener favClickListener;
    public AdapterRecentPost(ArrayList<ItemRecentPost> arrayList, Context context,RecentPostFavClickListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.favClickListener = listener;
    }

    @NonNull
    @Override
    public RecentView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recent_post_item,parent,false);
        return new RecentView(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentView holder, int position) {
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ItemRecentPost recent = arrayList.get(position);
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

        Picasso.get().load(recent.getImage()).placeholder(R.drawable.material_design_default).into(holder.statusImage);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), ""+recent.getFont());
        holder.statusText.setShadowLayer(Float.parseFloat(recent.getRadius()),Float.parseFloat(recent.getDx()),Float.parseFloat(recent.getDy()),Color.parseColor(recent.getShadowColor()));
        holder.statusText.setText(recent.getStatus());
        int sizes = Integer.parseInt(recent.getSize());
        double percentageToSubtract = 0.23;
        double subtractedValue = sizes - (sizes * percentageToSubtract);
        sizes = (int) subtractedValue;

        holder.statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP,Float.parseFloat(String.valueOf(sizes)));
        holder.statusText.setTextColor(Color.parseColor(recent.getColor()));
        holder.statusText.setTypeface(typeface);

        holder.copy.setOnClickListener(v -> {
            ClipData data = ClipData.newPlainText("Text",holder.statusText.getText().toString());
            manager.setPrimaryClip(data);
            Toast.makeText(context,"Copied to clipboard",Toast.LENGTH_SHORT).show();
        });

        holder.download.setOnClickListener(v -> {
            Bitmap combinedBitmap = createCombinedBitmap(holder.statusImage, holder.statusText);
            saveCombinedImage(combinedBitmap);
        });

        holder.share.setOnClickListener(v -> {
            Bitmap combinedBitmap = createCombinedBitmap(holder.statusImage, holder.statusText);
            shareImageAndText(combinedBitmap);
        });

        database = new DatabaseHelper(context);
        Cursor cursor = database.getPost_fav();
        while (cursor.moveToNext()){
            serverid = cursor.getInt(11);
            if (serverid == Integer.parseInt(recent.getId())) {
                Log.d("id : ",recent.getId());
                holder.favImg.setImageResource(R.drawable.favorite_24);
                recent.setBookmarked(true);
                break;
            }
        }

        if (recent.getIsBookmarked()){

            holder.fav.setOnClickListener(v -> {
                holder.favImg.setImageResource(R.drawable.favorite_border_24);
                recent.setBookmarked(false);
                favClickListener.postFavClick(position,false);
            });

        }else {
            holder.fav.setOnClickListener(v -> {
                holder.favImg.setImageResource(R.drawable.favorite_24);
                recent.setBookmarked(true);
                Toast.makeText(context,"Added to favourite",Toast.LENGTH_SHORT).show();
                favClickListener.postFavClick(position,true);
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class RecentView extends RecyclerView.ViewHolder{
        TextView statusText;
        ImageView statusImage,favImg;
        RelativeLayout copy;
        RelativeLayout fav,download,share;
        public RecentView(@NonNull View itemView) {
            super(itemView);

            statusText = itemView.findViewById(R.id.statusText);
            statusImage = itemView.findViewById(R.id.statusImage);
            fav = itemView.findViewById(R.id.fav);
            download = itemView.findViewById(R.id.download);
            share = itemView.findViewById(R.id.share);
            copy = itemView.findViewById(R.id.copy);
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
            Log.d("Exception",e.getMessage().toString());
        }
        return uri;
    }
    private Bitmap createCombinedBitmap(ImageView backgroundImageView, TextView captionTextView) {
        Bitmap backgroundBitmap = Bitmap.createBitmap(backgroundImageView.getWidth(),backgroundImageView.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBitmap);
        backgroundImageView.draw(canvas);

        Matrix originalMatrix = canvas.getMatrix();
        canvas.setMatrix(new Matrix());

        captionTextView.measure(
                View.MeasureSpec.makeMeasureSpec(backgroundImageView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(backgroundImageView.getHeight(), View.MeasureSpec.EXACTLY)
        );
        captionTextView.layout(0, 0, captionTextView.getMeasuredWidth(), captionTextView.getMeasuredHeight());

        int x = (backgroundImageView.getWidth() - captionTextView.getMeasuredWidth()) / 2;
        int y = (backgroundImageView.getHeight() - captionTextView.getMeasuredHeight()) / 2;

        canvas.translate(x, y);
        captionTextView.draw(canvas);

        canvas.setMatrix(originalMatrix);

        return backgroundBitmap;
    }

    private void saveCombinedImage(Bitmap combinedBitmap) {
        if (combinedBitmap == null) {
            Toast.makeText(context, "Error: Combined Bitmap is null", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PostCraft");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        ContentResolver resolver = context.getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            if (outputStream != null) {
                combinedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
                values.clear();
                values.put(MediaStore.Images.Media.IS_PENDING, 0);
                resolver.update(imageUri, values, null, null);
                Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show();
            } else Toast.makeText(context, "Error: Output stream is null", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error found. Image not saved", Toast.LENGTH_SHORT).show();
        }
    }
    //====================================----------share----------==================
    //====================================----------share----------==================
}
