package com.example.project_album;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class LoginFragment extends Fragment {
    private final String keyUsername = "username";
    private final String keyPassword = "password";
    private final String keyEmail = "email";
    private final String keyPhone = "phone";
    private final String keyUser = "userkey";
    private final String keyUserID = "userid";
    public String username = "";
    public String password = "";
    private String phone;
    private String email;
    private String userkey;
    private String hidepass;


    LoginActivity main;
    Context context = null;
    FragmentTransaction ft;
    TextView inputUsername;
    TextView inputPassword;
    Button btnLogin;
    TextView toSignUp;
    int userID;
    boolean isadmin = false;
    private SharedPreferences myPrefContainer;
    private String PREFNAME = "user";
    ProgressDialog dialog;
    int count= 0;

    public static LoginFragment newInstance(String strArg) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (LoginActivity) getActivity();
//            int count = MainActivity.dataResource.countUser();
//            if(count == 0){
//                User user= new User("admin","admin",
//                        "THASH@gmail.com","035555557");
//                long i=MainActivity.dataResource.InsertUser(user);
//            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        inputUsername = view.findViewById(R.id.inputUsername);
        inputPassword = view.findViewById(R.id.inputPassword);
        toSignUp = view.findViewById(R.id.btnToSignUp);
        myPrefContainer = main.getSharedPreferences(PREFNAME, Activity.MODE_PRIVATE);
        dialog = new ProgressDialog(main);
        if((myPrefContainer!=null) && myPrefContainer.contains(keyUser)) {
            username = myPrefContainer.getString(keyUsername,null);
            password = myPrefContainer.getString(keyPassword,null);
            phone = myPrefContainer.getString(keyPhone,null);
            email = myPrefContainer.getString(keyEmail,null);
            userkey = myPrefContainer.getString(keyUser,null);
            hidepass = myPrefContainer.getString("hidepass",null);
            inputUsername.setText(username);
            inputPassword.setText(password);
            dialog.setTitle("Da dang nhap "+username+" "+password+" "+email+" "+phone+" "+userkey);
            dialog.show();
            readDatabase();

        }

        else {
            Thread thr = new Thread(new Runnable() {
                @Override
                public void run() {
                    StartUpUser();
                }
            });
            thr.start();
            toSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ft = getFragmentManager().beginTransaction();
                    ft.replace(R.id.login_container, new SignUpFragment());
                    ft.commit();

                    //getFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment()).commit();
                }
            });
            btnLogin = view.findViewById(R.id.btnLogin);
            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Khi test
                   loginAction();
                }
            });
        }
        return view;
    }

    private void loginAction(){
        //Khi sử dụng thật xóa 4 dòng trên, bỏ comment các dòng dưới này
        if (inputUsername.getText().toString().length() == 0) {
            Toast.makeText(main, "Tên đăng nhập không thể để trống", Toast.LENGTH_SHORT).show();
        } else if (inputPassword.getText().toString().length() == 0) {
            Toast.makeText(main, "Mật khẩu không thể để trống", Toast.LENGTH_SHORT).show();
        } else {
            username = inputUsername.getText().toString();
            password = inputPassword.getText().toString();
            User user = DataFirebase.checkUser(username,password);
            if (user != null){
                email = user.getEmail();
                phone =user.getPhone();
                userkey = user.getKey();
                hidepass = user.getHidepass();
                dialog.setTitle("Da dang nhap "+user.getUsername());
                dialog.show();

                //khoi tao database duoi internal
                MainActivity.dataResource.clearTable();
                createDatabase();
                //luu lai thong tin dang nhap
                SharedPreferences.Editor myPreEditor = myPrefContainer.edit();
                myPreEditor.putString(keyUsername,user.getUsername());
                myPreEditor.putString(keyPassword,user.getPassword());
                myPreEditor.putString(keyUser,user.getKey());
                myPreEditor.putString(keyEmail,user.getEmail());
                myPreEditor.putString(keyPhone,user.getPhone());
                myPreEditor.putString("hidepass",user.getHidepass());
                myPreEditor.commit();
            }

            else {
                Toast.makeText(main, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readDatabase() {
        MainActivity.images = MainActivity.dataResource.getAllImage();
        getImageForLayouts();
        thread_background();
    }

    private void thread_background(){
        Thread my = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = AllLayout.images.size() - 1; i >= AllLayout.images.size()
                        - 15 && i >=0; i--) {
                    if(AllLayout.images.get(i).getImgBitmap() == null)
                        AllLayout.images.get(i).setImgBitmap(getImageFromPath
                                (AllLayout.images.get(i).getPath()));
                }
                dialog.dismiss();
                gotoMainActivity();
            }
        });
        my.start();
    }

    private Bitmap getImageFromPath(String path) {
        Bitmap b =null;
        try {
            File f = new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {

            e.printStackTrace();
        }
        return b;
    }
    private void getImageForLayouts(){
        AllLayout.images.clear();
        TrashCanLayout.images.clear();
        FavoriteLayout.images.clear();
        for (int i = 0;i<MainActivity.images.size();i++){
            if(MainActivity.images.get(i).getDeleted().equals("F")){
                if (MainActivity.images.get(i).getHide().equals(("T"))) {
                    HideInAlbumLayoutFragment.images.add((MainActivity.images.get(i)));
                }else{
                    AllLayout.images.add(MainActivity.images.get(i));
                    if (MainActivity.images.get(i).getFavorite().equals("T")){
                        FavoriteLayout.images.add(MainActivity.images.get(i));
                    }
                }
            }
            else{
                TrashCanLayout.images.add(MainActivity.images.get(i));
            }
        }
        Log.e("LoginFragment","AllLayout: "+String.valueOf(AllLayout
                .images.size())
                +", FavoriteLayout: "+String.valueOf(FavoriteLayout.images.size())
                +", TrashCanLayout: "+String.valueOf(TrashCanLayout.images.size()));
    }
    private void gotoMainActivity(){
        Intent intent = new Intent(main, MainActivity.class);
        intent.putExtra("username",username);
        intent.putExtra("password",password);
        intent.putExtra("email",email);
        intent.putExtra("phone",phone);
        intent.putExtra("keyUser",userkey);
        intent.putExtra("hidepass",hidepass);

        startActivity(intent);
        main.finish();
    }
    private void createDatabase(){
        ArrayList<DataFirebase.UploadImage> imgs = new ArrayList<>();
        FirebaseFirestore fbf = FirebaseFirestore.getInstance();
        fbf.collection("user").document(userkey)
            .collection("Image").get().addOnCompleteListener(
        new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                for(QueryDocumentSnapshot document:task.getResult()){
                    //lay tat ca anh dang class upload
                    imgs.add(new DataFirebase.UploadImage(document.getData()));
                    // luu vao database va images cua mainActivity
                }
                Log.e("LoginFrament",String.valueOf(imgs.size()));
                saveToDatabase(imgs);
            }
        });


    }
    private void saveToDatabase(ArrayList<DataFirebase.UploadImage> imgs){
        Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            StorageReference mStoreageRef = FirebaseStorage.getInstance().getReference("Picture/"+userkey);
            for(DataFirebase.UploadImage upload:imgs){
                File newfile = MainActivity.dataResource.createNewFile(upload.getName());
                mStoreageRef.child(upload.getName()).getFile(newfile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        count++;
                        MainActivity.dataResource.insertImage(upload);
                        if(count == imgs.size()){
                            MainActivity.images = MainActivity.dataResource.getAllImage();
                            getImageForLayouts();
                            createAlbum();
                            thread_background();
                        }
                    }
                });
//            try {
//                URL url = new URL(upload.getImageUrl());
//                Bitmap bitmap = BitmapFactory.decodeStream(url.
//                    openConnection().getInputStream());
//                Image image = new Image(upload,bitmap);
//                image.setKey(upload.getKey());
//                image.setId(MainActivity.dataResource.InsertImage(image));
//                MainActivity.images.add(image);
//
//            } catch(IOException e) {
//                Log.e("Loginfragment",e.toString());
//            }
            }
            if(imgs.size() == 0){
                getImageForLayouts();
                dialog.dismiss();
                gotoMainActivity();
            }
        }
        });
        thread.start();
    }
    private void createAlbum(){
        // tao album
        FirebaseFirestore fbf = FirebaseFirestore.getInstance();
        fbf.collection("user").document(userkey)
        .collection("Album").get().addOnCompleteListener(
        new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<DataFirebase.UploadAlbum> albums =new ArrayList<>();
                for(QueryDocumentSnapshot document:task.getResult()){
                    albums.add(new DataFirebase.UploadAlbum(document.getData()));
                }
                saveAbToDatabase(albums);
                Log.e("LoginFrament",String.valueOf(albums.size()));
            }
        }
        );
    }
    private void saveAbToDatabase(ArrayList<DataFirebase.UploadAlbum> albums){
        for(DataFirebase.UploadAlbum album:albums){
            long idalbum = MainActivity.dataResource.InsertAlbum(album.getName(),album.getKey());
            String key_images = album.getKey_images();
            String[] keys = key_images.split("-");
            for(String key:keys){
                long id = 0;
                for(Image image:MainActivity.images){
                    if(image.getKey().equals(key)){
                        id = image.getId();
                    }
                }
                MainActivity.dataResource.InsertAlbumImage(idalbum,id);
            }
        }
    }
    private void StartUpUser(){
        DataFirebase.open();
    }
}