package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.adapters.AdapterBackgroundImage;
import com.developerobaida.postcraft.adapters.AdapterFont;
import com.developerobaida.postcraft.model.ItemFont;
import com.developerobaida.postcraft.model.ItemImageBackground;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;

public class EditPostActivity extends AppCompatActivity implements AdapterBackgroundImage.OnItemClickListener,AdapterFont.OnItemClickListener{
    TextView text,ts;
    CardView text_size,text_color,text_style,text_font,save,share,background,text_shadow,text_edit;
    SeekBar seekBar,shadow_r,shadow_v,shadow_h;
    RecyclerView recBackground,recFont;
    ImageView edit_image,back;
    boolean isBold = false,isVisible = false,isShowing = false,isShowing2=false;
    ArrayList<ItemImageBackground> arrayList = new ArrayList<>();
    ArrayList<ItemFont> list = new ArrayList<>();
    AdapterBackgroundImage adapterBackgroundImage;
    AdapterFont adapterFont;
    Button shadow_c,done;
    LinearLayout laySeek;
    String hexColorShadow,dx,dy,radius;
    float dxe,dye,radiuse;
    EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        text = findViewById(R.id.text);
        text_color = findViewById(R.id.text_color);
        text_size = findViewById(R.id.text_size);
        text_style = findViewById(R.id.text_style);
        text_font = findViewById(R.id.text_font);
        seekBar = findViewById(R.id.seekBar);
        ts = findViewById(R.id.ts);
        save = findViewById(R.id.save);
        share = findViewById(R.id.share);
        back = findViewById(R.id.back);
        recBackground = findViewById(R.id.recBackground);
        recFont = findViewById(R.id.recFont);
        edit_image = findViewById(R.id.edit_image);
        background = findViewById(R.id.background);
        text_shadow = findViewById(R.id.text_shadow);
        laySeek = findViewById(R.id.laySeek);
        text_edit = findViewById(R.id.text_edit);
        if (Build.VERSION.SDK_INT>=21){
            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.white));
        }


        Bundle bundle = getIntent().getExtras();
        String status = bundle.getString("status");
        String backImage = bundle.getString("image");
        String font = bundle.getString("font");
        String size = bundle.getString("size");
        String color = bundle.getString("color");
        dx = bundle.getString("dx");
        dy = bundle.getString("dy");
        radius = bundle.getString("radius");
        String shadow = bundle.getString("shadowColor");


        seekBar.setProgress(Integer.parseInt(size));

        Typeface typeface = Typeface.createFromAsset(getAssets(), ""+font);

        Picasso.get().load(backImage).into(edit_image);
        text.setText(status);
        text.setTypeface(typeface);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(size));
        text.setTextColor(Color.parseColor(color));
        text.setShadowLayer(Float.parseFloat(radius),Float.parseFloat(dx),Float.parseFloat(dy), Color.parseColor(shadow));


        text_edit.setOnClickListener(v -> {
            final BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);
            View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet2,(LinearLayout)findViewById(R.id.bottom_sheet_container2));
            edit = bottomSheetView.findViewById(R.id.edit);
            done = bottomSheetView.findViewById(R.id.done);

            done.setOnClickListener(v1 -> {
                String txt = edit.getText().toString();
                text.setText(txt);
                bottomSheetDialog2.dismiss();
            });

            bottomSheetDialog2.setContentView(bottomSheetView);
            bottomSheetDialog2.show();
        });


        text_shadow.setOnClickListener(v -> {

            ColorPickerDialogBuilder.with(this).setTitle("Select Shadow Color")
                    .initialColor(Color.WHITE).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(14)
                    .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                        hexColorShadow = String.format("#%06X", (0xFFFFFF & selectedColor));
                        text.setShadowLayer(radiuse, dxe, dye, selectedColor);
                        openSheet();
                    }).setNegativeButton("cancel", (dialog, which) -> {}).build().show();
        });


        text_font.setOnClickListener(v -> {
            if (!isShowing2){
                text_font.setBackgroundColor(Color.parseColor("#CDDFFF"));

                if (adapterFont!=null){
                    recFont.setVisibility(View.VISIBLE);
                    isShowing2 = true;
                    if (isVisible){
                        text_size.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        laySeek.setVisibility(View.GONE);
                        isVisible = false;
                    }
                }else {
                    recFont.setVisibility(View.VISIBLE);
                    fontList();
                    adapterFont = new AdapterFont(list,EditPostActivity.this);
                    adapterFont.setOnItemClickListener(this);
                    adapterFont.notifyDataSetChanged();
                    recFont.setLayoutManager(new LinearLayoutManager(EditPostActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    recFont.setAdapter(adapterFont);

                    if (isVisible){
                        text_size.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        laySeek.setVisibility(View.GONE);
                        isVisible = false;
                    }
                    isShowing2 = true;
                }

            }else if (isShowing2){

                text_font.setBackgroundColor(Color.parseColor("#FFFFFF"));
                recFont.setVisibility(View.GONE);
                isShowing2 = false;
            }
        });

        background.setOnClickListener(v -> getBackground());

        text_style.setOnClickListener(v -> {
            if (!isBold){
                text_style.setBackgroundColor(Color.parseColor("#CDDFFF"));
                text.setTypeface(null, Typeface.BOLD);
                isBold = true;
            }else {
                text_style.setBackgroundColor(Color.parseColor("#FFFFFF"));
                text.setTypeface(null, Typeface.NORMAL);
                isBold = false;
            }

        });
        text_size.setOnClickListener(v -> {
            if (!isVisible){
                laySeek.setVisibility(View.VISIBLE);
                text_size.setBackgroundColor(Color.parseColor("#CDDFFF"));
                isVisible = true;

                if (isShowing2){
                    text_font.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    recFont.setVisibility(View.GONE);
                    isShowing2 = false;
                }

            }else {
                laySeek.setVisibility(View.GONE);
                text_size.setBackgroundColor(Color.parseColor("#FFFFFF"));
                isVisible = false;
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP,Float.parseFloat(String.valueOf(progress)));
                ts.setText(progress+" sp");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        text_color.setOnClickListener(v -> ColorPickerDialogBuilder.with(EditPostActivity.this).setTitle("Text Color")
                .initialColor(Color.WHITE).wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE).density(14)
                .setPositiveButton("ok", (dialog, selectedColor, allColors) -> text.setTextColor(selectedColor))
                .setNegativeButton("cancel", (dialog, which) -> {}).build().show());

        save.setOnClickListener(v ->{
            Bitmap combinedBitmap = createCombinedBitmap(edit_image, text);
            saveCombinedImage(combinedBitmap);
        });
        share.setOnClickListener(v -> {
            Bitmap combinedBitmap = createCombinedBitmap(edit_image, text);
            shareImageAndText(combinedBitmap);
        });
        back.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });
    }
    @Override
    public void onItemClick(ItemImageBackground item) {
        String resource = item.getImage();
        Picasso.get().load(resource).into(edit_image);
        item.setSelected(true);
        for (ItemImageBackground otherItem : arrayList) if (otherItem != item) otherItem.setSelected(false);
        adapterBackgroundImage.notifyDataSetChanged();
    }
    @Override
    public void onItemClick(ItemFont item) {
        String resource = item.getFont();
        Typeface typeface = Typeface.createFromAsset(getAssets(), ""+resource);
        text.setTypeface(typeface);
        item.setSelected(true);

        for (ItemFont otherItem : list) if (otherItem != item) otherItem.setSelected(false);
        adapterFont.notifyDataSetChanged();
    }

    //====================================----------share----------==================
    //====================================----------share----------==================
    private void shareImageAndText(Bitmap bitmap){
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Sharing image");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject");
        intent.setType("image/");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent,"Share via"));
    }
    private Uri getImageToShare (Bitmap bitmap){
        File imageFolder = new File(this.getCacheDir(),"images");
        Uri uri = null;

        try {
            imageFolder.mkdir();
            File file = new File(imageFolder,"shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this,"com.developerobaida.postcraft", file);
        } catch (Exception e){
            Toast.makeText(this, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private Bitmap createCombinedBitmap(ImageView backgroundImageView, TextView captionTextView) {
        Bitmap backgroundBitmap = Bitmap.createBitmap(backgroundImageView.getWidth(), backgroundImageView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(backgroundBitmap);
        backgroundImageView.draw(canvas);

        captionTextView.measure(
                View.MeasureSpec.makeMeasureSpec(backgroundImageView.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(backgroundImageView.getHeight(), View.MeasureSpec.EXACTLY));

        captionTextView.layout(0, 0, captionTextView.getMeasuredWidth(),captionTextView.getMeasuredHeight());

        int x = (backgroundImageView.getWidth() - captionTextView.getMeasuredWidth()) / 2;
        int y = (backgroundImageView.getHeight() - captionTextView.getMeasuredHeight()) / 2;

        canvas.translate(x, y);
        captionTextView.draw(canvas);

        return backgroundBitmap;
    }

    private void saveCombinedImage(Bitmap combinedBitmap) {

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
    //====================================----------share----------==================
    //====================================----------share----------==================

    public void fontList(){

        //=========================bengali fonts===========
        list.add(new ItemFont("abidul.ttf"));
        list.add(new ItemFont("alkatra-regular.ttf"));
        list.add(new ItemFont("alkatra-bold.ttf"));
        list.add(new ItemFont("balooda2-bold.ttf"));
        list.add(new ItemFont("balooda-medium.ttf"));
        list.add(new ItemFont("hindsiliguri_bold.ttf"));
        list.add(new ItemFont("hindsiliguri_light.ttf"));
        list.add(new ItemFont("hindsiliguri_regular.ttf"));
        list.add(new ItemFont("lashkar.ttf"));
        list.add(new ItemFont("liyakot.ttf"));
        list.add(new ItemFont("mina_bold.ttf"));
        list.add(new ItemFont("mina_regular.ttf"));

        //==================english fonts==================
        list.add(new ItemFont("allamanda.otf"));
        list.add(new ItemFont("embrodery.otf"));
        list.add(new ItemFont("enigma.otf"));
        list.add(new ItemFont("mockup.otf"));
        list.add(new ItemFont("santa.otf"));
        list.add(new ItemFont("technical.otf"));
        list.add(new ItemFont("kalpurush.ttf"));
        list.add(new ItemFont("amaticsc-bold.ttf"));
        list.add(new ItemFont("astro.ttf"));
        list.add(new ItemFont("authentic_claisha.ttf"));
        list.add(new ItemFont("banurasmie.ttf"));
        list.add(new ItemFont("caveat_regular.ttf"));
        list.add(new ItemFont("chakra_petch_light.ttf"));
        list.add(new ItemFont("chinese.ttf"));
        list.add(new ItemFont("christmas.ttf"));
        list.add(new ItemFont("flower_regular.ttf"));
        list.add(new ItemFont("kdam_thmor.ttf"));
        list.add(new ItemFont("macondo.ttf"));
        list.add(new ItemFont("marhey.ttf"));
        list.add(new ItemFont("ruwudu.ttf"));
        list.add(new ItemFont("teko.ttf"));
        list.add(new ItemFont("titan.ttf"));
        list.add(new ItemFont("zeyada_regular.ttf"));
        list.add(new ItemFont("motion.ttf"));
        list.add(new ItemFont("ooiret_one.ttf"));
        list.add(new ItemFont("pacifico.ttf"));
        list.add(new ItemFont("raleway_thin.ttf"));
        list.add(new ItemFont("readex_light.ttf"));
        list.add(new ItemFont("reem.ttf"));
        list.add(new ItemFont("roboto.ttf"));

        //==========================arbi fonts=============
        list.add(new ItemFont("alkalami_regular.ttf"));
        list.add(new ItemFont("amiri_bold.ttf"));
        list.add(new ItemFont("amiri_regular.ttf"));
        list.add(new ItemFont("arefruqaa_bold.ttf"));
        list.add(new ItemFont("arefruqaa_regular.ttf"));
        list.add(new ItemFont("blaka_regular.ttf"));
        list.add(new ItemFont("cairo_light.ttf"));
        list.add(new ItemFont("changa_light.ttf"));
        list.add(new ItemFont("changa_medium.ttf"));
        list.add(new ItemFont("elmessiri_regular.ttf"));
        list.add(new ItemFont("kufam_medium.ttf"));
        list.add(new ItemFont("mada_light.ttf"));
        list.add(new ItemFont("mada_bold.ttf"));
        list.add(new ItemFont("qahiri_regular.ttf"));
        list.add(new ItemFont("tajawal_extralight.ttf"));
        list.add(new ItemFont("tajawal_regular.ttf"));
        list.add(new ItemFont("vibes_regular.ttf"));
    }
    private void getBackground(){
        if (!isShowing){
            String url ="https://developerobaida.com/practice/test_post_bank/background.json";
            background.setBackgroundColor(Color.parseColor("#CDDFFF"));
            if (adapterBackgroundImage!= null){
                recBackground.setVisibility(View.VISIBLE);
                isShowing = true;
            }else {
                recBackground.setVisibility(View.VISIBLE);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                    try {
                        for (int i=0; i<response.length();i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            String img = jsonObject.getString("background");
                            arrayList.add(new ItemImageBackground(img));
                        }
                        adapterBackgroundImage = new AdapterBackgroundImage(arrayList, EditPostActivity.this);
                        adapterBackgroundImage.setOnItemClickListener(this);
                        adapterBackgroundImage.notifyDataSetChanged();
                        recBackground.setLayoutManager(new LinearLayoutManager(EditPostActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        recBackground.setAdapter(adapterBackgroundImage);
                        isShowing = true;

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, error -> Toast.makeText(this, ""+error.toString(), Toast.LENGTH_SHORT).show());
                RequestQueue requestQueue = Volley.newRequestQueue(EditPostActivity.this);
                requestQueue.add(jsonArrayRequest);
            }
        }else if (isShowing){
            recBackground.setVisibility(View.GONE);
            background.setBackgroundColor(Color.parseColor("#FFFFFF"));
            isShowing = false;
        }
    }

    private void openSheet(){
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet,(LinearLayout)findViewById(R.id.bottom_sheet_container));
        shadow_v = bottomSheetView.findViewById(R.id.shadow_v);
        shadow_h = bottomSheetView.findViewById(R.id.shadow_h);
        shadow_r = bottomSheetView.findViewById(R.id.shadow_r);
        shadow_c = bottomSheetView.findViewById(R.id.shadow_c);
        bottomSheetView.findViewById(R.id.ok).setOnClickListener(v1 -> bottomSheetDialog.dismiss());


        shadow_v.setProgress((int) Float.parseFloat(dy));
        shadow_h.setProgress((int) Float.parseFloat(dx));
        shadow_r.setProgress((int) Float.parseFloat(radius));
        //===========================================================================================================================
        shadow_v.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dye = progress;
                text.setShadowLayer(radiuse,dxe,dye,Color.parseColor(hexColorShadow));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        shadow_h.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dxe = progress;
                text.setShadowLayer(radiuse,dxe,dye,Color.parseColor(hexColorShadow));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        shadow_r.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radiuse = progress;
                text.setShadowLayer(radiuse,dxe,dye,Color.parseColor(hexColorShadow));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        shadow_c.setOnClickListener(v -> {
            ColorPickerDialogBuilder.with(this).setTitle("Select Shadow Color")
                    .initialColor(Color.WHITE).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(14)
                    .setPositiveButton("ok", (dialog, selectedColor, allColors) -> {
                        hexColorShadow = String.format("#%06X", (0xFFFFFF & selectedColor));
                        text.setShadowLayer(radiuse, dxe, dye, selectedColor);
                    }).setNegativeButton("cancel", (dialog, which) -> {}).build().show();
        });
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}