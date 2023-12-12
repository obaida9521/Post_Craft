package com.developerobaida.postcraft.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UserProfile extends AppCompatActivity {
    ImageView profile_pic,pick,back;
    TextView userName,userMail,title;
    LinearLayout logout;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    StorageReference storageRef;
    ProgressBar pr;
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
        pr = findViewById(R.id.pr);
        pr.getIndeterminateDrawable().setTint(Color.parseColor("#F44336"));
        Window window = getWindow();window.setStatusBarColor(this.getColor(R.color.white));
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

            reference = FirebaseDatabase.getInstance().getReference(USERS);
            String userId = firebaseUser.getUid();
            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                String name,gmail,mobile;
                String image ="";
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user!=null){
                        name = user.getDisplayName();
                        gmail = user.getEmail();
                        mobile = user.getPhoneNumber();
                        image = user.getImageUrl();

                        userName.setText(name);
                        userMail.setText(gmail);
                        title.setText(name+"'s profile");

                        if (user.getImageUrl()!=null) Picasso.get().load(user.getImageUrl()).into(profile_pic);
                        else profile_pic.setImageResource(R.drawable.man);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        }else {
            userName.setText("User");
            userMail.setText("Mail");
            profile_pic.setImageResource(R.drawable.no_img);

        }

    }
    ActivityResultLauncher<PickVisualMediaRequest> launcher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri o) {
            if (o!=null) {
                pr.setVisibility(View.VISIBLE);
                Picasso.get().load(o).into(profile_pic);
                uploadImageToFirebase(o);

            }else Toast.makeText(UserProfile.this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    });
    private void uploadImageToFirebase(Uri imageUri) {
        pr.setVisibility(View.VISIBLE);

        String uid = firebaseUser.getUid();
        storageRef = FirebaseStorage.getInstance().getReference().child("profile_images").child(uid + ".webp");

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 70, baos);
            byte[] data = baos.toByteArray();

            storageRef.putBytes(data)
                    .addOnSuccessListener(taskSnapshot -> {
                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();

                            reference = FirebaseDatabase.getInstance().getReference("users").child(uid);
                            reference.child("imageUrl").setValue(imageUrl);
                            Toast.makeText(UserProfile.this, "Image uploaded.", Toast.LENGTH_SHORT).show();

                            loadUserDetails();
                            pr.setVisibility(View.GONE);
                        });
                    })
                    .addOnFailureListener(e -> {
                        pr.setVisibility(View.GONE);
                        Toast.makeText(UserProfile.this, "Failed to upload.", Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            pr.setVisibility(View.GONE);
            Toast.makeText(UserProfile.this, "Error loading the image.", Toast.LENGTH_SHORT).show();
        }
    }

}