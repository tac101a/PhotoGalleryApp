package com.example.project_album;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.transition.ChangeImageTransform;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class DataFirebase {
    MainActivity main;
    private String userkey;
    private static ArrayList<UploadUser> users = new ArrayList<UploadUser>();
    private StorageReference mStoreageRef;
    private FirebaseFirestore mFirebaseFir;
    CollectionReference cUser;
    CollectionReference cImage;
    CollectionReference cAlbum;
    private boolean suc = false;
    public DataFirebase(){
    }
    public DataFirebase(String userkey,MainActivity main){
        this.main = main;
        this.userkey = userkey;
        mStoreageRef = FirebaseStorage.getInstance().getReference("Picture/"+userkey);
        mFirebaseFir = FirebaseFirestore.getInstance();
        cUser =mFirebaseFir.collection("user");
        cImage = cUser.document(userkey).collection("Image");
        cAlbum = cUser.document(userkey).collection("Album");
    }
    public static User checkUser(String username,String password){
        for(UploadUser user: users){
            if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                Log.e("DataFirebase",user.getKey());
                User user1 = new User(user.getUsername(),user.getPassword(),
                        user.getEmail(),user.getPhone());
                user1.setKey(user.getKey());
                user1.setHidepass(user.getHidepass());
                return user1;
            }
        }
        return null;
    }
    public static void open(){
        users.clear();
        FirebaseFirestore ff = FirebaseFirestore.getInstance();
        ff.collection("user").get().addOnCompleteListener(
        new OnCompleteListener<QuerySnapshot>() {
        @Override
        public void onComplete(@NonNull Task<QuerySnapshot> task)
            {for(QueryDocumentSnapshot document: task.getResult()){
                Map<String, Object> map = new HashMap<>();
                map = document.getData();
                UploadUser user = new UploadUser(map);
                user.setKey(document.getId());
                users.add(user);
            }}
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DataFirebase","No internet");
            }
        });
    }

    //insert===================================================================
    public void insertImage(Image image){
        debug("1");
        Uri uri = getUri(image.getImgBitmap());
        debug("3");
        StorageReference fileReference = mStoreageRef.child(
                image.getName());
        fileReference.putFile(uri).addOnSuccessListener(
            new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UploadImage upload = new UploadImage(uri.toString(),fileReference.getPath());
                        upload.setFavorite(image.getFavorite());
                        String nextkey = findNextkey();
                        upload.setKey(nextkey);
                        image.setKey(nextkey);
                        debug(nextkey);
                        cImage.document(nextkey).set(upload);
                        image.setId(main.dataResource.InsertImage(image));

                        //AllLayout.images.add(image);
                        MainActivity.images.add(image);
                        main.allLayout.adapter.insert(image);
                        main.allLayout.update();
                        if(main.isURLdownload){
                            main.allLayout.URLdownloadSuc();
                        }
                    }
                });
            }
            }).addOnCanceledListener(new OnCanceledListener() {
        @Override
        public void onCanceled() {

        }
        });
    }
    public void setHidePassIntoFirebase(String pass){
        Map<String,Object> map = new HashMap<>();
        map.put("hidepass",pass);
        cUser.document(userkey).update(map);
    }

    public void insertAlbum(String name){
        UploadAlbum upload = new UploadAlbum(name,"");
        cAlbum.get().addOnCompleteListener(
        new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                String newkey = "0";
                for(QueryDocumentSnapshot document: task.getResult()){
                    newkey = document.getId();
                }
                newkey = String.valueOf(Integer.parseInt(newkey)+1);
                upload.setKey(newkey);
                cAlbum.document(newkey).set(upload);
                if(main.isAddAlbum){
                    main.albumLayout.addToDatabase(newkey,name);
                    main.isAddAlbum = false;
                }
            }
        });
    }


    //update=========================================================
    public void updateImage(String keyImage,String key,String value){
        Map<String,Object> map = new HashMap<>();
        map.put(key,value);
        cImage.document(keyImage).update(map).addOnCompleteListener(
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            }
        );
    }
    public void updateAlbum(Album a){
        String data = "";
        for(int i = 0;i<a.getImages().size();i++){
            data+= a.get_image(i).getKey();
            if(i!=a.getImages().size() - 1){
                data+="-";
            }
        }
        debug(data);
        Map<String ,Object>map = new HashMap<>();
        map.put("key_images",data);
        map.put("name",a.getName());
        cAlbum.document(a.getKey()).update(map);
    }
    //delete=========================================================
    public void deleteImage(String key,String name){
        mStoreageRef.child(name).delete().addOnCompleteListener(
        new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                cImage.document(key).delete().addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
            }
        });
    }
    public void deleteAlbum(String key){
        cAlbum.document(key).delete();
    }


    //ham phu==============================================================
    private Uri getUri(Bitmap image){
        Uri uri = null;
        try{
            debug("4");
            FileOutputStream fos = null;
            File file = File.createTempFile("temprentpk", ".jpeg");
            fos = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            uri = Uri.fromFile(file);
        }
        catch (Exception e){
            debug(e.toString());
        }
        return uri;
    }

    private void debug(String t){
        Log.e("Datafirebase",t);
    }
    public String findNextkey(){
        int max = 0;
        for(Image image:MainActivity.images){
            if (max < Integer.parseInt(image.getKey())){
                max = Integer.parseInt(image.getKey());
            }
        }
        return String.valueOf(max+1);
    }

    //==================================Upload User=============================
    public static class UploadUser{
        private String username;
        private String password;
        private  String email;
        private String phone;
        private String key;
        private String hidepass;

        public UploadUser(){

        }
        public UploadUser(String username, String pass, String email, String phone)
        {
            this.username=username;
            this.email=email;
            this.password=pass;
            this.phone=phone;
            this.hidepass="";
        }

        public String getHidepass() {
            return hidepass;
        }

        public void setHidepass(String hidepass) {
            this.hidepass = hidepass;
        }

        public UploadUser(Map<String,Object> map){
            username = map.get("username").toString();
            password = map.get("password").toString();
            email = map.get("email").toString();
            phone = map.get("phone").toString();
            hidepass=map.get("hidepass").toString();
        }

        public void setUsername(String username) {
            this.username = username;
        }
        public void setPass(String pass){
            this.password = pass;
        }
        public void setEmail(String email){
            this.email = email;
        }
        public void setPhone(String phone){
            this.phone = phone;
        }
        public String getUsername(){return username;}
        public String getPassword(){return  password;}
        public String getEmail(){return email;}
        public String getPhone(){return phone;}
        public void setKey(String key){
            this.key =key;
        }
        public String getKey(){
            return key;
        }
    }

    //==================================Upload Album============================
    public static class UploadAlbum {
        private String name;
        private String key_images;
        private String key;
        public UploadAlbum(){

        }
        public UploadAlbum(String name,String key_images){
            this.name = name;
            this.key_images = key_images;
        }
        public UploadAlbum(Map<String,Object> map){
            this.name = map.get("name").toString();
            this.key_images = map.get("key_images").toString();
            this.key = map.get("key").toString();
        }

        public String getKey_images() {
            return key_images;
        }

        public void setKey_images(String key_images) {
            this.key_images = key_images;
        }

        public String getName(){
            return name;
        }
        public void setName(String name){
            this.name = name;
        }
        public void setKey(String key){
            this.key = key;
        }
        public String getKey(){
            return key;
        }
    }

    //==================================Upload Image============================

    public static class UploadImage{
        private String name;
        private String imageUrl;
        private String path;
        private String date;
        private String describe;
        private String delete;
        private String favorite;

        private String hide;
        private String key;
        public UploadImage(){
        }

        public UploadImage(String imageUrl,String path){
            this.path = path;
            this.imageUrl = imageUrl;
            this.name = path.split("/")[path.split("/").length-1];
            date = ConvertDate(new Date());
            this.delete = "F";
            this.favorite = "F";
            this.hide = "F";
            this.describe = "";
        }
        public UploadImage(Map<String,Object> map){
            this.path = map.get("path").toString();
            this.imageUrl = map.get("imageUrl").toString();
            this.name = map.get("name").toString();
            date = map.get("date").toString();
            this.delete = map.get("delete").toString();
            this.favorite = map.get("favorite").toString();
            this.hide = map.get("hide").toString();
            this.describe = map.get("describe").toString();
            this.key = map.get("key").toString();
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getHide(){
            return hide;
        }
        public void setHide(String hide){
            this.hide = hide;
        }

        public String getName(){
            return name;
        }
        public String getDate(){
            return date;
        }
        public void setDate(String date){
            this.date = date;
        }
        public String getDescribe(){
            return describe;
        }
        public String getDelete() {
            return delete;
        }
        public void setName(String name){
            this.name = name;
        }
        public void setDescribe(String describe){
            this.describe = describe;
        }

        public void setDelete(String delete) {
            this.delete = delete;
        }

        public void setFavorite(String favorite) {
            this.favorite = favorite;
        }

        public String getFavorite() {
            return favorite;
        }
        public void setKey(String key){
            this.key = key;
        }
        public String getKey(){
            return key;
        }
        private String ConvertDate(Date d){
            SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy");
            return ft.format(d);
        }
    }
}
