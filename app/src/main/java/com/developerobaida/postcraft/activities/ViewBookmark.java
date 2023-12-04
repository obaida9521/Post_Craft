package com.developerobaida.postcraft.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.adapters.AdapterBookmark;
import com.developerobaida.postcraft.model.BookmarkItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ViewBookmark extends AppCompatActivity {

    RecyclerView bookmarkRec;
    AdapterBookmark adapter;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    ArrayList<BookmarkItem> bookmarkItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookmark);
        if (Build.VERSION.SDK_INT>=21){
            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.white));
        }

        bookmarkRec = findViewById(R.id.bookmarkRec);



        String userId = currentUser != null ? currentUser.getUid() : "NULL";
        DatabaseReference bookmarksRef = FirebaseDatabase.getInstance().getReference("users/" + userId + "/bookmarks");
        bookmarksRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot bookmarkSnapshot : dataSnapshot.getChildren()) {

                    String bookmarkId = bookmarkSnapshot.getKey();
                    String title = bookmarkSnapshot.child("title").getValue(String.class);
                    String url = bookmarkSnapshot.child("url").getValue(String.class);
                    long timestamp = bookmarkSnapshot.child("timestamp").getValue(Long.class);
                    String type = bookmarkSnapshot.child("type").getValue(String.class);


                    bookmarkItems.add(new BookmarkItem(title, url, timestamp,"PROFILE",bookmarkId,userId));
                }
                adapter = new AdapterBookmark(ViewBookmark.this,bookmarkItems);
                bookmarkRec.setAdapter(adapter);
                bookmarkRec.setLayoutManager(new LinearLayoutManager(ViewBookmark.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle onCancelled event
            }
        });

    }
}