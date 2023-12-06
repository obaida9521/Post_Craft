package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class SignUp extends AppCompatActivity {

    EditText input_name,input_email,input_password,input_number;
    Button buttonSignUp;
    ProgressBar progressBar;
    TextView textSignIn;
    private FirebaseAuth mAuth;
    StorageReference storageRef;
    User user;
    FirebaseUser firebaseUser;
    DatabaseReference usersRef;
    String imageUrl,email,password,name,number;
    RadioGroup radioGroup;
    RadioButton radioButton;
    int pic,gender;
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

        if (Build.VERSION.SDK_INT>=21){
            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.white));
        }
        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_number = findViewById(R.id.input_number);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        progressBar = findViewById(R.id.progressbar);
        textSignIn = findViewById(R.id.textSignIn);
        radioGroup = findViewById(R.id.radioGroup);

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
        int id = radioGroup.getCheckedRadioButtonId();
        if (id == -1) {
            Toast.makeText(SignUp.this, "Please select gender", Toast.LENGTH_SHORT).show();
            return;
        }
        radioButton = findViewById(id);
        gender = radioGroup.indexOfChild(radioButton);
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(password)) {
            Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            buttonSignUp.setText("SIGN UP");
            return;
        }

        if (gender==0) pic = R.drawable.man;
        else if (gender==1)pic = R.drawable.woman;

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        buttonSignUp.setText("SIGN UP");
                        firebaseUser = mAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();

                        storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid + ".jpg");

                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),pic);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] data = baos.toByteArray();

                        storageRef.putBytes(data).addOnSuccessListener(taskSnapshot -> {
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                imageUrl = uri.toString();
                                user = new User(email,number,name,password,imageUrl);
                                user.setImageUrl(imageUrl);

                                usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                                usersRef.setValue(user);

                            });
                        }).addOnFailureListener(e -> Toast.makeText(SignUp.this, "Image upload failed.", Toast.LENGTH_SHORT).show());

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