package com.example.project_album;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.net.URL;

public class UrlFragment extends DialogFragment {
    private String url;
    public String getUrl() {
        return url;
    }
    private ImageView URLImage;
    public static String Tag = "URLDialog";
    private EditText edtURL;
    MainActivity main;
    Image img;
    TextView tv_find;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            main = (MainActivity) getActivity();
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        AlertDialog.Builder urlDialog = new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom);
        AlertDialog.Builder urlDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = main.getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_url, null);
        edtURL = view.findViewById(R.id.edtURL);
        URLImage = view.findViewById(R.id.urlImage);
        tv_find = view.findViewById(R.id.tv_find);
        urlDialog.setView(view)
                .setPositiveButton("Tải về", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(img!=null) {
                            MainActivity.dataFirebase.insertImage(img);
                        }
                        else{
                            Toast.makeText(main,"Không tìm thấy link",Toast.LENGTH_SHORT).show();
                            edtURL.setText("");
                        }
                    }
                }).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dismiss();
                    }
                });
        tv_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = edtURL.getText().toString();
                if(url.startsWith("https://") || url.startsWith("http://")) {
                    DownloadImgURL(url);
                }
                else{
                    Toast.makeText(main,"Không tìm thấy link",Toast.LENGTH_SHORT).show();
                    edtURL.setText("");
                }
            }
        });

        return urlDialog.create();
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.e("Qdsf","fdsafs");
            URLImage.setVisibility(View.VISIBLE);
            URLImage.setImageBitmap(img.getImgBitmap());
        }
    };
    private Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            Toast.makeText(main,"Không phải link ảnh",
                    Toast.LENGTH_SHORT).show();
            edtURL.setText("");
        }
    };
    private Handler handler = new Handler();
    public void DownloadImgURL (String url) {
        boolean network = checkInternetConnection();
        if (!network) {
            return;
        }
        else {
            ProgressDialog dia = new ProgressDialog(main);
            dia.show();
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        URL Url = new URL(url);
                        Bitmap image = BitmapFactory.decodeStream(Url.openConnection().getInputStream());
                        //URLImage.setImageBitmap(image);
                        img = new Image(image,main.GenerateName());
                        handler.post(runnable);
                    }
                    catch(Exception e) {
                        img = null;
                        handler.post(runnable1);
                    }
                    dia.dismiss();
                }
            });
            thread.start();
        }
    }

    public boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            Toast.makeText(getContext(), "No network is currently active!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!networkInfo.isConnected()) {
            Toast.makeText(getContext(), "Network is not connected!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!networkInfo.isAvailable()) {
            Toast.makeText(getContext(), "Network is not available!", Toast.LENGTH_SHORT).show();
            return false;
        }

        Toast.makeText(getContext(), "Network is OK!", Toast.LENGTH_SHORT).show();
        return true;
    }
    private void succesfull(){
        Toast.makeText(main,"Đã tải về",Toast.LENGTH_SHORT).show();
        dismiss();
    }
}