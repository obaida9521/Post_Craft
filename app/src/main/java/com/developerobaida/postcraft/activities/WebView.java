package com.developerobaida.postcraft.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.developerobaida.postcraft.R;

public class WebView extends AppCompatActivity {

    android.webkit.WebView web;
    ImageView back;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Window window = getWindow();
        window.setStatusBarColor(this.getColor(R.color.background));
        web = findViewById(R.id.web);
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        Bundle bundle = getIntent().getExtras();
        String link = bundle.getString("link");
        String titles = bundle.getString("title");

        title.setText(titles);

        web.loadUrl(link);
        web.setWebViewClient(new WebViewClient());
        web.canGoBack();
        WebSettings webSettings = web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        checkInternet();

        back.setOnClickListener(v -> {
            super.onBackPressed();
            finish();
        });

    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
         else super.onBackPressed();
    }

    private void checkInternet() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if (networkInfo != null) {
            if (networkInfo.isConnected()) web.setVisibility(View.VISIBLE);
            else {
                web.setVisibility(View.GONE);
                dialog();
            }
        } else {
            web.setVisibility(View.GONE);
            dialog();
        }

    }

    public void dialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_ui, null);

        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        CardView positive = dialogView.findViewById(R.id.positive);
        CardView negative = dialogView.findViewById(R.id.negative);
        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView positiveTxt = dialogView.findViewById(R.id.positiveTxt);


        title.setText("Connection Problem!");
        message.setText("Check your Internet connection \nTry again");
        icon.setImageResource(R.drawable.wifi_off_24);
        positiveTxt.setText("Back");
        negative.setVisibility(View.GONE);

        AlertDialog alertDialog = new AlertDialog.Builder(WebView.this, R.style.AlertDialogTheme).setView(dialogView).create();

        positive.setOnClickListener(v -> {
            alertDialog.dismiss();
            super.onBackPressed();
        });

        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

}