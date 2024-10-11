package com.example.project_album;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ViewPagerHideFragment extends Fragment {

    ArrayList<Image> images = new ArrayList<>();
    ViewPager2 mViewPager;
    TextView txtTimeDelete;
    TextView txtNumberPerAll;
    ImageView txtBack;
    TextView btnUnHide;
    MainActivity main;
    Context context = null;
    ViewPagerInTrashCanAdapter mAdapter;
    int index = 0;//Vị trí được chọn hiện tại
    RelativeLayout mainLayout;

    public ViewPagerHideFragment(ArrayList<Image> imgs, int index) {
        this.images = imgs;
        this.index = index;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager_hide, container, false);
        mainLayout=view.findViewById(R.id.main_view_hide_layout);
        txtTimeDelete = view.findViewById(R.id.edt_time_hide);
        txtBack=view.findViewById(R.id.txt_back_hide);
        txtNumberPerAll = view.findViewById(R.id.txt_number_per_all_hide);
        btnUnHide = view.findViewById(R.id.btn_unhide);
        mViewPager = view.findViewById(R.id.viewpage_in_hide);
        mAdapter = new ViewPagerInTrashCanAdapter(main.getSupportFragmentManager(), main.getLifecycle(), images);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(index, false);
        txtNumberPerAll.setText(String.valueOf(index + 1) + "/" + String.valueOf(images.size()));
        main.mBottomNavigationView.setVisibility(View.GONE);
        //set Theme
        setTheme(main.mainColorBackground,main.mainColorText);


        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                index = position;
                txtNumberPerAll.setText(String.valueOf(position + 1) + "/" + String.valueOf(images.size()));
            }
        });
        btnUnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                alertDialogBuilder.setMessage("Bạn có muốn bỏ ẩn ảnh?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Xóa dòng hình ảnh đó đó ở dataResource
                        long idImage = images.get(index).getId();
                        String key = images.get(index).getKey();
                        //set trạng thái đã hide là false ở data resource
                        MainActivity.dataResource.updateStateImageHideIsFalse(idImage,key);

                        // set trạng thái Hide là False ở images hiện hành
                        for (int j = 0; j < images.size(); j++) {
                            if (images.get(j).getId() == idImage) {
                                images.get(j).setHide("F");
                                break;
                            }
                        }
                        //Thêm ảnh vào lại AllLayout
                        //add ảnh vào lại allLayout đúng vị trí đã hide
                        if (idImage > AllLayout.images.get(AllLayout.images.size() - 1).getId()) {
                            AllLayout.images.add(images.get(index));
                        } else if (idImage < AllLayout.images.get(0).getId()) {
                            AllLayout.images.add(0, images.get(index));
                        } else {
                            for (int j = 1; j < AllLayout.images.size(); j++) {
                                if (AllLayout.images.get(j - 1).getId() < idImage
                                        && idImage < AllLayout.images.get(j).getId()) {
                                    AllLayout.images.add(j, images.get(index));
                                    break;
                                }
                            }
                        }
                        ////////////////////////
//                        Log.e("Day la lo1","Anh index co id "+String.valueOf(images.get(index).getId())+" Voi index"+String.valueOf(index));
                        if (index == images.size() - 1) {
                            images.remove(index);
                            index = images.size() - 1;
                        } else if (index == 0) {
                            images.remove(0);
                            index = 0;
                        } else {
                            images.remove(index);
                        }

                        //Adapter cũ thì nó sai, tuy nhiên tạo adapter mới thì nó lại đúng, chả biết tại sao
                        //Trong này có cả update images và notify
//                           mAdapter.updateImagesList(images);
                        if (images.size()==0){
                            txtBack.callOnClick();
                        }
                        mAdapter = new ViewPagerInTrashCanAdapter(main.getSupportFragmentManager(), main.getLifecycle(), images);
                        mViewPager.setAdapter(mAdapter);
                        mViewPager.setCurrentItem(index, false);
                        txtNumberPerAll.setText(String.valueOf(index + 1) + "/" + String.valueOf(images.size()));

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
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentmanager.beginTransaction();
                HideInAlbumLayoutFragment newHideFragment=new HideInAlbumLayoutFragment(images);
                ft.replace(R.id.replace_fragment_layout, newHideFragment);
                ft.commit();
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

    private void doSthWithOrientation(int newOrientation) {
        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {

        }
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
        txtTimeDelete.setTextColor(textColor);
        txtNumberPerAll.setTextColor(textColor);
        btnUnHide.setTextColor(textColor);
    }
}