package com.example.project_album;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

public class ViewPagerAllLayoutFragment extends Fragment {

    ArrayList<Image> images;
    ViewPager2 mViewPager;
    TextView txtTimeCapture;
    ImageView txtSetWallPaper;

    ImageView txtDeleteIntoTrashCan;
    ImageView imgBack;
    ImageView txtFavorite;

    ImageView txtShare;

    ImageView txtEdit;
    ImageView txtConvertText;
    TextView tv_animate;
    View view1;
    MainActivity main;
    Context context = null;
    ViewPagerInTrashCanAdapter mAdapter;
    private TextRecognizer recognizer;

    private LinearLayout ln_top;
    private LinearLayout ln_bottom;
    private boolean isSlider = false;
    private Handler handler = new Handler();
    int index = 0;//Vị trí được chọn hiện tại
    RelativeLayout mainLayout;

    public ViewPagerAllLayoutFragment(ArrayList<Image> imgs, int index) {
        this.images = imgs;
        this.index = index;
    }

    public ViewPagerAllLayoutFragment(ArrayList<Image> imgs) {
        this.images = imgs;
        this.index = 0;
        isSlider = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    private void onPageChange() {
        Log.e("ViewPaper ", String.valueOf(index));
        //Để khi mà bấm vào xem ảnh lớn ở bên mục favorite thì k còn cái xóa
        if (main.getIDItemBottomNavigationView() == R.id.action_favorite) {
            txtDeleteIntoTrashCan.setVisibility(View.GONE);
        } else {
            txtDeleteIntoTrashCan.setVisibility(View.VISIBLE);
        }
        txtTimeCapture.setText(String.valueOf(index + 1) + "/" + String.valueOf(images.size()));
        if (images.get(index).getFavorite().equals("T")) {
            txtFavorite.setImageResource(R.drawable.icon_fill_favorite_in_all_layout);
        } else {
            txtFavorite.setImageResource(R.drawable.icon_favorite_in_alllayout);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager_all_layout, container, false);
        //Chỗ này là imageView mà sa chwua đổi txt tên ở id bên xml á.
        ln_top = view.findViewById(R.id.top);
        ln_bottom = view.findViewById(R.id.bottom);
        mViewPager = view.findViewById(R.id.viewpager_in_all_layout);
        view1 = view.findViewById(R.id.view1);
        // chỗ nay tan dung lai Adapter ben TrashCan khoi can tao moi
        mAdapter = new ViewPagerInTrashCanAdapter(main.getSupportFragmentManager(), main.getLifecycle(), images);
        mViewPager.setAdapter(mAdapter);
        if (!isSlider) {
            ln_top.setVisibility(View.VISIBLE);
            ln_bottom.setVisibility(View.VISIBLE);
            mViewPager.setCurrentItem(index, false);
            main.mBottomNavigationView.setVisibility(View.GONE);
            txtTimeCapture = view.findViewById(R.id.edt_time_capture);
            txtDeleteIntoTrashCan = view.findViewById(R.id.txt_delete_into_trashcan);
            txtFavorite = view.findViewById(R.id.txt_favorite);
            txtSetWallPaper = view.findViewById(R.id.txt_setWallpaper);
            txtShare = view.findViewById(R.id.txt_share);
            txtEdit = view.findViewById(R.id.txt_edit);
            txtConvertText = view.findViewById(R.id.txt_convertText);
            imgBack = view.findViewById(R.id.img_back);
            mainLayout=view.findViewById(R.id.main_layout);
            setTheme(main.mainColorBackground,main.mainColorText);
            onPageChange();

            EventForAll();
        }
        //view paper dung de tao slide================================================
        else {
            mViewPager.animate().setDuration(500);
            tv_animate = view.findViewById(R.id.tv_animate);
            tv_animate.setTextColor(Color.MAGENTA);
            tv_animate.animate().setDuration(1000);
            tv_animate.setVisibility(View.VISIBLE);
            mViewPager.setClipToPadding(false);
            mViewPager.setClipChildren(false);
            mViewPager.setOffscreenPageLimit(3);
            mViewPager.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


            ln_bottom.setVisibility(View.INVISIBLE);
            ln_top.setVisibility(View.INVISIBLE);
        }

        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (!isSlider) {
                    index = position;
                    onPageChange();
                } else {
                    handler.removeCallbacks(sliderRunable);
                    handler.postDelayed(sliderRunable, 2000);
                }
            }
        });

        return view;
    }

    private Runnable sliderRunable = new Runnable() {
        @Override
        public void run() {
            if (mViewPager.getCurrentItem() + 1 == images.size()) {
                closeFragment();
                main.mBottomNavigationView.setVisibility(View.VISIBLE);
            }
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            mViewPager.setBackgroundColor(RandomBackgroundColor());
            tv_animate.setText("Picture " + String.valueOf(mViewPager.getCurrentItem() + 1));
            tv_animate.setScaleX(1);
            tv_animate.setScaleY(1);
            tv_animate.setTextSize(50);
            tv_animate.setTranslationX(0);
            tv_animate.setTranslationY(0);
            tv_animate.setRotation(0);
            getAnimateOnTextView();

        }

        private void getAnimateOnTextView() {
            Random random = new Random();
            int type = random.nextInt(4);
            if (type == 0) {
                tv_animate.setTextSize(5);
                tv_animate.animate().scaleX(10).withLayer();
                tv_animate.animate().scaleY(10).withLayer();
            } else if (type == 1) {
                tv_animate.setTranslationX(700);
                tv_animate.animate().translationX(0).withLayer();

            } else if (type == 2) {
                tv_animate.setTextSize(5);
                tv_animate.animate().scaleX(10).withLayer();
                tv_animate.animate().scaleY(10).withLayer();
                tv_animate.animate().rotation(360).withLayer();
            } else if (type == 3) {
                tv_animate.setTranslationY(-500);
                tv_animate.animate().translationY(0).withLayer();
            }
        }

        private int RandomBackgroundColor() {
            int color[] = {Color.BLACK, Color.BLUE, Color.YELLOW, Color.GREEN};
            Random random = new Random();
            int i = Math.abs(random.nextInt() % 4);
            Log.e("Viewpaper", String.valueOf(i));
            return color[i];
        }
    };

    private float RandomTranlation() {
        return 1;
    }

    private void closeFragment() {

        FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();
        main.getSupportFragmentManager().popBackStack();
        transaction.remove(this);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = newConfig.orientation;
        doSthWithOrientation(newOrientation);
    }

    private void doSthWithOrientation(int newOrientation) {
        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    private Uri saveImage(Bitmap image) {
        File imagesFolder = new File(main.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(main, "com.example.fileprovider", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    private void shareImageUri(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        main.startActivity(intent);
    }

    private void EventForAll() {
        txtShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri UriToShare = saveImage(images.get(index).getImgBitmap());
                shareImageUri(UriToShare);
            }
        });
        txtDeleteIntoTrashCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                alertDialogBuilder.setMessage("Bạn có muốn xóa ảnh?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Nếu xóa thì thay đổi ảnh là unlike ở favorite và ở albumfavorite
                        Image image = images.get(index);
                        Log.e("asda",image.getDate());
                        if (image.getFavorite().equals("T")) {
                            image.setFavorite("F");
                            main.favoriteLayout.updateFavorite(image);
                            // main.albumLayout.updateFavorite(images.get(index));
                        }

                        //
                        long idImageDelete = image.getId();
                        //Set hình ảnh đã xóa là True
                        image.setDeleted("T");
                        //Set ở dataResource là True đã xóa
                        MainActivity.dataResource.updateStateImageDeletedIsTrue(
                                image.getId(),image.getKey());

                        // cap nhật ở trashcan. them vao trash can
                        main.trashCanLayout.updateTrashCan(image);


                        //xóa ở images cua viewpager, k hiểu tại sao cái này nó lại xóa luôn
                        //bên adapter của AllLayout nữa
                        images.remove(image);
                        if (index == images.size()){
                            index = images.size()-1;
                        }
                        //nay la phong truong hop adapter truyen vao ko phai adapter cua all
                        AllLayout.images.remove(image);
                        if (images.size() == 0) {
                            imgBack.callOnClick();
                        }
                        MainActivity.dataResource.deleteImageInAlbum(image);
                        main.albumLayout.updateAlbumFirebase();
                        mAdapter.notifyDataSetChanged();
                        mViewPager.setAdapter(mAdapter);
                        mViewPager.setCurrentItem(index, false);
                        txtTimeCapture.setText(String.valueOf(index + 1) + "/" + String.valueOf(images.size()));
                        //xoa image o allLayout
//                        for (int i = 0; i < AllLayout.images.size(); i++) {
//                            if (AllLayout.images.get(i).getId() == idImageDelete) {
//                                AllLayout.images.remove(i);
//                                Log.e("TestAll", "---------------------");
//                                break;
//                            }
//                        }
                    }
                });


                alertDialogBuilder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Xử lý khi chọn Cancel
                        dialog.dismiss(); // Đóng AlertDialog mà không làm gì cả
                    }
                });
                // Hiển thị AlertDialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });
        txtFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (images.get(index).getFavorite().equals("T")) {
                    //khả năng chỗ ni là nó đã set luôn o AllLayout là F
                    images.get(index).setFavorite("F");
                    txtFavorite.setImageResource(R.drawable.icon_favorite_in_alllayout);
                    MainActivity.dataResource.unlikeImage(images.get(index).getId()
                    ,images.get(index).getKey());


                    if (main.getIDItemBottomNavigationView() == R.id.action_all_picture) {
                        //Xóa Favorite khỏi album- Huy cs xử lí bên Huy r
                        //main.albumLayout.updateFavorite(images.get(index));
                        main.favoriteLayout.images.remove(images.get(index));
                    } else if (main.getIDItemBottomNavigationView() == R.id.action_favorite) {
                        //Xóa Favorite khỏi album
                        //main.albumLayout.updateFavorite(images.get(index));
                        if (index == 0) {
                            main.favoriteLayout.images.remove(0);
                            index = 0;
                        } else if (index == main.favoriteLayout.images.size()) {
                            main.favoriteLayout.images.remove(main.favoriteLayout.images.size() - 1);
                            index = main.favoriteLayout.images.size() - 1;
                        } else {
                            main.favoriteLayout.images.remove(index);
                        }
                        try {
                            //Nếu xóa hết ảnh ưa thích thì nó sẽ tro ve man hinh favorite
                            if (images.size() == 0) {
                                imgBack.callOnClick();
                            }
                            mAdapter = new ViewPagerInTrashCanAdapter(main.getSupportFragmentManager(), main.getLifecycle(), images);
                            mViewPager.setAdapter(mAdapter);
                            mViewPager.setCurrentItem(index, false);
                            txtTimeCapture.setText(String.valueOf(index + 1) + "/" + String.valueOf(images.size()));
                        } catch (Exception e) {
                            Toast.makeText(main, "Loi o try catch r nhe", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        main.favoriteLayout.images.remove(images.get(index));
                    }


                } else {
                    images.get(index).setFavorite("T");
                    //Thêm Favorite vào album
                    //main.albumLayout.updateFavorite(images.get(index));
                    txtFavorite.setImageResource(R.drawable.icon_fill_favorite_in_all_layout);
                    MainActivity.dataResource.likeImage(images.get(index).getId(),images.get(index).getKey());
                    main.favoriteLayout.images.add(images.get(index));

                }

            }
        });

        txtSetWallPaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(main, "Set Wallpaper", Toast.LENGTH_SHORT).show();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                images.get(mViewPager.getCurrentItem()
                ).getImgBitmap().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte imageInByte[] = stream.toByteArray();
                main.setWallPaper(imageInByte);
            }
        });
        //PHAN EDIT
        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.replace_fragment_layout, new EditFragment(images, index));
                ft.commit();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentmanager.beginTransaction();
                if (main.getIDItemBottomNavigationView() == R.id.action_favorite) {
                    ft.replace(R.id.replace_fragment_layout, main.favoriteLayout);
                } else if (main.getIDItemBottomNavigationView() == R.id.action_all_picture) {
                    ft.replace(R.id.replace_fragment_layout, main.allLayout);
                }
//                else if (main.getIDItemBottomNavigationView() == R.id.action_album) {
//                    ft.replace(R.id.replace_fragment_layout, main.albumLayout);
//                    main.albumLayout.update();
//                }
                else{
                    closeFragment();
                    main.albumLayout.update();

                }
                ft.commit();
                main.mBottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
        txtConvertText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizeTextFromImage(images.get(index));
            }
        });
    }
    private void recognizeTextFromImage(Image img){
        Bitmap bitmap = img.getImgBitmap();
        if (bitmap == null){
            Log.e("TextRecognition", "Bitmap is null.");
            return;
        }
        try {
            final String[] recognizedText = new String[1]; // Use an array to hold the recognized text
            AlertDialog.Builder dialog = new AlertDialog.Builder(main);

            // Define the positive button, but don't set it yet because 'recognizedText' is not known
            DialogInterface.OnClickListener copyButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ClipboardManager clipboard = (ClipboardManager) main.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("recognizedText", recognizedText[0]);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(main, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            };

            InputImage image = InputImage.fromBitmap(bitmap,0);
            Task<Text> textTaskResult = recognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            recognizedText[0] = text.getText().toString();
                            if (recognizedText[0].equals("")){
                                dialog.setMessage("[Không tìm thấy text trên ảnh]");
                            } else {
                                dialog.setMessage(recognizedText[0]);
                            }
                            dialog.setPositiveButton("Copy", copyButtonListener);
                            dialog.setNegativeButton("Xong", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            dialog.create().show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.setMessage("[Đã xảy ra lỗi]");
                            dialog.create().show();
                        }
                    });
        }catch (Exception e){
            Toast.makeText(main,"Failed due to:" +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //Set theme
    private void setTheme(int backgroundColor, ColorStateList textColor) {
        setThemeBackGround(backgroundColor);
        setThemeText(textColor);
    }

    private void setThemeBackGround(int backgroundColor) {
        mainLayout.setBackgroundColor(backgroundColor);
        mViewPager.setBackgroundColor(backgroundColor);
        ln_top.setBackgroundColor(backgroundColor);
        ln_bottom.setBackgroundColor(backgroundColor);
    }

    private void setThemeText(ColorStateList textColor) {
        txtTimeCapture.setTextColor(textColor);
        imgBack.setImageTintList(textColor);
        txtConvertText.setImageTintList(textColor);
        txtShare.setImageTintList(textColor);
        txtEdit.setImageTintList(textColor);
        txtFavorite.setImageTintList(textColor);
        txtSetWallPaper.setImageTintList(textColor);
        txtDeleteIntoTrashCan.setImageTintList(textColor);
        txtTimeCapture.setTextColor(textColor);
        view1.setBackgroundColor(textColor.getDefaultColor());
    }
}