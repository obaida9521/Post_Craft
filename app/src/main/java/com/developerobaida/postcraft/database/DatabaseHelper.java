package com.developerobaida.postcraft.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "post_craft";
    public static final String table_profile = "profile_fav";
    public static final String table_post = "post_fav";
    public static final String table_wall = "wall_fav";
    public static final int DB_VERSION = 1;
    public DatabaseHelper( Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+table_profile+" (id INTEGER PRIMARY KEY AUTOINCREMENT ,image VARCHAR,title VARCHAR,type VARCHAR,serverid INTEGER)");
        db.execSQL("CREATE TABLE "+table_wall+" (id INTEGER PRIMARY KEY AUTOINCREMENT ,image VARCHAR,title VARCHAR,type VARCHAR,serverid INTEGER)");
        db.execSQL("CREATE TABLE "+table_post+
                " (id INTEGER PRIMARY KEY AUTOINCREMENT ,status VARCHAR,type VARCHAR,image VARCHAR,font VARCHAR,color VARCHAR,size VARCHAR,dy VARCHAR,dx VARCHAR,radius VARCHAR,shadowColor VARCHAR,serverid INTEGER)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE if exists "+table_profile);
        db.execSQL("DROP TABLE if exists "+table_post);
        db.execSQL("DROP TABLE if exists "+table_wall);
    }

    //-----------------------------------------------------**  profile **----------------------==
    public void addProfile_fav(String image,String title,String type,String serverid){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("image",image);
        values.put("title",title);
        values.put("type",type);
        values.put("serverid",Integer.parseInt(serverid));

        db.insert(table_profile,null,values);
    }
    public Cursor getProfile_fav(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+table_profile,null);

        return cursor;
    }
    public void deleteProfile_fav(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+table_profile+" WHERE serverid LIKE "+id);
    }
    //-----------------------------------------------------**  profile **----------------------==

    //-----------------------------------------------------**  post **----------------------==
    public void addPost_fav(String status,String type,String image,String font,String color,String size,String dy,String dx,String radius,String shadowColor,String serverid){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("status",status);
        values.put("image",image);
        values.put("type",type);
        values.put("font",font);
        values.put("color",color);
        values.put("size",size);
        values.put("dy",dy);
        values.put("dx",dx);
        values.put("radius",radius);
        values.put("shadowColor",shadowColor);
        values.put("serverid",Integer.parseInt(serverid));

        db.insert(table_post,null,values);
    }
    public Cursor getPost_fav(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+table_post,null);

        return cursor;
    }
    public void deletePost_fav(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+table_post+" WHERE serverid LIKE "+id);
    }
    //-----------------------------------------------------**  post **----------------------==

    //-----------------------------------------------------**  Wallpaper **----------------------==
    public void addWall_fav(String image,String title,String type,String serverid){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("image",image);
        values.put("title",title);
        values.put("type",type);
        values.put("serverid",Integer.parseInt(serverid));

        db.insert(table_wall,null,values);
    }
    public Cursor getWall_fav(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+table_wall,null);

        return cursor;
    }
    public void deleteWall_fav(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+table_wall+" WHERE serverid LIKE "+id);
    }
    //-----------------------------------------------------**  Wallpaper **----------------------==
}
