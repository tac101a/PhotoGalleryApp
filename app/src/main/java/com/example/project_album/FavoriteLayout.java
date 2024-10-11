package com.example.project_album;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class FavoriteLayout extends Fragment {
    public static ShowImageAdapter mGridAdapter;
    public static ArrayList<Image> images = new ArrayList<Image>();
    MainActivity main;
    Context context = null;
    TextView txtTotal;
    TextView txtFavoriteRecently;
    RecyclerView mGridView;
    Button btnChoose;
    Button btnUnFavoriteChosenImages;
    Button btnShareChosenImages;

    Bundle myOriginalMemoryBundle;
    LinearLayout lastLinear;
    LinearLayout mainLayout;

    public FavoriteLayout(ArrayList<Image> imgs) {
        this.images=imgs;
    }
    public  FavoriteLayout(){}
    public static FavoriteLayout newInstance(String strArg) {
        FavoriteLayout fragment = new FavoriteLayout();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Favorite", "onCreate size="+String.valueOf(images.size()));
        myOriginalMemoryBundle = savedInstanceState;

        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            //Auto đọc lại từ đầu luôn nó mới chính xác được, k cần ktra ==0 chi co mắc công
            // Lỡ nó xóa bên all rồi qua trash thì sao, lúc nos k vô if là sai r
            Log.e("fix loi","Hello");
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Khởi tạo lại danh sách ưa thích
//        for (int i = 0; i < AllLayout.images.size(); i++) {
//            if (AllLayout.images.get(i).getFavorite().equals("T")) {
//                Log.e("fix loi","anh ="+String.valueOf(AllLayout.images.get(i).getId()));
//                images.add(AllLayout.images.get(i));
//            }
//        }
        Log.e("DEBUG", "onCreateView of TrashCan");
        View mView = inflater.inflate(R.layout.fragment_favorite_layout, container, false);
        mainLayout = mView.findViewById(R.id.main_favorite_layout);
        mGridView = mView.findViewById(R.id.grid_view_favorite);
        txtTotal = mView.findViewById(R.id.txt_display_total_picture_favorite);
        btnChoose = mView.findViewById(R.id.btn_choose_in_favorite);
        btnUnFavoriteChosenImages = mView.findViewById(R.id.btn_no_favorite);
        btnShareChosenImages = mView.findViewById(R.id.btn_share_in_favorite_layout);
        lastLinear = mView.findViewById(R.id.last_linear_in_favorite);
        txtFavoriteRecently = mView.findViewById(R.id.txt_favorite_recently);
        DoSthWithOrientation(getResources().getConfiguration().orientation);
        mGridView = mView.findViewById(R.id.grid_view_favorite);
        mGridAdapter = new ShowImageAdapter(main, R.layout.item_image, images, this);
        mGridView.setAdapter(mGridAdapter);
//        Toast.makeText(main, "helllo", Toast.LENGTH_SHORT).show();
        txtTotal.setText("Total: " + String.valueOf(images.size()));
        txtTotal.setVisibility(View.VISIBLE);
        //set hinhf anh ở cuối
        mGridView.scrollToPosition(mGridAdapter.getItemCount() - 1);
        //Toast.makeText(main, "DAY LA CREATEVIEW", Toast.LENGTH_SHORT).show();

        //set theme
        setTheme(main.mainColorBackground,main.mainColorText);

        doBtnChooseWhenIsCancel();

        //Khi Click vào choose ảnh để xóa
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnChoose.getText().toString().equals("Chọn")) {
                    doBtnChooseWhenIsChoose();
                } else {
                    doBtnChooseWhenIsCancel();
                }
            }
        });
        return mView;
    }

    public void doBtnChooseWhenIsChoose() {

        txtFavoriteRecently.setText("Chọn ảnh");
        lastLinear.setVisibility(View.VISIBLE);
        btnUnFavoriteChosenImages.setVisibility(View.VISIBLE);

        mGridAdapter.setChooseSelection(true);
        btnChoose.setText("Hủy");
        btnUnFavoriteChosenImages.setText("Bỏ thích tất cả");
        btnShareChosenImages.setText("Chia sẻ tất cả");
        btnChoose.setBackgroundColor(getResources().getColor(R.color.green, context.getTheme()));
        main.mBottomNavigationView.setVisibility(View.GONE);
        //Chú ý chỗ này
        mGridAdapter.resetCount();
        btnUnFavoriteChosenImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Hiển thị Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                alertDialogBuilder.setMessage("Bạn đã chắc chắn muốn bỏ thích ảnh?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (btnUnFavoriteChosenImages.getText().toString().equals("Bỏ thích tất cả")) {
                            mGridAdapter.addAllIntoImageChosen();
                        }
                        for (int i = 0; i < mGridAdapter.chosenArrayImages.size(); i++) {
                            //unlike ở database
                            MainActivity.dataResource.unlikeImage(
                            mGridAdapter.chosenArrayImages.get(i).getId(),
                            mGridAdapter.chosenArrayImages.get(i).getKey());

                            long idImage = mGridAdapter.chosenArrayImages.get(i).getId();

                            //Unlike ở images
                            for (int i1 = 0; i1 < images.size(); i1++) {
                                if (images.get(i1).getId() == idImage) {
                                    images.get(i1).setFavorite("F");
                                    break;
                                }
                            }
                            //Unlike ở AllLayout- có vẻ này k cần
                            for (int i1 = 0; i1 < AllLayout.images.size(); i1++) {
                                if (AllLayout.images.get(i1).getId() == idImage) {
                                    AllLayout.images.get(i1).setFavorite("F");
                                    break;
                                }
                            }
                            // xóa image ở images hiện hành tức là chỉ còn ảnh ưa thích
                            for (int i1 = 0; i1 < images.size(); i1++) {
                                if (images.get(i1).getId() == idImage) {
                                    images.remove(i1);
                                    break;
                                }
                            }


                            //Unlike ở AlbumLayout
                           // main.albumLayout.updateFavorite(mGridAdapter.chosenArrayImages.get(i));
                        }
                        mGridAdapter.setmSelectedArray();
                        mGridAdapter.setChosenArrayImages();
                        mGridAdapter.notifyDataSetChanged();
                        doBtnChooseWhenIsCancel();

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
        txtTotal.setText("Tổng " + String.valueOf(images.size()));
    }

    public void doBtnChooseWhenIsCancel() {
        txtFavoriteRecently.setText("Ưa thích gần đây");
        lastLinear.setVisibility(View.GONE);
        btnUnFavoriteChosenImages.setVisibility(View.GONE);
        btnChoose.setText("Chọn");
        main.mBottomNavigationView.setVisibility(View.VISIBLE);
        mGridAdapter.setChooseSelection(false);
        if (images.size() == 0) {
            btnChoose.setVisibility(View.INVISIBLE);
        } else {
            btnChoose.setVisibility(View.VISIBLE);
        }

        btnChoose.setBackgroundColor(getResources().getColor(R.color.blue_press, context.getTheme()));
        mGridAdapter.setmSelectedArray();
        mGridAdapter.setChosenArrayImages();
        mGridAdapter.notifyDataSetChanged();
        txtTotal.setText("Tổng: " + String.valueOf(images.size()));
    }

    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of TrashCan");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of TrashCan");
        super.onPause();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = newConfig.orientation;
        DoSthWithOrientation(newOrientation);
        mGridAdapter.notifyDataSetChanged();
    }

    private void DoSthWithOrientation(int newOrientation) {
        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridView.setLayoutManager(new GridLayoutManager(getContext(),5));
        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridView.setLayoutManager(new GridLayoutManager(getContext(),3));
        }
    }
    public void update(){
        try {
            mGridAdapter.notifyDataSetChanged();
        }
        catch(Exception e){

        }
    }
    public void updateFavorite(Image img){
        if (img.getFavorite().equals("T")) {
            images.add(img);
        } else {
            images.remove(img);
        }
    }
    public void updateArrayFavorite(ArrayList<Image> imgs){
        for(int i = 0;i<imgs.size();i++){
            if (imgs.get(i).getFavorite().equals("F")){
                images.add(imgs.get(i));
            }
        }
    }
    public void removeImageOutFavorite(Image img){
        images.remove(img);
    }
    public void updateIndex(int index){
        mGridView.scrollToPosition(index);
    }
    //Set theme
    private void setTheme(int backgroundColor, ColorStateList textColor) {
        setThemeBackGround(backgroundColor);
        setThemeText(textColor);
    }

    private void setThemeBackGround(int backgroundColor) {
        mainLayout.setBackgroundColor(backgroundColor);
    }

    private void setThemeText(ColorStateList textColor) {
        txtTotal.setTextColor(textColor);
        txtFavoriteRecently.setTextColor(textColor);

        btnChoose.setTextColor(textColor);
        btnUnFavoriteChosenImages.setTextColor(textColor);
        btnShareChosenImages.setTextColor(textColor);
    }
}