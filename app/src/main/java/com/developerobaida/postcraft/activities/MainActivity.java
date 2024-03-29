package com.developerobaida.postcraft.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.fragments.AllPost;
import com.developerobaida.postcraft.fragments.AllProfilePic;
import com.developerobaida.postcraft.fragments.AllWallpaper;
import com.developerobaida.postcraft.fragments.Home;
import com.developerobaida.postcraft.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainActivity extends AppCompatActivity {
    AnimatedBottomBar bottom_bar;
    ReviewManager reviewManager;
    ReviewInfo reviewInfo;
    AppUpdateManager appUpdateManager;
    NavigationView drawer;
    DrawerLayout drawerLay;
    MaterialToolbar toolbar;
    Button signIn_header;
    TextView userName, userMail;
    ImageView userImage;
    private final int REQ_CODE = 10;
    View headerView;
    DatabaseReference reference;
    FirebaseUser firebaseUser;
    private static final String USERS = "users";
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();
        window.setStatusBarColor(this.getColor(R.color.white));
        drawer = findViewById(R.id.drawer);
        drawerLay = findViewById(R.id.drawerLay);
        headerView = drawer.getHeaderView(0);
        bottom_bar = findViewById(R.id.bottom_bar);
        toolbar = findViewById(R.id.toolbar);
        reqReview();
        checkForUpdate();
        myPermissions();
        setSupportActionBar(toolbar);
        loadUserDetails();

        signIn_header = headerView.findViewById(R.id.signIn_header);
        userImage = headerView.findViewById(R.id.userImage);
        userName = headerView.findViewById(R.id.userName);
        userMail = headerView.findViewById(R.id.userMail);
        userMail.setSelected(true);
        userName.setSelected(true);
        userImage.setOnClickListener(v -> {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                Intent intent = new Intent(this, UserProfile.class);
                startActivity(intent);
            } else Toast.makeText(this, "Please Sign in", Toast.LENGTH_SHORT).show();

        });


        signIn_header.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignIn.class);
            startActivity(intent);
        });


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLay, toolbar, R.string.OPEN, R.string.CLOSE);
        drawerLay.addDrawerListener(toggle);
        drawer.setNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.favourite) {
                Intent intent = new Intent(MainActivity.this, ViewBookmark.class);
                startActivity(intent);
                drawerLay.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.rating) {
                reviewFlow();
                drawerLay.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.about) {
                Intent intent = new Intent(MainActivity.this, WebView.class);
                intent.putExtra("title", "About Us");
                intent.putExtra("link", "https://sites.google.com/view/postcraftaboutus/home");
                startActivity(intent);
                drawerLay.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                finish();
                drawerLay.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(MainActivity.this, UserProfile.class);
                startActivity(intent);
                drawerLay.closeDrawer(GravityCompat.START);
            } else if (item.getItemId() == R.id.privacy) {
                Intent intent = new Intent(MainActivity.this, WebView.class);
                intent.putExtra("title", "Privacy");
                intent.putExtra("link", "https://sites.google.com/view/post-craft/home");
                startActivity(intent);
                drawerLay.closeDrawer(GravityCompat.START);
            }

            return true;
        });

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.frame, new Home());
        transaction.commit();

        bottom_bar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                Fragment fragment;
                if (tab1.getId() == R.id.home) fragment = new Home();
                else if (tab1.getId() == R.id.post) fragment = new AllPost();
                else if (tab1.getId() == R.id.video) fragment = new AllWallpaper();
                else if (tab1.getId() == R.id.profilePic) fragment = new AllProfilePic();
                else fragment = new Home();

                switchFragment(fragment);
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {}
        });

    }//========================================on create=========================================

    //---------------------in app update----------------------------------------------------
    public void checkForUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, REQ_CODE);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        appUpdateManager.registerListener(listener);
    }

    InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADED) popupSnackbarForCompleteUpdate();
    };

    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("INSTALL", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(getResources().getColor(R.color.darkBlue));
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == REQ_CODE) if (REQ_CODE != RESULT_OK) Toast.makeText(this, "error : " + resultCode, Toast.LENGTH_SHORT).show();
    }
    //---------------------in app update----------------------------------------------------

    public void changeTab(int tabId) {
        bottom_bar.selectTabById(tabId, true);
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, fragment).addToBackStack(null).commit();

        int tabId;
        if (fragment instanceof Home) tabId = R.id.home;
        else if (fragment instanceof AllPost) tabId = R.id.post;
        else if (fragment instanceof AllWallpaper) tabId = R.id.video;
        else if (fragment instanceof AllProfilePic) tabId = R.id.profilePic;
        else tabId = R.id.home;

        bottom_bar.selectTabById(tabId, true);
    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), permission -> {
        boolean allGranted = true;
        for (Boolean isGranted : permission.values()) {
            if (!isGranted) {
                allGranted = false;
                break;
            }
        }
        if (allGranted) Log.d("permission", permission.toString());

    });

    private void myPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            String[] permissions = new String[]{android.Manifest.permission.READ_MEDIA_IMAGES, android.Manifest.permission.READ_MEDIA_AUDIO, android.Manifest.permission.READ_MEDIA_VIDEO};
            List<String> permissionsTORequest = new ArrayList<>();
            for (String permission : permissions)
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) permissionsTORequest.add(permission);

            if (permissionsTORequest.isEmpty()) {
            } else {
                String[] permissionsArray = permissionsTORequest.toArray(new String[0]);
                boolean shouldShowRationale = false;

                for (String permission : permissionsArray) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        shouldShowRationale = true;
                        break;
                    }
                }
                if (shouldShowRationale) {
                    new AlertDialog.Builder(this).setMessage("Please allow all permissions").setCancelable(false)
                            .setPositiveButton("YES", (dialogInterface, i) -> requestPermissionLauncher.launch(permissionsArray))
                            .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                } else requestPermissionLauncher.launch(permissionsArray);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};

            List<String> permissionsTORequest = new ArrayList<>();
            for (String permission : permissions)
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) permissionsTORequest.add(permission);
            if (permissionsTORequest.isEmpty()) {
            } else {
                String[] permissionsArray = permissionsTORequest.toArray(new String[0]);
                boolean shouldShowRationale = false;
                for (String permission : permissionsArray) {
                    if (shouldShowRequestPermissionRationale(permission)) {
                        shouldShowRationale = true;
                        break;
                    }
                }
                if (shouldShowRationale) {
                    new AlertDialog.Builder(this).setMessage("Please allow all permissions").setCancelable(false)
                            .setPositiveButton("YES", (dialogInterface, i) -> requestPermissionLauncher.launch(permissionsArray))
                            .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss()).show();
                } else requestPermissionLauncher.launch(permissionsArray);

            }
        }
    } // myPermissions end here ================

    private void reqReview() {
        reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) reviewInfo = task.getResult();
            else Log.d("LOG", "Task not Successful");
        });
    }

    private void reviewFlow(){
        if (reviewInfo != null) {
            Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
            flow.addOnCompleteListener(task -> Toast.makeText(this, "Review Successful", Toast.LENGTH_SHORT).show());
        }
    }

    private void loadUserDetails() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            Menu menu = drawer.getMenu();
            MenuItem logoutItem = menu.findItem(R.id.logout);
            MenuItem profileItem = menu.findItem(R.id.profile);
            logoutItem.setVisible(true);
            profileItem.setVisible(true);

            userId = firebaseUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference(USERS);
            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        userName.setText(user.getDisplayName());
                        userMail.setText(user.getEmail());
                        userMail.setVisibility(View.VISIBLE);
                        signIn_header.setVisibility(View.GONE);

                        if (user.getImageUrl() != null) Picasso.get().load(user.getImageUrl()).into(userImage);
                        else userImage.setImageResource(R.drawable.man);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } else {
            try {
                userName.setText("User");
                userMail.setText("Mail");
                userImage.setImageResource(R.drawable.no_img);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Menu menu = drawer.getMenu();
            MenuItem logoutItem = menu.findItem(R.id.logout);
            MenuItem profileItem = menu.findItem(R.id.profile);
            logoutItem.setVisible(false);
            profileItem.setVisible(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        appUpdateManager.unregisterListener(listener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bottom_bar.selectTabById(R.id.home, true);
    }
}