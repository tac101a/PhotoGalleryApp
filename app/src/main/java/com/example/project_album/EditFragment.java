package com.example.project_album;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class EditFragment extends Fragment {

    private Canvas canvas;
    private Paint paint;

    Context context = null;
    MainActivity main;
    ImageView imageView;
    Bitmap originalBitmap ;
    Bitmap tempfilter;
    ImageView filter1,filter2,filter3,filter4,filter5,filter6,filter7,filter8;
    int size=5;

    ArrayList<Image> images = new ArrayList<>();
    int index=0;
    EditFragment(ArrayList<Image> imgs, int index){

        this.images=imgs;
        this.index=index;
        this.originalBitmap=images.get(index).getImgBitmap();
    }
    EditFragment(){}




    public static EditFragment newInstance(String strArg) {
        EditFragment fragment = new EditFragment();
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
            main = (MainActivity) getActivity();

        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_edit, container, false);
        imageView=view.findViewById(R.id.imgview);
        imageView.setImageBitmap(images.get(index).getImgBitmap());
        ImageView right=view.findViewById(R.id.txt_right);
        ImageView left=view.findViewById(R.id.txt_left);
        ImageView flip=view.findViewById(R.id.txt_flip);
        ImageView draw=view.findViewById(R.id.txt_draw);
        ImageView filter=view.findViewById(R.id.txt_filter);
        ImageView cancel=view.findViewById(R.id.cancel);
        ImageView save=view.findViewById(R.id.save);
        HorizontalScrollView scrollView=view.findViewById(R.id.srcollview);
        filter1=view.findViewById(R.id.filter1);
        filter2=view.findViewById(R.id.filter2);
        filter3=view.findViewById(R.id.filter3);
        filter4=view.findViewById(R.id.filter4);
        filter5=view.findViewById(R.id.filter5);
        filter6=view.findViewById(R.id.filter6);
        filter7=view.findViewById(R.id.filter7);
        filter8=view.findViewById(R.id.filter8);


        LinearLayout color_layout=view.findViewById(R.id.color);
        SeekBar sb_red = view.findViewById(R.id.seekBar_red);
        SeekBar sb_green = view.findViewById(R.id.seekBar_green);
        SeekBar sb_blue = view.findViewById(R.id.seekBar_blue);
        View view1 = view.findViewById(R.id.view_color);
        int color = Color.argb(255,sb_red.getProgress()*255/100,
                sb_green.getProgress()*255/100,sb_blue.getProgress()*255/100);
        view1.setBackgroundColor(color);
//        ImageView color1=view.findViewById(R.id.color1);
//        ImageView color2=view.findViewById(R.id.color2);
//        ImageView color3=view.findViewById(R.id.color3);
//        ImageView color4=view.findViewById(R.id.color4);
//        ImageView color5=view.findViewById(R.id.color5);
//        ImageView color6=view.findViewById(R.id.color6);
        ImageView increase=view.findViewById(R.id.increase);
        ImageView decrease=view.findViewById(R.id.decrease);



        //định dạng nét vẽ
        paint = new Paint();
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);

        initFilter();


        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setEnabled(false);
                color_layout.setVisibility(View.INVISIBLE);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                Bitmap rightBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                        originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                //image.setImgBitmap(rotatedBitmap);
                originalBitmap=rightBitmap;
                imageView.setImageBitmap(rightBitmap);
                initFilter();
            }
        });
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setEnabled(false);
                color_layout.setVisibility(View.INVISIBLE);
                Matrix matrix = new Matrix();
                matrix.postRotate(-90);
                Bitmap leftBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                        originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                //image.setImgBitmap(rotatedBitmap);
                originalBitmap=leftBitmap;
                imageView.setImageBitmap(leftBitmap);
                initFilter();
            }
        });
        flip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setEnabled(false);
                color_layout.setVisibility(View.INVISIBLE);
                Matrix matrix = new Matrix();
                matrix.postScale(-1,1);
                Bitmap flipdBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                        originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
                //image.setImgBitmap(rotatedBitmap);
                originalBitmap=flipdBitmap;
                imageView.setImageBitmap(flipdBitmap);
                initFilter();
            }
        });
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(main, "Draw", Toast.LENGTH_SHORT).show();
                scrollView.setVisibility(View.INVISIBLE);
                color_layout.setVisibility(view.VISIBLE);
                imageView.setEnabled(true);
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    float startX, startY;
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // chuyển sang bitmap có thể thay đổi
                        originalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                        canvas = new Canvas(originalBitmap);
                        //chuyển tọa độ trên imageview lên ảnh gốc
                        Matrix matrix = imageView.getImageMatrix();
                        Matrix inverseMatrix = new Matrix();
                        matrix.invert(inverseMatrix);
                        float[] touchPoint = {event.getX(), event.getY()};
                        inverseMatrix.mapPoints(touchPoint);

                        float originalX = touchPoint[0];
                        float originalY = touchPoint[1];
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                startX = originalX;
                                startY = originalY;
                                break;
                            case MotionEvent.ACTION_MOVE:
                                float endX = originalX;
                                float endY = originalY;
                                canvas.drawLine(startX, startY, endX, endY, paint);
                                startX = endX;
                                startY = endY;
                                imageView.setImageBitmap(originalBitmap);
                                initFilter();
                                break;

                        }
                        return true;
                    }
                });



            }
        });

        sb_red.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int color = Color.argb(255,sb_red.getProgress()*255/100,
                        sb_green.getProgress()*255/100,sb_blue.getProgress()*255/100);
                view1.setBackgroundColor(color);
                paint.setColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_green.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int color = Color.argb(255,sb_red.getProgress()*255/100,
                        sb_green.getProgress()*255/100,sb_blue.getProgress()*255/100);
                view1.setBackgroundColor(color);
                paint.setColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb_blue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int color = Color.argb(255,sb_red.getProgress()*255/100,
                        sb_green.getProgress()*255/100,sb_blue.getProgress()*255/100);
                view1.setBackgroundColor(color);
                paint.setColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        color1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paint.setColor(Color.WHITE);
//            }
//        });
//        color2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paint.setColor(Color.BLACK);
//            }
//        });
//        color3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paint.setColor(Color.RED);
//            }
//        });
//        color4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paint.setColor(Color.GREEN);
//            }
//        });
//        color5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paint.setColor(Color.parseColor("#FF69B4"));
//            }
//        });
//        color6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                paint.setColor(Color.parseColor("#FFFF00"));
//            }
//        });
//
//
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                size+=3;
                paint.setStrokeWidth(size);
            }
        });
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                size-=3;
                if(size<1) size=1;
                paint.setStrokeWidth(size);
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(main, "Filter", Toast.LENGTH_SHORT).show();
                scrollView.setVisibility(View.VISIBLE);
                imageView.setEnabled(false);
                color_layout.setVisibility(View.INVISIBLE);

            }
        });
        filter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap=images.get(index).getImgBitmap();
                imageView.setImageBitmap(originalBitmap);
            }
        });
        filter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap=images.get(index).getImgBitmap();

                originalBitmap=setfilter1(originalBitmap,0.5f);
                imageView.setImageBitmap(originalBitmap);
            }
        });
        filter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap=images.get(index).getImgBitmap();

                originalBitmap=setfilter2(originalBitmap,0.333333f);

                imageView.setImageBitmap(originalBitmap);
            }
        });
        filter4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap=images.get(index).getImgBitmap();

                originalBitmap=setfilter3(originalBitmap,-1.0f);
                imageView.setImageBitmap(originalBitmap);
            }
        });
        filter5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap=images.get(index).getImgBitmap();

                originalBitmap=setfilter2(originalBitmap,0.1f);

                imageView.setImageBitmap(originalBitmap);
            }
        });
        filter6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap=images.get(index).getImgBitmap();

                originalBitmap=setfilter1(originalBitmap,1.2f);

                imageView.setImageBitmap(originalBitmap);
            }
        });
        filter7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap=images.get(index).getImgBitmap();

                originalBitmap=setfilter2(originalBitmap,1.5f);

                imageView.setImageBitmap(originalBitmap);
            }
        });
        filter8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                originalBitmap=images.get(index).getImgBitmap();

                originalBitmap=setfilter1(originalBitmap,0.75f);

                imageView.setImageBitmap(originalBitmap);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //images.get(index).setImgBitmap(originalBitmap);
                Image image = new Image(originalBitmap,main.GenerateName());
                MainActivity.dataFirebase.insertImage(image);
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentmanager.beginTransaction();
                ViewPagerAllLayoutFragment newFragment=new ViewPagerAllLayoutFragment(images,index);
                ft.replace(R.id.replace_fragment_layout, newFragment);
                ft.commit();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentmanager.beginTransaction();
                ViewPagerAllLayoutFragment newFragment=new ViewPagerAllLayoutFragment(images,index);
                ft.replace(R.id.replace_fragment_layout, newFragment);
                ft.commit();
            }
        });

        //set tintcolor
        right.setImageTintList(main.mainColorText);
        left.setImageTintList(main.mainColorText);
        flip.setImageTintList(main.mainColorText);
        draw.setImageTintList(main.mainColorText);
        filter.setImageTintList(main.mainColorText);
        cancel.setImageTintList(main.mainColorText);
        save.setImageTintList(main.mainColorText);
        increase.setImageTintList(main.mainColorText);
        decrease.setImageTintList(main.mainColorText);
        return view;
    }

    private void initFilter() {
        tempfilter=originalBitmap;
        filter1.setImageBitmap(tempfilter);

        tempfilter=setfilter1(originalBitmap,0.5f);
        filter2.setImageBitmap(tempfilter);

        tempfilter=setfilter2(originalBitmap,0.33333f);
        filter3.setImageBitmap(tempfilter);

        tempfilter=setfilter3(originalBitmap,-1.0f);
        filter4.setImageBitmap(tempfilter);

        tempfilter=setfilter2(originalBitmap,0.1f);
        filter5.setImageBitmap(tempfilter);

        tempfilter=setfilter1(originalBitmap,1.2f);
        filter6.setImageBitmap(tempfilter);

        tempfilter=setfilter2(originalBitmap,1.5f);
        filter7.setImageBitmap(tempfilter);

        tempfilter=setfilter1(originalBitmap,0.75f);
        filter8.setImageBitmap(tempfilter);
    }

    public Bitmap setfilter1(Bitmap originalBitmap,float x){
        // chuyển sang bitmap có thể thay đổi
        originalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(originalBitmap);
        //biến đổi màu sắc ảnh
        ColorMatrix colorMatrix=new ColorMatrix();
        colorMatrix.set(new float[] {
                x, 0,0, 0, 0,
                0,x,0, 0, 0,
                0,0,x, 0, 0,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(originalBitmap,0, 0, paint);
        return originalBitmap;
    }
    public Bitmap setfilter2(Bitmap originalBitmap,float x){
        // chuyển sang bitmap có thể thay đổi
        originalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(originalBitmap);
        //biến đổi màu sắc ảnh
        ColorMatrix colorMatrix=new ColorMatrix();
        colorMatrix.set(new float[] {
                x, x,x, 0, 0,
                x,x,x, 0, 0,
                x,x,x, 0, 0,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(originalBitmap,0, 0, paint);
        return originalBitmap;
    }
    public Bitmap setfilter3(Bitmap originalBitmap,float x){
        // chuyển sang bitmap có thể thay đổi
        originalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        canvas = new Canvas(originalBitmap);
        //biến đổi màu sắc ảnh
        ColorMatrix colorMatrix=new ColorMatrix();
        colorMatrix.set(new float[] {
                x, 0,0, 0, 255,
                0,x,0, 0, 255,
                0,0,x, 0, 255,
                0, 0, 0, 1, 0
        });
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(originalBitmap,0, 0, paint);
        return originalBitmap;
    }
}