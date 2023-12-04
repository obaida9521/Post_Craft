package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class ShowImage extends AppCompatActivity {
    LinearLayout download;
    ImageView image,back;
    TextView title,tt;
    RelativeLayout setWallpaper;
    ProgressBar pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        if (Build.VERSION.SDK_INT>=21){
            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.black));
        }

        download = findViewById(R.id.download);
        image = findViewById(R.id.image);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        setWallpaper = findViewById(R.id.setWallpaper);
        pr = findViewById(R.id.pr);
        tt = findViewById(R.id.tt);




        Bundle bundle = getIntent().getExtras();
        String img = bundle.getString("image");
        String sTitle = bundle.getString("title");

        Picasso.get().load(img).into(image);
        title.setSelected(true);
        title.setText(sTitle);


        setWallpaper.setOnClickListener(v -> {
            pr.setVisibility(View.VISIBLE);
            tt.setVisibility(View.GONE);
            new SetWallpaperTask().execute();
        });

        download.setOnClickListener(v -> {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) image.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();
            saveImage(bitmap);
        });

        back.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
    }
    private void saveImage(Bitmap combinedBitmap) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PostBank");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);
        ContentResolver resolver = getContentResolver();

        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));
            combinedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            if (outputStream != null) outputStream.close();
            values.clear();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            resolver.update(imageUri, values, null, null);
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error found.\nImage not saved", Toast.LENGTH_SHORT).show();
        }
    }

    private class SetWallpaperTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            setWallpaper.setEnabled(false);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            try {

                wallpaperManager.setBitmap(bitmap);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                setWallpaper.setEnabled(true);
                tt.setVisibility(View.VISIBLE);
                pr.setVisibility(View.GONE);
                Toast.makeText(ShowImage.this, "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
            } else {
                setWallpaper.setEnabled(true);
                tt.setVisibility(View.VISIBLE);
                pr.setVisibility(View.GONE);
                Toast.makeText(ShowImage.this, "Failed to set wallpaper", Toast.LENGTH_SHORT).show();
            }
        }
    }
}