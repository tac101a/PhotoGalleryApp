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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class TrashCanLayout extends Fragment {
    public ShowImageAdapter mGridAdapter;
    public static ArrayList<Image> images = new ArrayList<Image>();
    MainActivity main;
    Context context = null;
    TextView txtTotal;
    TextView txtDeleteRecently;
    RecyclerView mGridView;
    Button btnChoose;
    Button btnDeleteChosenImages;
    Button btnRestoreChosenImages;

    Bundle myOriginalMemoryBundle;
    LinearLayout lastLinear;
    LinearLayout mainLayout;

    public TrashCanLayout() {
        Log.e("TrashCanLayout", "constructor");
        // Required empty public constructor
    }

    public static TrashCanLayout newInstance(String strArg) {
        TrashCanLayout fragment = new TrashCanLayout();
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
//            if (images.size() == 0) {// cần if chỗ này để xử lí ấn từ all->trash->all->trash
//                // Đọc dữ liệu thì đã có bên Allayout đọc rồi
//                //Không cần đọc lại, chỉ cần lấy ra những biến là "T" thôi
//
////                for (int i = 0; i < AllLayout.images.size(); i++) {
////                    if (AllLayout.images.get(i).getDeleted().equals("T")) {
////                        images.add(AllLayout.images.get(i));
////                        Log.e("TrashCanLayout", String.valueOf(i));
////                    }
////                }
//            } else {
//                Log.e("Day la loi", "-------------ELSE------------");
//                for (int i = 0; i < images.size(); i++) {
//                    Log.e("Day la loi", "ID Anh=" + String.valueOf(images.get(i).getId()));
//                }
//
//            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e("DEBUG", "onCreateView of TrashCan");
        View mView = inflater.inflate(R.layout.fragment_trash_can_layout, container, false);
        mGridView = mView.findViewById(R.id.grid_view_trashcan);
        txtTotal = mView.findViewById(R.id.txt_display_total_picture_deleted);
        btnChoose = mView.findViewById(R.id.btn_choose);
        btnDeleteChosenImages = mView.findViewById(R.id.btn_delete_chosen_image);
        btnRestoreChosenImages = mView.findViewById(R.id.btn_restore_chosen_image);
        lastLinear = mView.findViewById(R.id.last_linear_in_trashcan);
        txtDeleteRecently = mView.findViewById(R.id.txt_delete_recently);
        DoSthWithOrientation(getResources().getConfiguration().orientation);
        mGridView = mView.findViewById(R.id.grid_view_trashcan);
        mainLayout = mView.findViewById(R.id.main_trash_layout);
        mGridAdapter = new ShowImageAdapter(main, R.layout.item_image, images, this);
        mGridView.setAdapter(mGridAdapter);
//        Toast.makeText(main, "helllo", Toast.LENGTH_SHORT).show();
        txtTotal.setText("Total: " + String.valueOf(images.size()));
        txtTotal.setVisibility(View.VISIBLE);
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
        return mView;
    }

    public void doBtnChooseWhenIsChoose() {

        txtDeleteRecently.setText("Chọn ảnh");
        lastLinear.setVisibility(View.VISIBLE);
        btnDeleteChosenImages.setVisibility(View.VISIBLE);
        btnRestoreChosenImages.setVisibility(View.VISIBLE);
        mGridAdapter.setChooseSelection(true);
        btnChoose.setText("Hủy");
        btnDeleteChosenImages.setText("Xóa tất cả");
        btnRestoreChosenImages.setText("Khôi phục tất cả");
        btnChoose.setBackgroundColor(getResources().getColor(R.color.green, context.getTheme()));
        main.mBottomNavigationView.setVisibility(View.GONE);
        //Chú ý chỗ này
        mGridAdapter.resetCount();
        btnDeleteChosenImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Hiển thị Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                alertDialogBuilder.setMessage("Bạn đã chắc chắn muốn xóa ảnh?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (btnDeleteChosenImages.getText().toString().equals("Xóa tất cả")) {
                            mGridAdapter.addAllIntoImageChosen();
                        }
                        for (int i = 0; i < mGridAdapter.chosenArrayImages.size(); i++) {
                            //Xóa dòng hình ảnh đó đó ở dataResource
                            MainActivity.dataResource.deleteImage(mGridAdapter.chosenArrayImages.get(i));
                            MainActivity.dataFirebase.deleteImage(
                            mGridAdapter.chosenArrayImages.get(i).getKey(),
                                    mGridAdapter.chosenArrayImages.get(i).getName()
                            );
                            long idImage = mGridAdapter.chosenArrayImages.get(i).getId();
                            // xóa ở images hiện hành
                            for (int i1 = 0; i1 < images.size(); i1++) {
                                if (images.get(i1).getId() == idImage) {
                                    images.remove(i1);
                                    break;
                                }
                            }
                            ///???? Có cần khúc này k
                            //Xóa ở images bên MainActivity
                            for (int i1 = 0; i1 < main.images.size(); i1++) {
                                if (main.images.get(i1).getId() == idImage) {
                                    main.images.remove(i1);
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

        btnRestoreChosenImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hiển thị Dialog
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                alertDialogBuilder.setMessage("Bạn có muốn khôi phục ảnh?");
                alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (btnRestoreChosenImages.getText().toString().equals("Khôi phục tất cả")) {
                            mGridAdapter.addAllIntoImageChosen();
                        }
                        for (int i = 0; i < mGridAdapter.chosenArrayImages.size(); i++) {
                            long idImage = mGridAdapter.chosenArrayImages.get(i).getId();
                            String key  = mGridAdapter.chosenArrayImages.get(i).getKey();
                            //set trạng thái đã xóa là false ở data resource
                            MainActivity.dataResource.updateStateImageDeletedIsFalse(idImage,key);

                            // set trạng thái delete là False
                            for (int j = 0; j < images.size(); j++) {
                                if (images.get(j).getId() == idImage) {
                                    images.get(j).setDeleted("F");
                                    break;
                                }
                            }

                            //add ảnh vào lại allLayout đúng vị trí đã xóa
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
                            // Xóa ảnh ở imgaes hiện hành là cũng như xóa ở adapter hiện hành r
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
        txtTotal.setText("Tổng " + String.valueOf(images.size()));
    }

    public void doBtnChooseWhenIsCancel() {
        txtDeleteRecently.setText("Xóa gần đây");
        lastLinear.setVisibility(View.GONE);
        btnDeleteChosenImages.setVisibility(View.GONE);
        btnRestoreChosenImages.setVisibility(View.GONE);
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
            mGridView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        }
    }

    public void update() {
        try {
            mGridAdapter.notifyDataSetChanged();
        } catch (Exception e) {

        }
    }

    //thay đổi tử ALlLayout sang
    public void updateTrashCan(Image img) {
        if (img.getDeleted().equals("T")) {
            images.add(img);
        } else {
            images.remove(img);
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
        txtTotal.setTextColor(textColor);
        txtDeleteRecently.setTextColor(textColor);
        btnChoose.setTextColor(textColor);
        btnDeleteChosenImages.setTextColor(textColor);
        btnRestoreChosenImages.setTextColor(textColor);
    }

}