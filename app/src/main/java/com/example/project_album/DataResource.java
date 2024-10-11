package com.example.project_album;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DataResource {
    private SQLiteDatabase database,database1;
    private DatabaseHelper helper;
    private Context context;
    private String[] allColumns = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_IMAGE
            , DatabaseHelper.COLUMN_NAME, DatabaseHelper.COLUMN_SIZE, DatabaseHelper.COLUMN_DATE,
            DatabaseHelper.COLUMN_TYPE, DatabaseHelper.COLUMN_DESCRIBE,
            DatabaseHelper.COLUMN_IS_DELETE,DatabaseHelper.COLUMN_IS_FAVORITE,
            DatabaseHelper.COLUMN_IS_HIDE,DatabaseHelper.COLUMN_KEY};

    //    private String[] allColumns = {DatabaseHelper.COLUMN_ID,DatabaseHelper.COLUMN_IMAGE
//        ,DatabaseHelper.COLUMN_DATE};
    public DataResource(Context context) {
        this.context  = context;
        helper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
        database1=helper.getReadableDatabase();

    }

    public void close() {
        helper.close();
    }

    //--------------------------- ALBUM SPACE----------------------------

    public long InsertAlbum(String name,String key){
        SQLiteDatabase db = database;
        db.beginTransaction();
        long id = -1;
        try{
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_NAME_ALBUM,name);
            values.put(DatabaseHelper.COLUMN_KEY,key);
            id = database.insert(DatabaseHelper.TABLE_ALBUM,
                    null,values);
            db.setTransactionSuccessful();
            debug("insert sucessfull: "+name);

        }
        catch (Exception ex){
            debug("Error while insert Album");
        }
        finally {
            db.endTransaction();
        }
        return id;
    }
    public void UpdateKeyAlbum(long id,int keyA){
        ContentValues key = new ContentValues();
        key.put(DatabaseHelper.COLUMN_KEY,keyA);
        database.update(DatabaseHelper.TABLE_ALBUM,key,
                DatabaseHelper.COLUMN_ID +" = "+
                String.valueOf(id), null);

    }

    public long InsertAlbumImage(long idAlbum,long idImage){
        SQLiteDatabase db = database;
        db.beginTransaction();
        long id = -1;
        try{
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ID_IMAGE, idImage);
            values.put(DatabaseHelper.COLUMN_ID_ALBUM,idAlbum);
            id = database.insert(DatabaseHelper.TABLE_ALBUM_IMAGE,
                    null,values);
            db.setTransactionSuccessful();
            debug("insert sucessfull: "+String.valueOf(idImage));
        }
        catch (Exception ex){
            debug("Error while insert AlbumImage");
        }
        finally {
            db.endTransaction();
        }
        return id;
    }

    public ArrayList<Album> getAllAlbum(){
        ArrayList<Album> albums = new ArrayList<>();
        String columnAlbum[] = {DatabaseHelper.COLUMN_NAME_ALBUM,DatabaseHelper.COLUMN_ID_ALBUM
        ,DatabaseHelper.COLUMN_KEY};
        Cursor cursor = database.query(DatabaseHelper.TABLE_ALBUM,columnAlbum, null,
                null, null, null, null);
        debug(String.valueOf(cursor.getCount()));
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            albums.add(cursorToAlbum(cursor));
            cursor.moveToNext();
            debug("Album length: " +String.valueOf(albums.size()));
        }
        return albums;
    }

    private Album cursorToAlbum(Cursor cursor){
        return getAlbum(cursor.getLong(1),cursor.getString(0),cursor.getString(2));
    }

    public Album getAlbum(long id,String name,String key){
        debug(name);
        ArrayList<Image> images = new ArrayList<>();
        String columnImage_id[] = {DatabaseHelper.COLUMN_ID_IMAGE};
        Cursor cursor = database.rawQuery("select "+DatabaseHelper.COLUMN_ID_IMAGE+
                " from "+DatabaseHelper.TABLE_ALBUM_IMAGE+" where "+
                DatabaseHelper.COLUMN_ID_ALBUM +" = " + String.valueOf(id), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Image i= new Image();
            i.setId(cursor.getLong(0));
            images.add(i);
            debug(String.valueOf(i.getId()));
            cursor.moveToNext();

        }
        Album al = new Album(id,name,images);
        al.setKey(key);
        return al;
    }

    public boolean deleteImageInAlbum(Image image,long idAlbum){
        long id = image.getId();
        Log.e("SQLite","Person entry delete with id: "+id);
        try {
            database.delete(DatabaseHelper.TABLE_ALBUM_IMAGE,
                    DatabaseHelper.COLUMN_ID_IMAGE + " = " + String.valueOf(id) + " and "+
                            DatabaseHelper.COLUMN_ID_ALBUM +" = " +String.valueOf(idAlbum),
                    null);
            debug("Remove Successfull: "+String.valueOf(image.getId()));
            return true;
        }
        catch (Exception ex){
            debug("Exception while delete");
            return false;
        }
    }

    public boolean deleteImageInAlbum(Image image){
        long id = image.getId();
        Log.e("SQLite","Person entry delete with id: "+id);
        try {
            database.delete(DatabaseHelper.TABLE_ALBUM_IMAGE,
                    DatabaseHelper.COLUMN_ID_IMAGE + " = " + String.valueOf(id),
                    null);
            debug("Remove Successfull: "+String.valueOf(image.getId()));
            return true;
        }
        catch (Exception ex){
            debug("Exception while delete");
            return false;
        }
    }

    public boolean deleteAlbum(long idAlbum,String key){
        database.beginTransaction();
        try {
            database.delete(DatabaseHelper.TABLE_ALBUM,
                    DatabaseHelper.COLUMN_ID_ALBUM
                            + " = " + String.valueOf(idAlbum),
                    null);
            database.delete(DatabaseHelper.TABLE_ALBUM_IMAGE,
                    DatabaseHelper.COLUMN_ID_ALBUM
                            + " = " + String.valueOf(idAlbum),
                    null);
            MainActivity.dataFirebase.deleteAlbum(key);
            database.setTransactionSuccessful();
            return true;
        }
        catch (Exception ex){
            debug(ex.toString());
            return false;
        }
        finally {
            database.endTransaction();
        }

    }
    public void updateName(String oldName,String newName){
        ContentValues key = new ContentValues();
        key.put(DatabaseHelper.COLUMN_NAME_ALBUM,newName);
        database.update(DatabaseHelper.TABLE_ALBUM,key,
                DatabaseHelper.COLUMN_NAME_ALBUM +" = '"+oldName+"'",
                null);
    }
    //-------------------------------finish album space------------------------------------

    public long InsertUser(User user) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_USERNAME, user.getUsername());
            values.put(DatabaseHelper.COLUMN_PASSWORD, user.getPassword());
            values.put(DatabaseHelper.COLUMN_PHONE, user.getPhone());
            values.put(DatabaseHelper.COLUMN_EMAIL,user.getEmail());
            values.put(DatabaseHelper.COLUMN_NICKNAME,user.getUsername());

            long insertId = database.insert(DatabaseHelper.TABLE_USERS,
                    null, values);
            return insertId;
        } catch (Exception ex) {
            return -1;
        }
    }
    public long InsertImage(Image image) {
        try {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_IMAGE, helper.PATH+"/"+image.getName());
            values.put(DatabaseHelper.COLUMN_NAME, image.getName());
            values.put(DatabaseHelper.COLUMN_SIZE, image.getSize());
            values.put(DatabaseHelper.COLUMN_KEY,image.getKey());

            values.put(DatabaseHelper.COLUMN_DATE, image.getDate());
            values.put(DatabaseHelper.COLUMN_TYPE, image.getType());
            values.put(DatabaseHelper.COLUMN_DESCRIBE, image.getDescribe());
            values.put(DatabaseHelper.COLUMN_IS_DELETE, image.getDeleted());
            values.put(DatabaseHelper.COLUMN_IS_FAVORITE, image.getFavorite());
            values.put(DatabaseHelper.COLUMN_IS_HIDE, image.getHide());

            long insertId = database.insert(DatabaseHelper.TABLE_PICTURE, null, values);

            //luu image vao internal storage
            ContextWrapper cw = new ContextWrapper(context);
            File file = new File(helper.directory, image.getName());
            if (!file.exists()) {
                Log.d("path", file.toString());
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    image.getImgBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                    image.setPath(helper.PATH+"/"+image.getName());
                } catch (java.io.IOException e) {
                    debug(e.toString());
                    e.printStackTrace();
                }
            }

            return insertId;
        } catch (Exception ex) {
            debug(ex.toString());
            return -1;
        }
    }
    public long insertImage(DataFirebase.UploadImage image){
        ContentValues values = new ContentValues();
        File file = new File(helper.PATH+"/"+image.getName());
        values.put(DatabaseHelper.COLUMN_IMAGE, helper.PATH+"/"+image.getName());
        values.put(DatabaseHelper.COLUMN_NAME, image.getName());
        values.put(DatabaseHelper.COLUMN_SIZE, file.length()/1024);
        values.put(DatabaseHelper.COLUMN_KEY,image.getKey());

        values.put(DatabaseHelper.COLUMN_DATE, image.getDate());
        values.put(DatabaseHelper.COLUMN_TYPE, "");
        values.put(DatabaseHelper.COLUMN_DESCRIBE, image.getDescribe());
        values.put(DatabaseHelper.COLUMN_IS_DELETE, image.getDelete());
        values.put(DatabaseHelper.COLUMN_IS_FAVORITE, image.getFavorite());
        values.put(DatabaseHelper.COLUMN_IS_HIDE, image.getHide());

        long insertId = database.insert(DatabaseHelper.TABLE_PICTURE, null, values);
        return insertId;
    }
    public void UpdateKeyImage(long id,int keyI){
        ContentValues key = new ContentValues();
        key.put(DatabaseHelper.COLUMN_KEY,keyI);
        database.update(DatabaseHelper.TABLE_ALBUM,key,
                DatabaseHelper.COLUMN_ID +" = "+
                        String.valueOf(id), null);

    }
    public void unlikeImage(long id,String key){
        MainActivity.dataFirebase.updateImage(key,"favorite","F");
        String que= "UPDATE " +DatabaseHelper.TABLE_PICTURE +" SET "
                +DatabaseHelper.COLUMN_IS_FAVORITE + " = 'F'" +
                " WHERE " +DatabaseHelper.COLUMN_ID +" = "+String.valueOf(id);
        database.execSQL(que);
    }
    public void likeImage(long id,String key){
        MainActivity.dataFirebase.updateImage(key,"favorite","T");
        String que= "UPDATE " +DatabaseHelper.TABLE_PICTURE +" SET "
                +DatabaseHelper.COLUMN_IS_FAVORITE + " = 'T'" +
                " WHERE " +DatabaseHelper.COLUMN_ID +" = "+String.valueOf(id);
        database.execSQL(que);
    }
    // này là update trạng thái trong database khi chuyển từ all sang Hide bên album thôi
    public void updateStateImageHideIsTrue(long id,String key){
        MainActivity.dataFirebase.updateImage(key,"hide","T");
        String que= "UPDATE " +DatabaseHelper.TABLE_PICTURE +" SET "
                +DatabaseHelper.COLUMN_IS_HIDE+ " = 'T'" +
                " WHERE " +DatabaseHelper.COLUMN_ID +" = "+String.valueOf(id);
        database.execSQL(que);
    }
    public void updateStateImageHideIsFalse(long id,String key){
        MainActivity.dataFirebase.updateImage(key,"hide","F");
        String que= "UPDATE " +DatabaseHelper.TABLE_PICTURE +" SET "
                +DatabaseHelper.COLUMN_IS_HIDE+ " = 'F'" +
                " WHERE " +DatabaseHelper.COLUMN_ID +" = "+String.valueOf(id);
        database.execSQL(que);
    }
    ////////////////////
    // này là update trạng thái trong database khi chuyển từ all sang trash thôi
    public void updateStateImageDeletedIsTrue(long id,String key){
        MainActivity.dataFirebase.updateImage(key,"delete","T");
        String que= "UPDATE " +DatabaseHelper.TABLE_PICTURE +" SET "
                +DatabaseHelper.COLUMN_IS_DELETE+ " = 'T'" +
                " WHERE " +DatabaseHelper.COLUMN_ID +" = "+String.valueOf(id);
        database.execSQL(que);
    }
    public void updateStateImageDeletedIsFalse(long id,String key){
        MainActivity.dataFirebase.updateImage(key,"delete","F");
        String que= "UPDATE " +DatabaseHelper.TABLE_PICTURE +" SET "
                +DatabaseHelper.COLUMN_IS_DELETE+ " = 'F'" +
                " WHERE " +DatabaseHelper.COLUMN_ID +" = "+String.valueOf(id);
        database.execSQL(que);
    }
    ////////////////////
    //Này là delete ở database, xóa hẳn luôn.
    public boolean deleteImage(Image image){
        long id = image.getId();
        Log.e("SQLite","Person entry delete with id: "+id);
        try {
            File file = new File(image.getPath());
            file.delete();
            database.delete(DatabaseHelper.TABLE_PICTURE, DatabaseHelper.COLUMN_ID + " = " + id,
                    null);

            return true;
        }
        catch (Exception ex){
            return false;
        }

    }
    public ArrayList<Image> getAllImage() {
        ArrayList<Image> list = new ArrayList<Image>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_PICTURE, allColumns,null,
                null, null, null, null);
//        Cursor c = database.rawQuery("select ")
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursorToImage(cursor));
            cursor.moveToNext();
        }
        return list;
    }

    private Image cursorToImage(Cursor cursor) {
        ;
        Image image = new Image();
        image.setId(cursor.getLong(0));
        image.setName(cursor.getString(2));
        image.setSize(cursor.getFloat(3));
        image.setPath(cursor.getString(1));
        image.setType(cursor.getString(5));
        image.setDescribe(cursor.getString(6));
        image.setDeleted(cursor.getString(7));
        image.setFavorite(cursor.getString(8));
        image.setHide(cursor.getString(9));
        image.setDate(cursor.getString(4));
        image.setKey(cursor.getString(10));
        return image;
    }

    public int getCount() {
        Cursor cursor = database.rawQuery("select* from " + DatabaseHelper.TABLE_PICTURE, null);
        return cursor.getCount();
    }

    public int checkLogin(String username, String password) {
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS,
                new String[]{DatabaseHelper.COLUMN_USER},
                DatabaseHelper.COLUMN_USERNAME + "=? AND " + DatabaseHelper.COLUMN_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);
        int cursorCount = cursor.getCount();
        if (cursorCount > 0) {
            cursor.moveToFirst();
            int userIDColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_USER);
            if (userIDColumnIndex != -1) {
                int userID = cursor.getInt(userIDColumnIndex);
                cursor.close();
                return userID;
            }
        }
        cursor.close();
        return -1;
    }

    public boolean checkSignUp(String name) {
        String mySQL = "SELECT username FROM users WHERE username = ?";
        Cursor cursor = database.rawQuery(mySQL, new String[]{name});
        int cursorCount = cursor.getCount();
        cursor.close();
        if (cursorCount == 0) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getAccountInfo(int userID) {
        ArrayList<String> userInfo = new ArrayList<>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_USERS, new String[] { DatabaseHelper.COLUMN_NICKNAME, DatabaseHelper.COLUMN_PASSWORD }, DatabaseHelper.COLUMN_USER + "=?", new String[] { String.valueOf(userID) }, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            userInfo.add(cursor.getString(0));
            userInfo.add(cursor.getString(1));
            cursor.close();
        }
        return userInfo;
    }

    public void updateAccountInfo(int userID, String newNickname, String newPassword) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NICKNAME, newNickname);
        values.put(DatabaseHelper.COLUMN_PASSWORD, newPassword);
        database1.update(DatabaseHelper.TABLE_USERS, values, DatabaseHelper.COLUMN_USER + "=?", new String[] { String.valueOf(userID) });
    }
    public int countUser() {
        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_USERS;
        Cursor cursor = database.rawQuery(query, null);
        int count= cursor.getCount();
        cursor.close();
        return count;
    }
    public void clearTable(){
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_PICTURE);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_USERS);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_ALBUM);
        database.execSQL("DROP TABLE IF EXISTS " + DatabaseHelper.TABLE_ALBUM_IMAGE);
        for (File child : helper.directory.listFiles()) {
            child.delete();
        }
        helper.onCreate(database);
    }
    public File createNewFile(String filename){
        return new File(helper.directory,filename);
    }

    private void debug(String str) {
        Log.e("DataResource", str);
    }
}
