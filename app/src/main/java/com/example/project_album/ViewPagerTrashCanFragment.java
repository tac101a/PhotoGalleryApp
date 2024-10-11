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

public class ViewPagerTrashCanFragment extends Fragment {

    ArrayList<Image> images = new ArrayList<>();
    ViewPager2 mViewPager;
    TextView txtTimeDelete;
    TextView txtNumberPerAll;
    ImageView txtBack;
    TextView btnDeleteImmediately, btnRestore;
    MainActivity main;
    Context context = null;
    ViewPagerInTrashCanAdapter mAdapter;
    int index = 0;//Vị trí được chọn hiện tại
    RelativeLayout mainLayout;

    public ViewPagerTrashCanFragment(ArrayList<Image> imgs, int index) {
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
        View view = inflater.inflate(R.layout.fragment_view_pager_trash_can, container, false);
        mainLayout=view.findViewById(R.id.main_view_trashcan_layout);
        txtTimeDelete = view.findViewById(R.id.edt_time_delete);
        txtBack=view.findViewById(R.id.txt_back);
        txtNumberPerAll = view.findViewById(R.id.txt_number_per_all);
        btnDeleteImmediately = view.findViewById(R.id.btn_delete_immediately);
        btnRestore = view.findViewById(R.id.btn_restore);
        mViewPager = view.findViewById(R.id.viewpage_in_trashcan);
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
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                alertDialogBuilder.setMessage("Bạn có muốn khôi phục ảnh?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long idImage = images.get(index).getId();
                        String key = images.get(index).getKey();
                        //set trạng thái đã xóa là false ở data resource
                        MainActivity.dataResource.updateStateImageDeletedIsFalse(idImage,key);
                        //set trạng thái ảnh delete là F
                        images.get(index).setDeleted("F");

                        //add ảnh vào lại allLayout đúng vị trí đã xóa
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

                        //Xóa ảnh ở image hiện hành
                        if (index == images.size() - 1) {
                            images.remove(index);
                            index = images.size() - 1;
                        } else if (index == 0) {
                            images.remove(0);
                            index = 0;
                        } else {
                            images.remove(index);
                        }
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
        btnDeleteImmediately.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                alertDialogBuilder.setMessage("Bạn có muốn xóa hoàn toàn ảnh?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Xóa dòng hình ảnh đó đó ở dataResource
                        MainActivity.dataResource.deleteImage(images.get(index));
                        MainActivity.dataFirebase.deleteImage(images.get(index).getKey(),
                                images.get(index).getName());
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
                ft.replace(R.id.replace_fragment_layout, main.trashCanLayout);
                ft.commit();
                main.mBottomNavigationView.setVisibility(View.VISIBLE);
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
        btnDeleteImmediately.setTextColor(textColor);
        btnRestore.setTextColor(textColor);
    }
}