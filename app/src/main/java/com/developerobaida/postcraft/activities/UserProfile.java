package com.developerobaida.postcraft.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

public class UserProfile extends AppCompatActivity {
    ReviewManager reviewManager;
    ReviewInfo reviewInfo;
    ImageView profile_pic,pick,back;
    TextView userName,userMail,title,contact,rateUs,deleteAccount;
    LinearLayout logout;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    StorageReference storageRef;
    ProgressBar pr,progressbar;
    private static final String USERS = "users";
    String user_pass;
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

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
        progressbar = findViewById(R.id.progressbar);
        contact = findViewById(R.id.contact);
        rateUs = findViewById(R.id.rateUs);
        deleteAccount = findViewById(R.id.deleteAccount);
        pr.getIndeterminateDrawable().setTint(Color.parseColor("#F44336"));
        progressbar.getIndeterminateDrawable().setTint(Color.parseColor("#31106A"));
        Window window = getWindow();window.setStatusBarColor(this.getColor(R.color.white));
        loadUserDetails();
        reqReview();


        pick.setOnClickListener(v -> launcher.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));

        deleteAccount.setOnClickListener(v -> showDialog());

        rateUs.setOnClickListener(v -> reviewFlow());
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
            bitmap.compress(Bitmap.CompressFormat.WEBP, 65, baos);
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


    private void reauthenticateUser(String pass) {
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), pass);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) deleteAccountAndImages();
                        else Toast.makeText(UserProfile.this, "invalid password", Toast.LENGTH_SHORT).show();
                    });
        } else Log.e("REAUTHENTICATION_ERROR", "User is null");
    }

    private void deleteAccountAndImages() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null && userProfile.getImageUrl() != null) {
                        deleteImageFromStorage(userProfile.getImageUrl());

                        databaseReference.removeValue().addOnSuccessListener(aVoid -> {
                            Log.e("DATABASE", "userData deleted from database");
                        }).addOnFailureListener(e -> {
                            Log.e("DATABASE", "Failed to delete user data", e);
                            Toast.makeText(UserProfile.this, "Failed to delete user data", Toast.LENGTH_SHORT).show();
                        });

                    }else {
                        deleteUserAccount();
                        databaseReference.removeValue().addOnSuccessListener(aVoid -> {
                            Log.e("DATABASE", "userData deleted from database");
                        }).addOnFailureListener(e -> {
                            Log.e("DATABASE", "Failed to delete user data", e);
                            Toast.makeText(UserProfile.this, "Failed to delete user data", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void deleteImageFromStorage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {

            Log.d("STORAGE", "Deleting image from URL: " + imageUrl);
            StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            storageRef.delete().addOnSuccessListener(aVoid -> deleteUserAccount())
                    .addOnFailureListener(e -> {Log.e("STORAGE", "Failed to delete image", e);});
        }
    }

    private void deleteUserAccount() {
        user.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressbar.setVisibility(View.GONE);
                Toast.makeText(UserProfile.this, "Account Deleted successfully", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(UserProfile.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                progressbar.setVisibility(View.GONE);
                Toast.makeText(UserProfile.this, "Failed to delete account\nContact us", Toast.LENGTH_SHORT).show();
                Log.e("USER_DELETE_ERROR", "Failed to delete user account", task.getException());
            }
        });
    }
    private void reqReview(){
        reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) reviewInfo = task.getResult();
            else Log.d("TAG","Task not Successful");
        });
    }
    private void reviewFlow(){
        if (reviewInfo!=null){
            Task<Void> flow = reviewManager.launchReviewFlow(this,reviewInfo);
            flow.addOnCompleteListener(task -> Toast.makeText(this,"Review Successful",Toast.LENGTH_SHORT).show());
        }
    }
    void showDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialogue_ui, null);

        TextView title = dialogView.findViewById(R.id.title);
        TextView message = dialogView.findViewById(R.id.message);
        CardView positive = dialogView.findViewById(R.id.positive);
        CardView negative = dialogView.findViewById(R.id.negative);
        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView positiveTxt = dialogView.findViewById(R.id.positiveTxt);
        TextView negativeTxt = dialogView.findViewById(R.id.negativeTxt);
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme).setView(dialogView).create();

        icon.setImageResource(R.drawable.baseline_delete_24);
        title.setText("Delete account");
        positiveTxt.setText("Yes");
        negativeTxt.setText("No");
        message.setText("Do you want to delete your data and all information from this app?");

        positive.setOnClickListener(v -> {
            showInputDialog();
            alertDialog.dismiss();
        });
        negative.setOnClickListener(v -> alertDialog.dismiss());

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

    }
    void showInputDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_input, null);

        TextView title = dialogView.findViewById(R.id.title);
        TextInputEditText pass = dialogView.findViewById(R.id.pass);
        CardView positive = dialogView.findViewById(R.id.positive);
        CardView negative = dialogView.findViewById(R.id.negative);
        ImageView icon = dialogView.findViewById(R.id.icon);
        TextView positiveTxt = dialogView.findViewById(R.id.positiveTxt);
        TextView negativeTxt = dialogView.findViewById(R.id.negativeTxt);

        title.setText("Inter your password");
        positiveTxt.setText("Ok");
        negativeTxt.setText("Cancel");
        icon.setImageResource(R.drawable.baseline_delete_24);
        AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogTheme).setView(dialogView).create();

        positive.setOnClickListener(v -> {
            user_pass = pass.getText().toString();
            if (user_pass!=null && !user_pass.isEmpty()){
                progressbar.setVisibility(View.VISIBLE);
                reauthenticateUser(user_pass);
                alertDialog.dismiss();
            }else Toast.makeText(this, "Give a password", Toast.LENGTH_SHORT).show();

        });

        negative.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

}