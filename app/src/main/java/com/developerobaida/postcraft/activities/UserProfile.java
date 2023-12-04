package com.developerobaida.postcraft.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UserProfile extends AppCompatActivity {

    ImageView profile_pic,pick,back;
    TextView userName,userMail,title;
    LinearLayout logout;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    private static final String USERS = "users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profile_pic = findViewById(R.id.profile_pic);
        pick = findViewById(R.id.pick);
        userName = findViewById(R.id.userName);
        userMail = findViewById(R.id.userMail);
        title = findViewById(R.id.title);
        logout = findViewById(R.id.logout);
        back = findViewById(R.id.back);
        if (Build.VERSION.SDK_INT>=21){Window window = getWindow();window.setStatusBarColor(this.getColor(R.color.white));}
        loadUserDetails();

        pick.setOnClickListener(v -> launcher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this,SignIn.class);
            startActivity(intent);
            finish();
        });
        back.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

    }
    private void loadUserDetails(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser!=null){

            String userId = firebaseUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference(USERS);
            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                String name,gmail,mobile,image;
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user!=null){
                        name = user.getDisplayName();
                        gmail = user.getEmail();
                        mobile = user.getPhoneNumber();
                        image = user.getPic();

                        userName.setText(name);
                        userMail.setText(gmail);
                        title.setText(name+"'s profile");

                        if (user.getPic()!=null) Picasso.get().load(image).into(profile_pic);
                        else profile_pic.setImageResource(R.drawable.man);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }else {
            userName.setText("User");
            userMail.setText("Mail");
            profile_pic.setImageResource(R.drawable.profile);

        }
    }
    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri o) {
            if (o!=null) {
                Picasso.get().load(o).into(profile_pic);

            }else Toast.makeText(UserProfile.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    });
}