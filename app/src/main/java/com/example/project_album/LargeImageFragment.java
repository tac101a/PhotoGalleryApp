package com.example.project_album;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class LargeImageFragment extends Fragment {
    // Tấm này truyền vào để delete
    Image myImage;
    ImageView imgDelete;
    MainActivity main;
    Context context = null;
    public LargeImageFragment(Image myImage) {
        this.myImage=myImage;
    }

    long zoom=1;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();

        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_layout_solo_picture_in_trashcan, container, false);
        imgDelete=view.findViewById(R.id.img_delete);

        imgDelete.setImageBitmap(myImage.getImgBitmap());

        imgDelete.setOnTouchListener(new View.OnTouchListener() {
            private long lastTapTimeMs = 0;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                long currentTime = System.currentTimeMillis();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (currentTime - lastTapTimeMs < 400) {
                        // Double-tap detected
                        Toast.makeText(main, "Double Click", Toast.LENGTH_SHORT).show();

//                        Matrix matrix = new Matrix();

//                        matrix.setScale(2.0f,2.0f,x,y);
//                        Bitmap flipdBitmap = Bitmap.createBitmap(myImage.getImgBitmap(), 0, 0,
//                                myImage.getImgBitmap().getWidth(), myImage.getImgBitmap().getHeight(), matrix, true);
//                        //image.setImgBitmap(rotatedBitmap);
//                        myImage.setImgBitmap(flipdBitmap);
//                        imgDelete.setImageBitmap(flipdBitmap);
//                        imgDelete.setScaleType(ImageView.ScaleType.MATRIX);
                        float x = event.getX();
                        float y = event.getY();
                        zoomImage(zoom,x,y,imgDelete);
                        zoom=zoom*-1;


                    }
                    lastTapTimeMs = currentTime;
                }

                return true;
            }
        });
        return view;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = newConfig.orientation;
        doSthWithOrientation(newOrientation);
    }

    private void doSthWithOrientation(int newOrientation ) {
        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }
    private void zoomImage(long zoom,float x, float y,ImageView imgView)
    {
        if (zoom==1){
            Matrix matrix = imgView.getImageMatrix();
            matrix.postScale(2.0f, 2.0f,x,y);
            imgView.setImageMatrix(matrix);
            imgView.setScaleType(ImageView.ScaleType.MATRIX);
        }
        else{
            imgView.setScaleX(1.0f);
            imgView.setScaleY(1.0f);
            imgView.setImageMatrix(new Matrix());
            imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        }
    }


}
