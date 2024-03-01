package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
public class SignUp extends AppCompatActivity {

    EditText input_name,input_email,input_password,input_number;
    Button buttonSignUp;
    ProgressBar progressBar;
    TextView textSignIn,textPrivacy;
    private FirebaseAuth mAuth;
    User user;
    FirebaseUser firebaseUser;
    DatabaseReference usersRef;
    String email,password,name,number,imageUrl;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(SignUp.this,SignIn.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.white));

        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_number = findViewById(R.id.input_number);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        progressBar = findViewById(R.id.progressbar);
        textSignIn = findViewById(R.id.textSignIn);
        textPrivacy = findViewById(R.id.textPrivacy);

        textPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(this,WebView.class);
            intent.putExtra("title","Privacy Policy");
            intent.putExtra("link","");
            startActivity(intent);
        });

        progressBar.getIndeterminateDrawable().setTint(Color.parseColor("#FFFFFF"));

        mAuth = FirebaseAuth.getInstance();

        buttonSignUp.setOnClickListener(v -> {
            if (isNetworkAvailable()) authUser();
            else Toast.makeText(SignUp.this,"No Internet",Toast.LENGTH_SHORT).show();
        });

        textSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this,SignIn.class);
            startActivity(intent);
        });
    }
    private void authUser(){
        progressBar.setVisibility(View.VISIBLE);
        buttonSignUp.setText("");

        email = input_email.getText().toString();
        password = input_password.getText().toString();
        name = input_name.getText().toString();
        number = input_number.getText().toString();


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(password)) {
            Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            buttonSignUp.setText("SIGN UP");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        buttonSignUp.setText("SIGN UP");
                        firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();


                        user = new User(email, number, name, password, imageUrl);

                        usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                        usersRef.setValue(user);


                        input_email.setText("");
                        input_number.setText("");
                        input_name.setText("");
                        input_password.setText("");
                        updateUI(firebaseUser);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        buttonSignUp.setText("SIGN UP");
                        Toast.makeText(SignUp.this, "This Email is invalid \nor already used", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateUI(FirebaseUser firebaseUser){
        Intent intent = new Intent(this,SignIn.class);
        startActivity(intent);
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