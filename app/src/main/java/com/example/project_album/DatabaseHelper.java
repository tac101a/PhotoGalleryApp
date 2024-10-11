package com.example.project_album;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

public class DatabaseHelper extends SQLiteOpenHelper {
    // ten database va version cua no
    private static final String DATABASE_NAME = "manage_account.db";
    private static final int DATABASE_VERSION = 1;
    // ten cac bang tong database
    public static final String TABLE_PICTURE = "picture";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_ALBUM = "album";
    public static final String TABLE_ALBUM_IMAGE = "album_image";
    // cac truong trong bang
    public static final String COLUMN_USER = "user";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_ID_ALBUM = "id_album";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SIZE = "size";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIBE = "describe";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_NICKNAME = "nickname";
    public static final String COLUMN_NAME_ALBUM = "name_album";
    public static final String COLUMN_ID_IMAGE = "id_image";
    public static final String COLUMN_KEY = "key";

    public static final String COLUMN_IS_DELETE = "is_delete";// chỗ này nếu true thì sẽ k hiển thị ở all layout
    //mà chỉ hiển thị ở trash can, biến này kiểu trong SQL là text

    public static final String COLUMN_IS_FAVORITE = "is_favorite";
    public static final String COLUMN_IS_HIDE = "is_hide";

    // cac cau lenh tao bang
    private static final String DATABASE_CREATE_USERS = "create table " + TABLE_USERS + "( "
            + COLUMN_USER
            + " integer primary key autoincrement, "
            + COLUMN_USERNAME
            + " text, "
            + COLUMN_PHONE
            + " text, "
            + COLUMN_EMAIL
            + " text not null, "
            + COLUMN_PASSWORD
            + " text not null, "
            + COLUMN_NICKNAME
            + " text not null);";

    private static final String DATABASE_CREATE = "create table " + TABLE_PICTURE + "( " + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_USER
            + " integer, "
            + COLUMN_IMAGE
            + " text not null, "
            + COLUMN_NAME
            + " text not null, "
            + COLUMN_KEY
            + " text not null, "
            + COLUMN_SIZE
            + " float, "
            + COLUMN_DATE
            + " text not null, "
            + COLUMN_TYPE
            + " text not null, "
            + COLUMN_IS_DELETE
            + " text not null, "
            + COLUMN_IS_FAVORITE
            + " text not null, "
            + COLUMN_IS_HIDE
            + " text not null, "
            + COLUMN_DESCRIBE
            + " text);";

    private static final String DATABASE_CREATE_ALBUM = "create table " + TABLE_ALBUM + "( "
            + COLUMN_ID_ALBUM
            + " integer primary key autoincrement, "
            + COLUMN_KEY
            + " text not null, "
            + COLUMN_NAME_ALBUM
            + " text not null);";
    private static final String DATABASE_CREATE_ALBUM_IMAGE = "create table " +
            TABLE_ALBUM_IMAGE + "( "
            + COLUMN_ID
            + " integer primary key autoincrement, "
            + COLUMN_ID_IMAGE
            + " integer, "
            + COLUMN_ID_ALBUM
            + " integer);";
    public File directory;
    public static String PATH;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ContextWrapper cw = new ContextWrapper(context);
        directory = cw.getDir("Picture", Context.MODE_PRIVATE);
        PATH = directory.getPath();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_USERS);
        sqLiteDatabase.execSQL(DATABASE_CREATE_ALBUM);
        sqLiteDatabase.execSQL(DATABASE_CREATE_ALBUM_IMAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.v(DatabaseHelper.class.getName(),
                "Upgrading database from version " + i + " to " + i1
                        + ", which will destroy all data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ALBUM_IMAGE);

        onCreate(sqLiteDatabase);
    }
}