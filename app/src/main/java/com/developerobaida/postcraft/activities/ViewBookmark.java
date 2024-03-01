package com.developerobaida.postcraft.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.developerobaida.postcraft.R;
import com.developerobaida.postcraft.adapters.AdapterBookmark;
import com.developerobaida.postcraft.database.DatabaseHelper;
import com.developerobaida.postcraft.model.BookmarkItem;

import java.util.ArrayList;


public class ViewBookmark extends AppCompatActivity implements AdapterBookmark.OnFavClickListener{

    RecyclerView bookmarkRec;
    AdapterBookmark adapter;
    ArrayList<BookmarkItem> bookmarkItems = new ArrayList<>();
    DatabaseHelper database;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bookmark);
        if (Build.VERSION.SDK_INT>=21){
            Window window = getWindow();
            window.setStatusBarColor(this.getColor(R.color.white));
        }
        bookmarkRec = findViewById(R.id.bookmarkRec);
        back = findViewById(R.id.back);
        database = new DatabaseHelper(this);
        getFav();

        back.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });
    }

    public void getFav(){
        Cursor cursor = database.getProfile_fav();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String img = cursor.getString(1);
            String title = cursor.getString(2);
            String type = cursor.getString(3);
            int serverid = cursor.getInt(4);

            bookmarkItems.add(new BookmarkItem(title,img,type,serverid,id));
        }
        adapter = new AdapterBookmark(this,bookmarkItems,this);
        bookmarkRec.setAdapter(adapter);
        bookmarkRec.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        getWallFav();
    }
    public void getWallFav(){
        Cursor cursor = database.getWall_fav();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String img = cursor.getString(1);
            String title = cursor.getString(2);
            String type = cursor.getString(3);
            int serverid = cursor.getInt(4);

            bookmarkItems.add(new BookmarkItem(title,img,type,serverid,id));
        }
        adapter = new AdapterBookmark(this,bookmarkItems,this);
        bookmarkRec.setAdapter(adapter);
        bookmarkRec.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        getPostFav();
    }
    public void getPostFav(){
        Cursor cursor = database.getPost_fav();
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String status = cursor.getString(1);
            String type = cursor.getString(2);
            String image = cursor.getString(3);
            String font = cursor.getString(4);
            String color = cursor.getString(5);
            String size = cursor.getString(6);
            String dy = cursor.getString(7);
            String dx = cursor.getString(8);
            String radius = cursor.getString(9);
            String shadowColor = cursor.getString(10);
            int serverId = cursor.getInt(11);

            bookmarkItems.add(new BookmarkItem(""+status,""+image,""+font,""+color,""+size,""+dx,""+dy,""+radius,""+shadowColor,""+type,serverId,id));
        }
        adapter = new AdapterBookmark(this,bookmarkItems,this);
        bookmarkRec.setAdapter(adapter);
        bookmarkRec.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
    }

    @Override
    public void onFavClick(int position,int type) {
        if (type ==0){
            if (position >= 0 && position < bookmarkItems.size()) {
                BookmarkItem clickedItem = bookmarkItems.get(position);
                database.deletePost_fav(clickedItem.getServerId());
                bookmarkItems = new ArrayList<>();
                getFav();
            }
        } else if (type==1) {
            if (position >= 0 && position < bookmarkItems.size()) {
                BookmarkItem clickedItem = bookmarkItems.get(position);
                database.deleteProfile_fav(clickedItem.getServerId());
                bookmarkItems = new ArrayList<>();
                getFav();
            }
        } else if (type==2) {
            if (position >= 0 && position < bookmarkItems.size()) {
                BookmarkItem clickedItem = bookmarkItems.get(position);
                database.deleteWall_fav(clickedItem.getServerId());
                bookmarkItems = new ArrayList<>();
                getFav();
            }
        }
    }

}