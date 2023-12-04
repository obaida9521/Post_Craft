package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {
    EditText input_email,input_password;
    TextView textCreateNewAccount,textPrivacy;
    ProgressBar progressbar;
    Button buttonSignIn;
    private FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(SignIn.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (Build.VERSION.SDK_INT>=21){
            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.white));
        }

        input_password = findViewById(R.id.input_password);
        input_email = findViewById(R.id.input_email);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        textCreateNewAccount = findViewById(R.id.textCreateNewAccount);
        progressbar = findViewById(R.id.progressbar);
        textPrivacy =findViewById(R.id.textPrivacy);

        mAuth = FirebaseAuth.getInstance();

        buttonSignIn.setOnClickListener(v -> {
            progressbar.setVisibility(View.VISIBLE);
            buttonSignIn.setText("");
            String email,password;
            email = input_email.getText().toString();
            password = input_password.getText().toString();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressbar.setVisibility(View.GONE);
                            buttonSignIn.setText("SIGN IN");
                            Toast.makeText(SignIn.this,"sign in success",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignIn.this,MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressbar.setVisibility(View.GONE);
                            buttonSignIn.setText("SIGN IN");
                            Toast.makeText(SignIn.this,"Failed to sign in\ninvalid mail or password",Toast.LENGTH_SHORT).show();
                        }
                    });

        });
        textCreateNewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(SignIn.this,SignUp.class);
            startActivity(intent);
        });

    }
}