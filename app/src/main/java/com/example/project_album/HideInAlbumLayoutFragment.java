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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HideInAlbumLayoutFragment extends Fragment {

    public ShowImageAdapter mGridAdapter;
    public static ArrayList<Image> images = new ArrayList<Image>();
    MainActivity main;
    Context context = null;
    TextView txtTotalHide;
    TextView txtHideRecently;
    RecyclerView mGridView;
    Button btnChoose;
    Button btnCancelHideChosenImages;

    Bundle myOriginalMemoryBundle;
    LinearLayout lastLinear;
    ImageView imgBack;
    LinearLayout mainLayout;
    public HideInAlbumLayoutFragment() {
    }
    public HideInAlbumLayoutFragment(ArrayList<Image> mImages){
        images=mImages;
    }

    public static HideInAlbumLayoutFragment newInstance(String strArg) {
        HideInAlbumLayoutFragment fragment = new HideInAlbumLayoutFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("DEBUG", "onCreate of TrashCan");
        myOriginalMemoryBundle = savedInstanceState;

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
        main.mBottomNavigationView.setVisibility(View.GONE);
        // Inflate the layout for this fragment
        Log.e("Hide", "onCreateView of Hide");
        View mView = inflater.inflate(R.layout.fragment_hide_in_album_layout, container, false);
        mainLayout=mView.findViewById(R.id.main_layout_hide);
        mGridView = mView.findViewById(R.id.grid_view_hide);
        imgBack = mView.findViewById(R.id.img_back_in_hide);
        txtTotalHide = mView.findViewById(R.id.txt_display_total_picture_hide);
        btnChoose = mView.findViewById(R.id.btn_choose_in_hide);
        btnCancelHideChosenImages = mView.findViewById(R.id.btn_cancel_hide_chosen_image);
        lastLinear = mView.findViewById(R.id.last_linear_in_hide);
        txtHideRecently = mView.findViewById(R.id.txt_hide_recently);
        DoSthWithOrientation(getResources().getConfiguration().orientation);
        mGridView = mView.findViewById(R.id.grid_view_hide);
        mGridAdapter = new ShowImageAdapter(main, R.layout.item_image, images, this);
        mGridView.setAdapter(mGridAdapter);
//        Toast.makeText(main, "helllo", Toast.LENGTH_SHORT).show();
        txtTotalHide.setText("Tổng: " + String.valueOf(images.size()));
        txtTotalHide.setVisibility(View.VISIBLE);
        //set hinhf anh ở cuối
        mGridView.scrollToPosition(mGridAdapter.getItemCount() - 1);
        //Toast.makeText(main, "DAY LA CREATEVIEW", Toast.LENGTH_SHORT).show();

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
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentmanager.beginTransaction();
                ft.replace(R.id.replace_fragment_layout, main.albumLayout);
                ft.commit();
                main.albumLayout.setTextNumberHide(images.size());
                main.mBottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        return mView;
    }

    public void doBtnChooseWhenIsChoose() {

        txtHideRecently.setText("Chọn ảnh");
        lastLinear.setVisibility(View.VISIBLE);
        btnCancelHideChosenImages.setVisibility(View.VISIBLE);
        mGridAdapter.setChooseSelection(true);
        btnChoose.setText("Hủy");
        btnCancelHideChosenImages.setText("Bỏ ẩn tất cả");
        btnChoose.setBackgroundColor(getResources().getColor(R.color.green, context.getTheme()));
        main.mBottomNavigationView.setVisibility(View.GONE);
        //Chú ý chỗ này
        mGridAdapter.resetCount();


        btnCancelHideChosenImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                alertDialogBuilder.setMessage("Bạn có muốn bỏ ẩn các ảnh?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (btnCancelHideChosenImages.getText().toString().equals("Bỏ ẩn tất cả")) {
                            mGridAdapter.addAllIntoImageChosen();
                        }
                        for (int i = 0; i < mGridAdapter.chosenArrayImages.size(); i++) {
                            long idImage = mGridAdapter.chosenArrayImages.get(i).getId();
                            String key = mGridAdapter.chosenArrayImages.get(i).getKey();
                            //set trạng thái đã hide là false ở data resource
                            MainActivity.dataResource.updateStateImageHideIsFalse(idImage,key);

                            // set trạng thái Hide là False
                            for (int j = 0; j < images.size(); j++) {
                                if (images.get(j).getId() == idImage) {
                                    images.get(j).setHide("F");
                                    break;
                                }
                            }

                            //add ảnh vào lại allLayout đúng vị trí đã đem đi hide
                            if (idImage > AllLayout.images.get(AllLayout.images.size() - 1).getId()) {
                                AllLayout.images.add(mGridAdapter.chosenArrayImages.get(i));
                            } else if (idImage < AllLayout.images.get(0).getId()) {
                                AllLayout.images.add(0, mGridAdapter.chosenArrayImages.get(i));
                            } else {
                                for (int j = 1; j < AllLayout.images.size(); j++) {
                                    if (AllLayout.images.get(j - 1).getId() < idImage
                                            && idImage < AllLayout.images.get(j).getId()) {
                                        AllLayout.images.add(j, mGridAdapter.chosenArrayImages.get(i));
                                        break;
                                    }
                                }
                            }
                            //add ảnh vào lại favorite nếu ảnh đó là ưa thích
                            if (mGridAdapter.chosenArrayImages.get(i).getFavorite().equals("T")){
                                main.favoriteLayout.updateFavorite(mGridAdapter.chosenArrayImages.get(i));
                            }
                            // Xóa ảnh ở imgaes ẩn hiện hành là cũng như xóa ở adapter hiện hành r
                            for (int j = 0; j < images.size(); j++) {
                                if (images.get(j).getId() == idImage) {
                                    images.remove(j);
                                    break;
                                }
                            }

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
        txtTotalHide.setText("Tổng " + String.valueOf(images.size()));
    }

    public void doBtnChooseWhenIsCancel() {
        txtHideRecently.setText("Ảnh ẩn gần đây");
        lastLinear.setVisibility(View.GONE);
        btnCancelHideChosenImages.setVisibility(View.GONE);
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
        txtHideRecently.setText("Ảnh ẩn gần đây");
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
            mGridView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
    }
    //set theme
    private void setTheme(int backgroundColor, ColorStateList textColor) {
        setThemeBackGround(backgroundColor);
        setThemeText(textColor);
    }

    private void setThemeBackGround(int backgroundColor) {
        mainLayout.setBackgroundColor(backgroundColor);
    }

    private void setThemeText(ColorStateList textColor) {
        txtTotalHide.setTextColor(textColor);
        txtHideRecently.setTextColor(textColor);
        btnChoose.setTextColor(textColor);
        btnCancelHideChosenImages.setTextColor(textColor);
    }

}