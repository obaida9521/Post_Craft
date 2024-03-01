package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;

public class Splash extends AppCompatActivity {
    CardView card;
    Animation animation;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        card = findViewById(R.id.card);
        text = findViewById(R.id.text);

        animation = AnimationUtils.loadAnimation(this, R.anim.animation_from_bottom);
        card.startAnimation(animation);
        animation = AnimationUtils.loadAnimation(this, R.anim.visible);
        text.startAnimation(animation);

        Window window = getWindow();
        window.setStatusBarColor(this.getColor(R.color.white));
        final Handler handler = new Handler();



        if (isNetworkAvailable()){
            handler.postDelayed(() -> {
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();
            }, 3000);
        }

        else {
            Toast.makeText(Splash.this, "No internet connection", Toast.LENGTH_SHORT).show();
            handler.postDelayed(() -> {
                super.onBackPressed();
                finish();
            }, 3000);
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

}