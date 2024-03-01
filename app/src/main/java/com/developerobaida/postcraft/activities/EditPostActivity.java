package com.developerobaida.postcraft.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
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
import com.yalantis.ucrop.UCrop;

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
    CardView text_size,text_color,text_style,text_font,background,text_shadow,text_edit,add;
    SeekBar seekBar,shadow_r,shadow_v,shadow_h;
    RecyclerView recBackground,recFont;
    ImageView edit_image,back;
    boolean isBold = false,isVisible = false,isShowing = false,isShowing2=false;
    ArrayList<ItemImageBackground> arrayList = new ArrayList<>();
    ArrayList<ItemFont> list = new ArrayList<>();
    AdapterBackgroundImage adapterBackgroundImage;
    AdapterFont adapterFont;
    Button shadow_c,done,cancel;
    LinearLayout laySeek;
    RelativeLayout save,share;
    String hexColorShadow,dx,dy,radius;
    float dxe,dye,radiuse;
    EditText edit;
    Spinner spinner,spinner2;
    CardView card,card2;
    ProgressDialog progressDialog;

    //******************************************************
    float[] lastEvent = null;
    float d = 0f;
    float newRot = 0f;
    private boolean isZoomAndRotate;
    private boolean isOutSide;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float oldDist = 1f;
    private float xCoOrdinate, yCoOrdinate;
    //******************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        Window window = getWindow();
        window.setStatusBarColor(this.getColor(R.color.white));
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
        spinner = findViewById(R.id.spinner);
        spinner2 = findViewById(R.id.spinner2);
        card = findViewById(R.id.card);
        card2 = findViewById(R.id.card2);
        add = findViewById(R.id.add);
        card.setVisibility(View.GONE);
        card2.setVisibility(View.GONE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);



        spinnerBackgroundImg();
        spinnerFont();

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
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

        Picasso.get().load(backImage).placeholder(R.drawable.material_design_default).into(edit_image);
        text.setText(status);
        text.setTypeface(typeface);
        text.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(size));
        text.setTextColor(Color.parseColor(color));
        text.setShadowLayer(Float.parseFloat(radius),Float.parseFloat(dx),Float.parseFloat(dy), Color.parseColor(shadow));

        //******************************************************
        text.setOnTouchListener((v, event) -> {

            TextView view = (TextView) v;
            view.bringToFront();
            viewTransformation(view, event);
            return true;
        });
        //******************************************************

        add.setOnClickListener(v -> launcher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

        text.setOnClickListener(v -> setText());
        text_edit.setOnClickListener(v -> setText());
        text_font.setOnClickListener(v -> setFont());
        background.setOnClickListener(v -> getBackground());


        text_shadow.setOnClickListener(v -> {

            ColorPickerDialogBuilder.with(this).setTitle("Select Shadow Color")
                    .initialColor(Color.WHITE).wheelType(ColorPickerView.WHEEL_TYPE.FLOWER).density(14)
                    .setPositiveButton("Ok", (dialog, selectedColor, allColors) -> {
                        hexColorShadow = String.format("#%06X", (0xFFFFFF & selectedColor));
                        text.setShadowLayer(radiuse, dxe, dye, selectedColor);
                        openSheet();
                    }).setNegativeButton("Cancel", (dialog, which) -> {}).build().show();
        });

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
                    spinner.setVisibility(View.GONE);
                    card.setVisibility(View.GONE);
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
            progressDialog.setTitle("Image is saving");
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
            Bitmap combinedBitmap = createCombinedBitmap(edit_image, text);
            saveCombinedImage(combinedBitmap);
        });
        share.setOnClickListener(v -> {

            progressDialog.setTitle("Image is preparing to share");
            progressDialog.setMessage("Please wait..");
            progressDialog.show();
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
            progressDialog.dismiss();
            imageFolder.mkdir();
            File file = new File(imageFolder,"shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(this,"com.developerobaida.postcraft", file);
        } catch (Exception e){
            progressDialog.dismiss();
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
            progressDialog.dismiss();
            Toast.makeText(this, "Image saved to gallery", Toast.LENGTH_SHORT).show();

            //--------------------------------------------

            //--------------------------------------------
        } catch (IOException e) {
            e.printStackTrace();
            progressDialog.dismiss();
            Toast.makeText(this, "Error found.\nImage not saved", Toast.LENGTH_SHORT).show();
        }
    }
    //====================================----------share----------==================
    //====================================----------share----------==================

    public void fontList(){

        //=========================bengali fonts===========
        list.add(new ItemFont("abidul.ttf","Bangla"));
        list.add(new ItemFont("alkatra-regular.ttf","Bangla"));
        list.add(new ItemFont("alkatra-bold.ttf","Bangla"));
        list.add(new ItemFont("balooda2-bold.ttf","Bangla"));
        list.add(new ItemFont("balooda-medium.ttf","Bangla"));
        list.add(new ItemFont("hindsiliguri_bold.ttf","Bangla"));
        list.add(new ItemFont("hindsiliguri_light.ttf","Bangla"));
        list.add(new ItemFont("hindsiliguri_regular.ttf","Bangla"));
        list.add(new ItemFont("lashkar.ttf","Bangla"));
        list.add(new ItemFont("liyakot.ttf","Bangla"));
        list.add(new ItemFont("mina_bold.ttf","Bangla"));
        list.add(new ItemFont("mina_regular.ttf","Bangla"));

        //==================english fonts==================
        list.add(new ItemFont("allamanda.otf","English/Arabic"));
        list.add(new ItemFont("embrodery.otf","English/Arabic"));
        list.add(new ItemFont("enigma.otf","English/Arabic"));
        list.add(new ItemFont("mockup.otf","English/Arabic"));
        list.add(new ItemFont("santa.otf","English/Arabic"));
        list.add(new ItemFont("technical.otf","English/Arabic"));
        list.add(new ItemFont("kalpurush.ttf","English/Arabic"));
        list.add(new ItemFont("amaticsc-bold.ttf","English/Arabic"));
        list.add(new ItemFont("astro.ttf","English/Arabic"));
        list.add(new ItemFont("authentic_claisha.ttf","English/Arabic"));
        list.add(new ItemFont("banurasmie.ttf","English/Arabic"));
        list.add(new ItemFont("caveat_regular.ttf","English/Arabic"));
        list.add(new ItemFont("chakra_petch_light.ttf","English/Arabic"));
        list.add(new ItemFont("chinese.ttf","English/Arabic"));
        list.add(new ItemFont("christmas.ttf","English/Arabic"));
        list.add(new ItemFont("flower_regular.ttf","English/Arabic"));
        list.add(new ItemFont("kdam_thmor.ttf","English/Arabic"));
        list.add(new ItemFont("macondo.ttf","English/Arabic"));
        list.add(new ItemFont("marhey.ttf","English/Arabic"));
        list.add(new ItemFont("ruwudu.ttf","English/Arabic"));
        list.add(new ItemFont("teko.ttf","English/Arabic"));
        list.add(new ItemFont("titan.ttf","English/Arabic"));
        list.add(new ItemFont("zeyada_regular.ttf","English/Arabic"));
        list.add(new ItemFont("motion.ttf","English/Arabic"));
        list.add(new ItemFont("ooiret_one.ttf","English/Arabic"));
        list.add(new ItemFont("pacifico.ttf","English/Arabic"));
        list.add(new ItemFont("raleway_thin.ttf","English/Arabic"));
        list.add(new ItemFont("readex_light.ttf","English/Arabic"));
        list.add(new ItemFont("reem.ttf","English/Arabic"));
        list.add(new ItemFont("roboto.ttf","English/Arabic"));

        //==========================arbi fonts=============
        list.add(new ItemFont("alkalami_regular.ttf","English/Arabic"));
        list.add(new ItemFont("amiri_bold.ttf","English/Arabic"));
        list.add(new ItemFont("amiri_regular.ttf","English/Arabic"));
        list.add(new ItemFont("arefruqaa_bold.ttf","English/Arabic"));
        list.add(new ItemFont("arefruqaa_regular.ttf","English/Arabic"));
        list.add(new ItemFont("blaka_regular.ttf","English/Arabic"));
        list.add(new ItemFont("cairo_light.ttf","English/Arabic"));
        list.add(new ItemFont("changa_light.ttf","English/Arabic"));
        list.add(new ItemFont("changa_medium.ttf","English/Arabic"));
        list.add(new ItemFont("elmessiri_regular.ttf","English/Arabic"));
        list.add(new ItemFont("kufam_medium.ttf","English/Arabic"));
        list.add(new ItemFont("mada_light.ttf","English/Arabic"));
        list.add(new ItemFont("mada_bold.ttf","English/Arabic"));
        list.add(new ItemFont("qahiri_regular.ttf","English/Arabic"));
        list.add(new ItemFont("tajawal_extralight.ttf","English/Arabic"));
        list.add(new ItemFont("tajawal_regular.ttf","English/Arabic"));
        list.add(new ItemFont("vibes_regular.ttf","English/Arabic"));

    }
    private void getBackground(){
        if (!isShowing){
            background.setBackgroundColor(Color.parseColor("#CDDFFF"));
            if (adapterBackgroundImage!= null){
                recBackground.setVisibility(View.VISIBLE);
                card2.setVisibility(View.VISIBLE);
                isShowing = true;
            }else {
                recBackground.setVisibility(View.VISIBLE);
                card2.setVisibility(View.VISIBLE);
                String url ="https://developerobaida.com/post_bank/show_background.php";
                String location = "https://developerobaida.com/post_bank/Background/";
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                    try {
                        for (int i=0; i<response.length();i++){
                            JSONObject jsonObject = response.getJSONObject(i);
                            String img = jsonObject.getString("image");
                            String category = jsonObject.getString("category");
                            arrayList.add(new ItemImageBackground(location+img,category));
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
            card2.setVisibility(View.GONE);
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
    private void setText(){
        final BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(this,R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet2,(LinearLayout)findViewById(R.id.bottom_sheet_container2));
        edit = bottomSheetView.findViewById(R.id.edit);
        done = bottomSheetView.findViewById(R.id.done);
        cancel = bottomSheetView.findViewById(R.id.cancel);

        edit.setText(text.getText().toString());
        done.setOnClickListener(v1 -> {
            String txt = edit.getText().toString();
            text.setText(txt);
            bottomSheetDialog2.dismiss();
        });
        cancel.setOnClickListener(v1 -> bottomSheetDialog2.dismiss());

        bottomSheetDialog2.setContentView(bottomSheetView);
        bottomSheetDialog2.show();
    }
    private void setFont(){
        if (!isShowing2){
            text_font.setBackgroundColor(Color.parseColor("#CDDFFF"));

            if (adapterFont!=null){
                recFont.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                card.setVisibility(View.VISIBLE);
                isShowing2 = true;
                if (isVisible){
                    text_size.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    laySeek.setVisibility(View.GONE);
                    isVisible = false;
                }
            }else {
                recFont.setVisibility(View.VISIBLE);
                spinner.setVisibility(View.VISIBLE);
                card.setVisibility(View.VISIBLE);
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
            card.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            isShowing2 = false;
        }
    }

    //=====================================================================-------------------------------
    private void spinnerFont(){
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.font_category, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (list != null && list.size() > 0) {
                    String selectedCategory = spinner.getSelectedItem().toString();
                    if (selectedCategory.equals("All")) adapterFont.setFilter(list);
                    else {
                        ArrayList<ItemFont> filteredList = filterByCategory(list, selectedCategory);
                        adapterFont.setFilter(filteredList);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }
    private ArrayList<ItemFont> filterByCategory(ArrayList<ItemFont> items, String category) {
        ArrayList<ItemFont> filteredList = new ArrayList<>();
        for (ItemFont item : items) if (item.getCategory().equalsIgnoreCase(category)) filteredList.add(item);
        return filteredList;
    }
    //=====================================================================-------------------------------

    //=====================================================================-------------------------------
    private void spinnerBackgroundImg(){
        ArrayAdapter<CharSequence> spinnerAdapter2 = ArrayAdapter.createFromResource(this, R.array.background_category, android.R.layout.simple_spinner_item);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(spinnerAdapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (arrayList != null && arrayList.size() > 0) {
                    String selectedCategory = spinner2.getSelectedItem().toString();
                    if (selectedCategory.equals("All")) adapterBackgroundImage.setFilter(arrayList);
                    else {
                        ArrayList<ItemImageBackground> filteredList = filterByCategory2(arrayList, selectedCategory);
                        adapterBackgroundImage.setFilter(filteredList);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });
    }
    private ArrayList<ItemImageBackground> filterByCategory2(ArrayList<ItemImageBackground> items, String category) {
        ArrayList<ItemImageBackground> filteredList = new ArrayList<>();
        for (ItemImageBackground item : items) if (item.getCategory().equalsIgnoreCase(category)) filteredList.add(item);
        return filteredList;
    }
    //=====================================================================-------------------------------

    private void viewTransformation(View view, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                xCoOrdinate = view.getX() - event.getRawX();
                yCoOrdinate = view.getY() - event.getRawY();

                start.set(event.getX(), event.getY());
                isOutSide = false;
                mode = DRAG;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                if (oldDist > 10f) {
                    midPoint(mid, event);
                    mode = ZOOM;
                }

                lastEvent = new float[4];
                lastEvent[0] = event.getX(0);
                lastEvent[1] = event.getX(1);
                lastEvent[2] = event.getY(0);
                lastEvent[3] = event.getY(1);
                d = rotation(event);
                break;
            case MotionEvent.ACTION_UP:
                isZoomAndRotate = false;
                if (mode == DRAG) {
                    float x = event.getX();
                    float y = event.getY();
                }
            case MotionEvent.ACTION_OUTSIDE:
                isOutSide = true;
                mode = NONE;
                lastEvent = null;
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                lastEvent = null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isOutSide) {
                    if (mode == DRAG) {
                        isZoomAndRotate = false;
                        view.animate().x(event.getRawX() + xCoOrdinate).y(event.getRawY() + yCoOrdinate).setDuration(0).start();
                    }
                    if (mode == ZOOM && event.getPointerCount() == 2) {
                        float newDist1 = spacing(event);
                        if (newDist1 > 10f) {
                            float scale = newDist1 / oldDist * view.getScaleX();
                            view.setScaleX(scale);
                            view.setScaleY(scale);
                        }
                        if (lastEvent != null) {
                            newRot = rotation(event);
                            view.setRotation((float) (view.getRotation() + (newRot - d)));
                        }
                    }
                }
                break;
        }
    }

    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri o) {
            if (o!=null) {
                Picasso.get().load(o).into(edit_image);
            }else Toast.makeText(EditPostActivity.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    });

    private float rotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (int) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

}