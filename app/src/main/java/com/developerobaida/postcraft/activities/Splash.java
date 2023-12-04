package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.developerobaida.postcraft.R;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (Build.VERSION.SDK_INT>=21){
            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.white));
        }
    }
}