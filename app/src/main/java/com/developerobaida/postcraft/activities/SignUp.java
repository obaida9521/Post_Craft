package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
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
    TextView textSignIn;
    private FirebaseAuth mAuth;
    User user;
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

        input_name = findViewById(R.id.input_name);
        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_number = findViewById(R.id.input_number);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        progressBar = findViewById(R.id.progressbar);
        textSignIn = findViewById(R.id.textSignIn);
        if (Build.VERSION.SDK_INT>=21){
            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.white));
        }

        mAuth = FirebaseAuth.getInstance();


        buttonSignUp.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            buttonSignUp.setText("");

            String email,password,name,number;
            email = input_email.getText().toString();
            password = input_password.getText().toString();
            name = input_name.getText().toString();
            number = input_number.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(name) || TextUtils.isEmpty(number) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }
            user = new User(email,number,name,password, null);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            buttonSignUp.setText("SIGN UP");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            String uid = firebaseUser.getUid();

                            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
                            usersRef.setValue(user);

                            input_email.setText("");
                            input_number.setText("");
                            input_name.setText("");
                            input_password.setText("");

                            updateUI(firebaseUser);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            buttonSignUp.setText("SIGN UP");
                            Toast.makeText(SignUp.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    });

        });

        textSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUp.this,SignIn.class);
            startActivity(intent);
        });
    }
    private void updateUI(FirebaseUser firebaseUser){
        Intent intent = new Intent(this,SignIn.class);
        startActivity(intent);

    }
}