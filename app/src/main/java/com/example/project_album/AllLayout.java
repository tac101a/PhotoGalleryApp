package com.example.project_album;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.text.TextRecognizer;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;


public class AllLayout extends Fragment {

    // TODO: Rename and change types of parameters
    private final int STORAGE_PERMISSION_REQUEST_CODE = 1001;
    //khai báo biến cho dialog============================
    private Dialog dialog;
    private TextView tv_addtoAlbum;
    private TextView tv_delete;
    private TextView tv_favorite;
    private TextView tv_blind;
    private TextView tv_slider;
    private TextView tv_zip;
    private TextView tv_share_multiple;
    private View v_allInfo;
    private boolean clicked = false;

    //view header dialog=============================
    private TextView tv_theme;
    private TextView tv_size_square_small;
    private TextView tv_size_square_big;
    private TextView tv_type_square;
    private TextView tv_unzip;
    private View v_dialog_h;
    private View v_text_color;
    private View v_bg_color;
    private TextView tv_text_color;
    private TextView tv_deleteCoppy;
    private TextView tv_sort;
    Dialog dialog_header;
    //finish=======================================

    //finish

    //view header ==================================
    private TextView tv_info, tv_choose;
    private Button btn_extend;
    private FloatingActionButton btn_search;
    //finish

    //view footer===================================
    private ImageView img_all_info;
    private TextView tv_numSelect;
    //finish
    private TextView tv_bottom;
    MainActivity main;
    Context context = null;

    //khai báo của TA ======================================

    ActivityResultLauncher<PickVisualMediaRequest> pickMultiImages;
    FloatingActionButton btnAdd, btnAddCamera, btnAddUrl, btnAddImage;
    private Animation rotateOpen;
    private Animation rotateClose;
    private Animation fromBottom;
    private Animation toBottom;
    public static ArrayList<Image> images = new ArrayList<>();
    private UrlFragment fragmenturl;
    //    ImageAdapter adapter;
    //GridView gridView;
    ShowImageInAllAdapter adapter;
    RecyclerView recyclerView;
    Spinner spinner;
    String sort = "Sắp xếp theo kích cỡ";
    private RelativeLayout rl_background;
    ArrayAdapter<String> spinnerAdapter;
    private static final int REQUEST_CAMERA_PERMISSION_CODE = 123;
    private ArrayList<Image> copyImages = new ArrayList<>();

    //Zip
    Button btnOkZip, btnCancelZip;
    TextView txtTitleZip, txtDetailZip;
    EditText edtNameZip;

    //Xong zip
    //Password for hide
    private String myFilePasswordForHide = "user";
    String key = "hidepass";
    String pass = "";//này là lúc đọc lên
    String value = "";// này là lúc lưu xuống

    public AllLayout() {
        debug("constructor, images.count = " + String.valueOf(images.size()));
        //images = MainActivity.dataResource.getAllImage();
    }

    public static AllLayout newInstance(String strArg) {
        AllLayout fragment = new AllLayout();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        debug("onCreate, images.size = " + String.valueOf(MainActivity.images.size()));
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            //initDataResource();

        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
        debug("finish onCreate");
    }

    @Override
    public void onResume() {
        super.onResume();
        debug("onResume" + String.valueOf(images.size()));
        for (int j = 0; j < images.size(); j++){
            debug(images.get(j).getDate());
        }
        main.albumLayout.InitAlbums();
        main.albumLayout.isInit = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        debug("onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_layout, container, false);

        // view for all info dialog
        v_allInfo = main.getLayoutInflater().inflate(R.layout.all_info_alllayout, null);
        tv_addtoAlbum = v_allInfo.findViewById(R.id.add_to_album);
        tv_delete = v_allInfo.findViewById(R.id.delete);
        tv_favorite = v_allInfo.findViewById(R.id.favorite);
        tv_slider = v_allInfo.findViewById(R.id.slider);
        tv_blind = v_allInfo.findViewById(R.id.blind);
        tv_zip = v_allInfo.findViewById(R.id.zip);
        tv_share_multiple = v_allInfo.findViewById(R.id.share_multiple);
        dialog = new Dialog(main);
        dialog.setContentView(v_allInfo);
        EventViewDialog();
        // finish view for all info dialog======================================

        //view header
        tv_info = view.findViewById(R.id.tv_info);
        tv_choose = view.findViewById(R.id.tv_choose);
        btn_extend = view.findViewById(R.id.btn_many);
        btn_search = view.findViewById(R.id.search_button);
        btn_search.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(main, R.color.blue)));

        EventViewHeader();
        //finish===================================================
        //View for header dialog
        v_dialog_h = main.getLayoutInflater().inflate(R.layout.header_dialog, null);
        tv_theme = v_dialog_h.findViewById(R.id.tv_theme);
        tv_size_square_small = v_dialog_h.findViewById(R.id.tv_size_small);
        tv_size_square_big = v_dialog_h.findViewById(R.id.tv_size_big);
        tv_type_square = v_dialog_h.findViewById(R.id.tv_type_square);
        tv_unzip = v_dialog_h.findViewById(R.id.tv_unzip);
        tv_deleteCoppy =v_dialog_h.findViewById(R.id.tv_delete_coppy);
        tv_sort = v_dialog_h.findViewById(R.id.tv_sort);
        dialog_header = new Dialog(main);
        dialog_header.setContentView(v_dialog_h);
        v_text_color = v_dialog_h.findViewById(R.id.v_text_color);
        v_bg_color = v_dialog_h.findViewById(R.id.v_bg_color);
        tv_text_color = v_dialog_h.findViewById(R.id.tv_text_color);
        EventViewDialogHeader();

        //view footer
        img_all_info = main.bottom_navigation_album.findViewById(R.id.img_all_info);
        tv_numSelect = main.bottom_navigation_album.findViewById(R.id.tv1);
        EventViewFooter();
        //finish====================================================

        //view TA ==============================================
        rotateOpen = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getActivity(), R.anim.to_bottom_anim);

        btnAdd = view.findViewById(R.id.btn_add);
        btnAddCamera = view.findViewById(R.id.btn_add_Camera);
        btnAddCamera.setVisibility(View.GONE);
        btnAddUrl = view.findViewById(R.id.btn_add_Url);
        btnAddImage = view.findViewById(R.id.btn_add_Images);
        EventViewAddPicture();


        //finish=====================================================
        rl_background = view.findViewById(R.id.rl_background);
        tv_bottom = view.findViewById(R.id.tv_bottom);
        tv_bottom.setText(String.valueOf(images.size()) + " ảnh");
        adapter = new ShowImageInAllAdapter(main, R.layout.item_image, images);
        recyclerView = view.findViewById(R.id.rv_image_in_all);
        DoSthWithOrientation(getResources().getConfiguration().orientation);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(images.size() - 1);


        setTheme(main.mainColorBackground, main.mainColorText);
        //Khởi tạo cho SharePrefence lần đầu cho cái Hide bên Album tức là nếu
        //main.hidepass lần đầu chạy là null thì ghi vào file user pass là ""
        //còn nếu đọc từ firebase có rồi thì thôi không cần ghi đè xuống
        if (main.hidepass.equals("")) {
            Log.e("MainHidePass","Lanull");
            SharedPreferences myPrefContainer = main.getSharedPreferences(
                    myFilePasswordForHide, Activity.MODE_PRIVATE);
            SharedPreferences.Editor myPrefEditor = myPrefContainer.edit();
            myPrefEditor.putString(key, "");
            main.dataFirebase.setHidePassIntoFirebase("");
            main.hidepass="";
            myPrefEditor.commit();
        }
        for (int j = 0; j < images.size(); j++){
            debug(images.get(j).getDate());
        }

        //Kết thúc
        return view;
    }

    public void setTextInfo(String info) {
        tv_info.setText(info);
    }

    private void EventViewDialogHeader() {
        if (main.mainColorBackground == main.getColor(R.color.black)) {
            tv_theme.setText("Chế độ sáng");
        } else {
            tv_theme.setText("Chế độ tối");
        }
        tv_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTheme();
            }
        });
        tv_size_square_small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSizeSquareSmall();
            }
        });
        tv_size_square_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateSizeSquareBig();
            }
        });
        tv_type_square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTypeSquare();
            }
        });
        tv_unzip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unzip();
            }
        });
        v_text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeTextColor();
            }
        });
        tv_text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeTextColor();
            }
        });
        v_bg_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChangeColorBackground();
            }
        });
        tv_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortAction();
            }
        });
        tv_deleteCoppy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCopy();
            }
        });
    }

    //=======================bắt các sự kiện từ dialog ===================
    private void EventViewDialog() {
        tv_slider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentmanager.beginTransaction();
                Fragment fragment = new ViewPagerAllLayoutFragment(adapter.image_chosen);
                ft.add(R.id.replace_fragment_layout, fragment);
                ft.addToBackStack(fragment.getClass().getSimpleName());
                ft.commit();
                dialog.dismiss();
                adapter.resetChooseSelection();
                tv_choose.callOnClick();
            }
        });

        tv_zip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kiểm tra quyền truy cập lưu trữ - kiểm tra cả read va write lên external mặc dù ở đây chỉ cần
                //read còn unzip mới cần cần đọc nhưng thôi kệ làm luôn lần
                boolean writePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
                boolean readPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;

                if (!writePermission || !readPermission) {
                    // Yêu cầu cấp quyền WRITE và READ_EXTERNAL_STORAGE nếu chưa được cấp
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(requireActivity(), permissions, STORAGE_PERMISSION_REQUEST_CODE);
                } else {
                    // Quyền đã được cấp, thực hiện các hành động cần thiết ở đây
                    for (int i = 0; i < images.size(); i++) {
                        Log.e("pathImage", "Path=" + images.get(i).getPath());
                    }
                    ArrayList<String> folder = new ArrayList<>();
                    for (int i = 0; i < adapter.image_chosen.size(); i++) {
                        folder.add(adapter.image_chosen.get(i).getPath());
                    }
                    doZipImage(folder);
                    dialog.dismiss();
                    adapter.resetChooseSelection();
                    tv_choose.callOnClick();
//                    Toast.makeText(main, "Tien hanh tao thu muc", Toast.LENGTH_SHORT).show();

                }


            }
        });
        tv_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < adapter.image_chosen.size(); i++) {
                    if (adapter.image_chosen.get(i).getFavorite().equals("F")) {
                        adapter.image_chosen.get(i).setFavorite("T");
                        main.favoriteLayout.updateFavorite(adapter.image_chosen.get(i));
                        //main.albumLayout.updateFavorite(adapter.image_chosen.get(i));
                        MainActivity.dataResource.likeImage(adapter.image_chosen
                                .get(i).getId(), adapter.image_chosen.get(i).getKey());
                    }
                }
                dialog.dismiss();
                adapter.resetChooseSelection();
                tv_choose.callOnClick();
            }
        });
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < adapter.image_chosen.size(); i++) {

                    //Nếu chọn xóa ảnh thì bắt buộc ảnh đó la unlike
                    if (adapter.image_chosen.get(i).getFavorite().equals("T")) {
                        adapter.image_chosen.get(i).setFavorite("F");
                        main.favoriteLayout.updateFavorite(adapter.image_chosen.get(i));
                        //Unlike ở Album
                        //main.albumLayout.updateFavorite(adapter.image_chosen.get(i));
                    }
                    //thay đổi tính chất ảnh đã được xóa, là như này nó đã thay đổi bên main luôn rồi.
                    adapter.image_chosen.get(i).setDeleted("T");

                    //add vào images ở trashcan
                    main.trashCanLayout.updateTrashCan(adapter.image_chosen.get(i));



                    //xoa image o allLayout
                    long idImage = adapter.image_chosen.get(i).getId();
                    String key = adapter.image_chosen.get(i).getKey();
                    for (int i1 = 0; i1 < images.size(); i1++) {
                        if (images.get(i1).getId() == idImage) {
                            images.remove(i1);
                            break;
                        }
                    }

//                    //xoa image ở adapter
                    adapter.updateImagesInShowImageAllAdapter(idImage);
//                    Log.e("DeleteAllLayout","-----------------------------------------");
//                    Log.e("DeleteAllLayout","Images size="+String.valueOf(images.size()));
//                    Log.e("DeleteAllLayout","Adapter Size="+String.valueOf(adapter.images.size()));
                    //update trạng thái ở database
                    MainActivity.dataResource.updateStateImageDeletedIsTrue(idImage, key);
                    MainActivity.dataResource.deleteImageInAlbum(adapter.image_chosen.get(i));
                }
                main.albumLayout.updateAlbumFirebase();
                adapter.notifyDataSetChanged();
                dialog.dismiss();
                adapter.resetChooseSelection();
                tv_choose.callOnClick();
                tv_bottom.setText(String.valueOf(images.size()) + " ảnh");
            }
        });
        tv_blind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Đọc dữ liệu từ file myFilePasswordForHide
                SharedPreferences myPrefContainer = main.getSharedPreferences(myFilePasswordForHide, Activity.MODE_PRIVATE);

                if ((myPrefContainer != null) && myPrefContainer.contains(key)) {
                    pass = myPrefContainer.getString(key, "");
                }
                //Nếu pass=="" tức là chưa đặt mật khẩu thì bắt đặt mật khẩu
                if (pass.equals("")) {
                    Dialog myDialog = new Dialog(main);
                    myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    myDialog.setContentView(R.layout.custom_add_password_for_hide);
                    Window window = myDialog.getWindow();
                    if(window == null){
                        return;
                    }
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams attribute = window.getAttributes();
                    attribute.gravity =Gravity.CENTER;
                    EditText passForHide = myDialog.findViewById(R.id.edt_pass_hide_in_all_layout);
                    Button btnCancelSetPassHide = myDialog.findViewById(R.id.btn_cancel_set_password_hide);
                    Button btnSaveSetPassHide = myDialog.findViewById(R.id.btn_save_password_hide);
                    btnCancelSetPassHide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            myDialog.dismiss();
                        }
                    });
                    btnSaveSetPassHide.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Tiến hành lưu lại vào file myFilePasswordForHide
                            if (passForHide.getText().toString().equals("")) {
                                Toast.makeText(main, "Nhập mật khẩu", Toast.LENGTH_SHORT).show();
                            } else {
                                value = passForHide.getText().toString();
                                SharedPreferences.Editor myPrefEditor = myPrefContainer.edit();
                                myPrefEditor.putString(key, value);
                                //cập nhật pass lên firebase
                                main.dataFirebase.setHidePassIntoFirebase(value);
                                main.hidepass=value;
                                myPrefEditor.commit();
                            }
                            myDialog.dismiss();
                        }
                    });
                    myDialog.show();
                } else {
                    for (int i = 0; i < adapter.image_chosen.size(); i++) {

                        //Nếu chọn ẩn ảnh thì bắt buộc phải xóa ảnh đó khỏi favorite, mặc dù nó vẫn là ưa thích
                        if (adapter.image_chosen.get(i).getFavorite().equals("T")) {
                            //chỉ xóa khỏi imagesFavorite thôi, chứ k xóa tính chất ưa thích của nó
                            main.favoriteLayout.removeImageOutFavorite(adapter.image_chosen.get(i));
                        }
                        //thay đổi tính chất ảnh đã được ẩn, là như này nó đã thay đổi bên main luôn rồi.
                        adapter.image_chosen.get(i).setHide("T");

                        //add vào images ở hide
                        HideInAlbumLayoutFragment.images.add(adapter.image_chosen.get(i));

                        //xoa image o allLayout
                        long idImage = adapter.image_chosen.get(i).getId();
                        String key = adapter.image_chosen.get(i).getKey();
                        for (int i1 = 0; i1 < images.size(); i1++) {
                            if (images.get(i1).getId() == idImage) {
                                images.remove(i1);
                                break;
                            }
                        }

//                    //xoa image ở adapter, do AllLayout chưa đồng bộ nên phải xóa riêng
                        adapter.updateImagesInShowImageAllAdapter(idImage);

                        //update trạng thái ở database
                        MainActivity.dataResource.updateStateImageHideIsTrue(idImage, key);
                        MainActivity.dataResource.deleteImageInAlbum(adapter.image_chosen.get(i));
                    }
                    main.albumLayout.updateAlbumFirebase();
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    adapter.resetChooseSelection();
                    tv_choose.callOnClick();
                    Log.e("Hide", "Size sau ẩn=" + String.valueOf(images.size()));
                }
                tv_bottom.setText(String.valueOf(images.size()) + " ảnh");
            }
        });

        tv_addtoAlbum.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.dismiss();
            addToAlbumAction();
        }
        });

        tv_share_multiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Uri> uris = new ArrayList<>();
                for (int i = 0; i < adapter.image_chosen.size(); i++) {
                    uris.add(saveImage(adapter.image_chosen.get(i).getImgBitmap(), i));
                }
                shareImageUri(uris);
            }
        });

    }
    private void addToAlbumAction(){
        FragmentManager fragmentmanager = main.getSupportFragmentManager();
        FragmentTransaction ft = fragmentmanager.beginTransaction();
        Fragment fragment = new MyAlbumFragment(this);
        ft.add(R.id.replace_fragment_layout,fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }
    public void addToAlbum(long idAlbum){
        main.addImAb = false;
        ArrayList<Image> imgs = new ArrayList<>();
        imgs.addAll(adapter.image_chosen);
        for (int j = 1; j < AlbumLayout.albums.size(); j++) {
            if (AlbumLayout.albums.get(j).getId() == idAlbum) {
                for (int i = 0; i < imgs.size(); i++) {
                    MainActivity.dataResource.InsertAlbumImage(idAlbum, imgs.get(i).getId());
                    AlbumLayout.albums.get(j).addImage(imgs.get(i));
                }
                MainActivity.dataFirebase.updateAlbum(AlbumLayout.albums.get(j));
                break;
            }
        }
        adapter.notifyDataSetChanged();
        adapter.resetChooseSelection();
        tv_choose.callOnClick();
    }

    //========================kết thúc bắt các sự kiện từ dialog

    //bắt sự kiện tự view header
    private void EventViewHeader() {
        tv_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tv_choose.getText().equals("Chọn")) {
                    tv_choose.setTranslationX(100);
                    btnAddCamera.setVisibility(View.INVISIBLE);
                    tv_choose.setText("Hủy");
                    adapter.setChooseSelection(true);
                    btn_extend.setVisibility(View.INVISIBLE);
                    main.mBottomNavigationView.setVisibility(View.INVISIBLE);
                    main.bottom_navigation_album.setVisibility(View.VISIBLE);
                } else {
                    tv_choose.setTranslationX(0);
                    btnAddCamera.setVisibility(View.VISIBLE);
                    adapter.setChooseSelection(false);
                    adapter.resetChooseSelection();
                    adapter.notifyDataSetChanged();
                    main.bottom_navigation_album.setVisibility(View.INVISIBLE);
                    main.mBottomNavigationView.setVisibility(View.VISIBLE);
                    btn_extend.setVisibility(View.VISIBLE);
                    tv_choose.setText("Chọn");

                }
            }
        });
        btn_extend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Chỗ này đang thử cho unzip sau Huy thêm dialog mới dô sau đó đổi là được
                Window window = dialog_header.getWindow();
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams attribute = window.getAttributes();
                attribute.y = getResources().getDisplayMetrics().heightPixels - 40;
                attribute.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                dialog_header.show();
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
                Fragment fragment = new searchFragment();
                ft.add(R.id.replace_fragment_layout,fragment);
                ft.addToBackStack(fragment.getClass().getSimpleName());
                ft.commit();
            }
        });
    }

    //kết thúc bắt sự kiện từ view header ===============================

    //bắt sự kiện từ view footer
    private void EventViewFooter() {
        img_all_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.image_chosen.size() > 0) {
                    Window window = dialog.getWindow();
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    WindowManager.LayoutParams attribute = window.getAttributes();
                    attribute.y = 280;
                    attribute.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                    dialog.show();
                } else {
                    Toast.makeText(main, "Bạn chưa chọn ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //kết thúc bắt sự kiện từ view footer=============================

    //bắt sự kiện view addPicture của TA
    private void EventViewAddPicture() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonClicked();
            }
        });
        btnAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(main, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(main, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION_CODE);
                    return;
                }
                startCamera();
            }
        });
        btnAddUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                main.isURLdownload = true;
                fragmenturl = new UrlFragment();
                fragmenturl.show(main.getSupportFragmentManager(), UrlFragment.Tag);
            }
        });
        registerImageResultLauncher();
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickMultiImages.launch(new PickVisualMediaRequest.Builder().setMediaType
                        (ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
            }
        });
    }

    private void registerImageResultLauncher() {
        pickMultiImages = registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(99),
                new ActivityResultCallback<List<Uri>>() {
                    @Override
                    public void onActivityResult(List<Uri> result) {
                        if (!result.isEmpty()) {
                            try {
                                main.dataResource.open();
                                for (int i = 0; i < result.size(); i++) {
                                    Bitmap image = MediaStore.Images.Media.getBitmap(main.getContentResolver(), result.get(i));
                                    Image img = new Image(image, main.GenerateName());
                                    MainActivity.dataFirebase.insertImage(img);
//                                    img.setId(main.dataResource.InsertImage(img));
//                                    AllLayout.images.add(img);
//                                    MainActivity.images.add(img);
//                                    main.allLayout.adapter.insert(img);
//                                    main.allLayout.update();
                                }
                                Log.d("PhotoPicker", "Number of items selected: " + result.size());
                            } catch (Exception e) {

                            }
                        } else {
                            Log.d("PhotoPicker", "No media selected");
                        }
                    }
                });
    }


    public void startCamera() {
        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Đặt 123 là requestCode cho chụp hình
        startActivityForResult(camera_intent, 123);
        //getActivity().startActivityFromFragment(AllLayout.this, camera_intent, 123);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền truy cập lưu trữ đã được cấp, thực hiện các hành động cần thiết ở đây
                Toast.makeText(main, "Quyền truy cập đc cấp", Toast.LENGTH_SHORT).show();
            } else {
                // Quyền truy cập lưu trữ bị từ chối, bạn có thể thông báo cho người dùng về việc này
                Toast.makeText(main, "Quyền truy cập k đc cấp", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            MainActivity.dataResource.open();
            if (requestCode == 123) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                Image img = new Image(bitmap, main.GenerateName());
                MainActivity.dataFirebase.insertImage(img);
                //TH1: Chuyển thành byte để lưu vào database
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
//                byte[] byteArray = stream.toByteArray();

                //TH2: Lưu vào gallery
//                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH);
//                String filename = sdf.format(new Date());
//                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//                Log.i("Test", path.toString());
//                OutputStream fOut = null;
//                File file = new File(path, filename + ".jpg");
//
//                try {
//                    fOut = new FileOutputStream(file);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fOut);
//                    fOut.flush();
//                    fOut.close();
//
//                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                    mediaScanIntent.setData(Uri.fromFile(file));
//                    main.sendBroadcast(mediaScanIntent);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = newConfig.orientation;
        DoSthWithOrientation(newOrientation);
        adapter.notifyDataSetChanged();
    }

    private void DoSthWithOrientation(int newOrientation) {
        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), main.NUMCOLUMN * 2));
        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), main.NUMCOLUMN));
        }
        //Toast.makeText(getContext(),"oke",Toast.LENGTH_SHORT).show();
    }

    private void onAddButtonClicked() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }

    private void setVisibility(boolean clicked) {
        if (!clicked) {
            btnAddCamera.setVisibility(View.VISIBLE);
            btnAddUrl.setVisibility(View.VISIBLE);
            btnAddImage.setVisibility(View.VISIBLE);
        } else {
            btnAddCamera.setVisibility(View.GONE);
            btnAddUrl.setVisibility(View.GONE);
            btnAddImage.setVisibility(View.GONE);
        }
    }

    private void setAnimation(boolean clicked) {
        if (!clicked) {
            btnAdd.setAnimation(rotateOpen);
            btnAddCamera.setAnimation(fromBottom);
            btnAddUrl.setAnimation(fromBottom);
            btnAddImage.setAnimation(fromBottom);
            btnAddCamera.setEnabled(true);
            btnAddUrl.setEnabled(true);
            btnAddImage.setEnabled(true);
        } else {
            btnAdd.setAnimation(rotateClose);
            btnAddCamera.setAnimation(toBottom);
            btnAddUrl.setAnimation(toBottom);
            btnAddImage.setAnimation(toBottom);
            btnAddCamera.setEnabled(false);
            btnAddUrl.setEnabled(false);
            btnAddImage.setEnabled(false);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    public void debug1() {
        for (int i = 0; i < images.size(); i++) {
            debug(String.valueOf(images.get(i).getId()));
        }
    }

    public void showBigScreen(int position) {
        FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentmanager.beginTransaction();
        ViewPagerAllLayoutFragment newFragment = new ViewPagerAllLayoutFragment(images, position);
        ft.replace(R.id.replace_fragment_layout, newFragment);
        ft.commit();

    }

    private void debug(String str) {
        Log.e("AllLayout", str);
    }

    private Bitmap getImageFromPath(String path) {
        Bitmap b = null;
        debug(path);
        try {
            File f = new File(path);
            b = BitmapFactory.decodeStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        return b;
    }

    //=================== Quá trình zip =======================
    // list ds đã zip bên trong thư mục ImagesZip
    public ArrayList<String> getListOfFilesInDirectory(String directoryPath) {
        ArrayList<String> fileList = new ArrayList<>();
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileList.add(file.getName());
                    }
                }
            }
        } else {
            Log.e("Loi", "Thư mục không tồn tại hoặc không phải là thư mục.");
        }

        return fileList;
    }

    public void doZipImage(ArrayList<String> folder) {
        Dialog mDialog = new Dialog(main);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_zip);
        txtTitleZip = mDialog.findViewById(R.id.txt_title_zip);
        txtDetailZip = mDialog.findViewById(R.id.txt_detail_zip);
        edtNameZip = mDialog.findViewById(R.id.edt_name_zip);
        btnOkZip = mDialog.findViewById(R.id.btn_ok_zip);
        btnCancelZip = mDialog.findViewById(R.id.btn_cancel_zip);
        btnCancelZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        btnOkZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtNameZip.getText().toString().length() == 0) {
                    Toast.makeText(main, "Nhập tên file zip", Toast.LENGTH_SHORT).show();
                } else {
                    //Lấy đường dẫn thử mục Download
                    String folderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                    File folderZip = new File(folderPath);
                    if (!folderZip.exists()) {
                        folderZip.mkdirs(); // Tạo thư mục "Download" nếu chưa tồn tại
                    }
                    //Tạo thư mục MyImagesZip owe Download để chứa ảnh đã zip
                    String newFolderName = "MyImageZips";
                    File newFolder = new File(folderZip, newFolderName);
                    if (!newFolder.exists()) {
                        if (newFolder.mkdir()) { // Tạo thư mục mới "MyImageZips"
                            Log.e("FolderCreation", "Có thể tạo thư mục MyImageZips");
                        } else {
                            // Không thể tạo thư mục mới
                            Log.e("FolderCreation", "Không thể tạo thư mục MyImageZips");
                        }
                    } else {
                        // Thư mục đã tồn tại
                        Log.e("FolderCreation", "Thư mục MyImageZips đã tồn tại trước đó");
                    }

                    ArrayList<String> fileList = getListOfFilesInDirectory(newFolder.getPath());
                    if (fileList.contains(edtNameZip.getText().toString())) {
                        Toast.makeText(main, "Ten file da ton tai trong MyImageZips", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Lâ đường dẫn tên file cần zip
//                    Log.e("Loi", "Loi" + dir.getPath() + "   " + dir.getName());
                    String fileNameZip = newFolder.getPath() + "/" + edtNameZip.getText().toString();
                    try {
                        zip(folder, fileNameZip);
                        Toast.makeText(main, "Zip thành công", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    } catch (IOException e) {
                        Toast.makeText(main, "Zip lỗi", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        mDialog.show();
    }

    public void zip(ArrayList<String> folder, String fileNameZip) throws IOException {
        BufferedInputStream origin = null;
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(fileNameZip)));
        try {
            int BUFFER_SIZE = 1024;//đơn vị là byte// mỗi lần nó ghi là 4096 byte vào
            byte data[] = new byte[BUFFER_SIZE];
            for (int i = 0; i < folder.size(); i++) {
                FileInputStream fi = new FileInputStream(folder.get(i));
                origin = new BufferedInputStream(fi, BUFFER_SIZE);
                try {
                    ZipEntry entry = new ZipEntry(folder.get(i).substring(folder.get(i).lastIndexOf("/") + 1));
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
                        out.write(data, 0, count);
                    }
                } finally {
                    origin.close();
                }
            }
        } finally {
            out.close();
        }
    }

    //===================Kết thúc zip=======================


    //=================== Quá trình unzip =======================

    private void unzip() {
        boolean writePermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
        boolean readPermission = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        if (!writePermission || !readPermission) {
            // Yêu cầu cấp quyền WRITE và READ_EXTERNAL_STORAGE nếu chưa được cấp
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(requireActivity(), permissions, STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            //Nếu cấp phát rồi thì unzip thôi
            doUnzipImage();
        }
    }

    public void doUnzipImage() {
        Dialog mDialog = new Dialog(main);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_zip);
        txtTitleZip = mDialog.findViewById(R.id.txt_title_zip);
        txtDetailZip = mDialog.findViewById(R.id.txt_detail_zip);
        edtNameZip = mDialog.findViewById(R.id.edt_name_zip);
        btnOkZip = mDialog.findViewById(R.id.btn_ok_zip);
        btnCancelZip = mDialog.findViewById(R.id.btn_cancel_zip);

        txtTitleZip.setText("Unzip");
        txtDetailZip.setText("Sau khi unzip tệp sẽ hiển thị ở theo đường dẫn \n Downloads/MyImageUnZips/TênFileZip");
        edtNameZip.setHint("Nhập tên file đã zip trong thư mục Downloads/MyImageZips");
        btnCancelZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        btnOkZip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtNameZip.getText().toString().length() == 0) {
                    Toast.makeText(main, "Nhập tên file cần unzip", Toast.LENGTH_SHORT).show();
                } else {
                    //Lấy đường dẫn Zip truyền vào để UnZip
                    String pathDownload = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS).toString();
                    String parentDirPath1 = pathDownload + "/MyImageZips";
                    //Kiểm tra tên file zip có hợp lệ không
                    String zipFile = parentDirPath1 + "/" + edtNameZip.getText().toString();
                    ArrayList<String> fileList = getListOfFilesInDirectory(parentDirPath1);
                    if (!fileList.contains(edtNameZip.getText().toString())) {
                        Toast.makeText(main, "Không tồn tại file đã zip", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Tạo thư mục MyImagesUnZips
                    String newFolderName = "MyImageUnZips";
                    File newFolder = new File(pathDownload, newFolderName);
                    if (!newFolder.exists()) {
                        if (newFolder.mkdir()) { // Tạo thư mục mới "MyImageZips"
                            Log.e("FolderCreation", "Có thể tạo thư mục MyImageZips");
                        } else {
                            // Không thể tạo thư mục mới
                            Log.e("FolderCreation", "Không thể tạo thư mục MyImageZips");
                        }
                    } else {
                        // Thư mục đã tồn tại
                        Log.d("FolderCreation", "Thư mục MyImageZips đã tồn tại trước đó");
                    }
                    //Tạo thư mục chứa ảnh UnZip
                    File dirChildNameZip = new File(newFolder, edtNameZip.getText().toString());
                    if (!dirChildNameZip.exists()) {
                        dirChildNameZip.mkdir();
                    }

                    try {
                        unzip(zipFile, dirChildNameZip.getPath());
                        Toast.makeText(main, "UnZip thành công", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    } catch (IOException e) {
                        Toast.makeText(main, "UnZip Lỗi", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                        throw new RuntimeException(e);
                    }

                }
            }
        });
        mDialog.show();
    }

    public void unzip(String zipFile, String location) throws IOException {
        int size;
        byte[] buffer = new byte[1024];

        try {
            if (!location.endsWith("/")) {
                location += "/";
            }
            File f = new File(location);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze;//này kiểu từng hình ảnh trong file zip

            while ((ze = zin.getNextEntry()) != null) {
                if (!ze.isDirectory()) {
                    String fileName = ze.getName();
                    File newFile = new File(location + fileName);
                    FileOutputStream fout = new FileOutputStream(newFile);

                    while ((size = zin.read(buffer, 0, buffer.length)) != -1) {
                        fout.write(buffer, 0, size);
                    }
                    zin.closeEntry();
                    fout.close();
                }
            }
            zin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=======================Kết thúc unzip
    public void update() {
        try {
            recyclerView.scrollToPosition(images.size() - 1);
            adapter.notifyDataSetChanged();
            tv_bottom.setText(String.valueOf(images.size()) + " ảnh\nĐã đồng bộ với cloud");
            debug("update");
        } catch (Exception e) {
            debug(e.toString());
        }
    }

    private void updateTheme() {
        if (tv_theme.getText().toString().equals("Chế độ sáng")) {
            tv_theme.setText("Chế độ tối");
            main.mainColorBackground = main.getColor(R.color.light);
            main.mainColorText = main.getColorStateList(R.color.textview_form2);

        } else {
            tv_theme.setText("Chế độ sáng");
            main.mainColorBackground = main.getColor(R.color.black);
            main.mainColorText = main.getColorStateList(R.color.textview_form);
        }
        setTheme(main.mainColorBackground, main.mainColorText);
    }

    private void setTheme(int backgroundColor, ColorStateList textColor) {
        setThemeBackGround(backgroundColor);
        setThemeText(textColor);
    }

    private void setThemeBackGround(int backgroundColor) {
        //LinearLayout dialog_header = v_dialog_h.findViewById(R.id.linear);
        LinearLayout dialog_all = v_allInfo.findViewById(R.id.linear);

        //mau background va dialog
        rl_background.setBackgroundColor(backgroundColor);
        //dialog_header.setBackgroundColor(backgroundColor);
        dialog_all.setBackgroundColor(backgroundColor);

        v_bg_color.setBackgroundColor(backgroundColor);
    }

    private void setThemeText(ColorStateList textColor) {
        //Textview dialog footer======================
        tv_addtoAlbum.setTextColor(textColor);
        tv_favorite.setTextColor(textColor);
        tv_blind.setTextColor(textColor);
        tv_slider.setTextColor(textColor);
        tv_zip.setTextColor(textColor);
        tv_share_multiple.setTextColor(textColor);
        tv_delete.setTextColor(textColor);

        //view header dialog=========================
        tv_theme.setTextColor(textColor);
        tv_size_square_small.setTextColor(textColor);
        tv_size_square_big.setTextColor(textColor);
        tv_type_square.setTextColor(textColor);
        tv_unzip.setTextColor(textColor);
        tv_text_color.setTextColor(textColor);
        v_text_color.setBackgroundColor(textColor.getDefaultColor());
        tv_sort.setTextColor(textColor);
        tv_deleteCoppy.setTextColor(textColor);
        tv_sort.setText(sort);
        if(main.typeSquare.equals("non_square")){
            tv_type_square.setText("Hiện thị vuông");
        }
        else{
            tv_type_square.setText("Chỉnh dạng lưới");
        }

        tv_choose.setTextColor(textColor);
        if (main.NUMCOLUMN == 9) {
            tv_size_square_small.setEnabled(false);
            tv_size_square_small.setTextColor(createDarkColor(textColor.getDefaultColor()));
        }
        if (main.NUMCOLUMN == 1) {
            tv_size_square_big.setEnabled(false);
            tv_size_square_big.setTextColor(createDarkColor(textColor.getDefaultColor()));
        }
    }

    private void updateSizeSquareSmall() {
        main.NUMCOLUMN += 2;
        tv_size_square_big.setEnabled(true);
        tv_size_square_big.setTextColor(main.getColor(R.color.textview_form));
        if (main.NUMCOLUMN == 9) {
            tv_size_square_small.setEnabled(false);
            tv_size_square_small.setTextColor(main.getColor(R.color.blue_press));
        }
        DoSthWithOrientation(getResources().getConfiguration().orientation);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(images.size() - 1);
    }

    private void updateSizeSquareBig() {
        main.NUMCOLUMN -= 2;
        tv_size_square_small.setEnabled(true);
        tv_size_square_small.setTextColor(main.getColor(R.color.textview_form));
        if (main.NUMCOLUMN == 1) {
            tv_size_square_big.setEnabled(false);
            tv_size_square_big.setTextColor(main.getColor(R.color.blue_press));
        }
        DoSthWithOrientation(getResources().getConfiguration().orientation);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(images.size() - 1);
    }

    private void updateTypeSquare() {
        if (tv_type_square.getText().toString().equals("Chỉnh dạng lưới")) {
            tv_type_square.setText("Hiện thị vuông");
            main.typeSquare = "non_square";
        } else {
            tv_type_square.setText("Chỉnh dạng lưới");
            main.typeSquare = "square";
        }
        adapter.notifyDataSetChanged();
    }

    private void ChangeColorBackground() {
        //dialog_header.dismiss();
        ColorPicker(1);
    }

    private void ChangeTextColor() {
        ColorPicker(2);
    }

    private void ColorPicker(int type) {
        int mcolor = -1;
        if (type == 1) {
            mcolor = main.mainColorBackground;
        } else if (type == 2) {
            mcolor = main.mainColorText.getDefaultColor();
        }
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(main, mcolor,
                new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        if (type == 1) {
                            main.mainColorBackground = color;
                            setThemeBackGround(main.mainColorBackground);
                        } else if (type == 2) {
                            main.mainColorText = getColorStateList(color);
                            setThemeText(main.mainColorText);
                        }

                    }
                });
        colorPicker.show();
    }

    private ColorStateList getColorStateList(int color) {
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_pressed}, // unchecked
                new int[]{android.R.attr.state_pressed}};
        int[] mcolors = new int[]{
                color, createDarkColor(color)
        };
        return new ColorStateList(states, mcolors);
    }

    private int createDarkColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.5;
        return Color.HSVToColor(hsv);
    }
    private void sortAction(){
        if (tv_sort.getText().toString().equals("Sắp xếp theo ngày")){
            tv_sort.setText("Sắp xếp theo kích cỡ");
            sort = "Sắp xếp theo kích cỡ";
            sortDate();
        }
        else if (tv_sort.getText().toString().equals("Sắp xếp theo kích cỡ")){
            tv_sort.setText("Sắp xếp theo ngày");
            sort = "Sắp xếp theo ngày";
            sortSize();
        }
    }
    private void sortDate(){
        int pos = 0;
        for (int i = 0; i < MainActivity.images.size(); i++) {
            if (MainActivity.images.get(i).getDeleted().equals("F") && MainActivity
                    .images.get(i).getHide().equals("F")){
                images.set(pos++,MainActivity.images.get(i));
            }
        }
        adapter.images = images;
        adapter.notifyDataSetChanged();
    }
    private void sortSize(){
        for (int i = 0; i < images.size() - 1; i++) {
            for (int j = i + 1; j < images.size(); j++) {
                if (images.get(i).getSize() > images.get(j).getSize()) {
                    Image temp = images.get(i);
                    images.set(i, images.get(j));
                    images.set(j, temp);
                }
            }
        }
        adapter.images = images;
        adapter.notifyDataSetChanged();
    }
    private void deleteCopy(){
        copyImages.clear();
        for (int i = 0; i < images.size(); i++) {
            for (int j = i + 1; j < images.size(); j++) {
                if (copyImages.contains(images.get(i)) || copyImages.contains(images.get(j))) {
                    continue;
                }
                if (compareBitmap(images.get(i).getImgBitmap(), images.get(j).getImgBitmap())) {
                    copyImages.add(images.get(j));
                    Log.e("Copy Images Location", Integer.toString(j));
                    Log.e("Copy Images ID", Long.toString(images.get(j).getId()));
                }
            }
        }
        Log.e("Copy Images", Integer.toString(copyImages.size()));
        if (copyImages.size() == 0) {
            Toast.makeText(main, "Không tìm thấy ảnh trùng nhau!", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
        alertDialogBuilder.setMessage("Tìm thấy " + copyImages.size() + " ảnh trùng, có muốn chuyển vào thùng rác?");
        alertDialogBuilder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < copyImages.size(); i++) {
                    copyImages.get(i).setDeleted("T");

                    main.trashCanLayout.updateTrashCan(copyImages.get(i));

                    long idImage = copyImages.get(i).getId();
                    String key = copyImages.get(i).getKey();
                    for (int j = 0; j < images.size(); j++) {
                        if (images.get(j).getId() == idImage) {
                            images.remove(j);
                            break;
                        }
                    }

                    adapter.updateImagesInShowImageAllAdapter(idImage);
                    MainActivity.dataResource.updateStateImageDeletedIsTrue(idImage, key);
                }
                adapter.notifyDataSetChanged();
                adapter.resetChooseSelection();
                Toast.makeText(main, "Đã chuyển vào thùng rác!", Toast.LENGTH_SHORT).show();
                copyImages.clear();
            }
        });

        alertDialogBuilder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copyImages.clear();
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private static boolean compareBitmap(Bitmap b1, Bitmap b2) {
        if (b1.getWidth() == b2.getWidth() && b1.getHeight() == b2.getHeight()) {
            int[] pixels1 = new int[b1.getWidth() * b1.getHeight()];
            int[] pixels2 = new int[b2.getWidth() * b2.getHeight()];

            b1.getPixels(pixels1, 0, b1.getWidth(), 0, 0, b1.getWidth(), b1.getHeight());
            b2.getPixels(pixels2, 0, b2.getWidth(), 0, 0, b2.getWidth(), b2.getHeight());

            return Arrays.equals(pixels1, pixels2);
        } else {
            return false;
        }
    }

    private Uri saveImage(Bitmap image, int index) {
        File imagesFolder = new File(main.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagesFolder.mkdirs();
            File file = new File(imagesFolder, "shared_image_" + index + ".png");
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

    private void shareImageUri(ArrayList<Uri> uris) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        main.startActivity(intent);
    }

    @Override
    public void onPause() {
        super.onPause();
        debug("onPause " + String.valueOf(images.size()));
    }

    @Override
    public void onStop() {
        super.onStop();
        debug("onStop " + String.valueOf(images.size()));
    }

    public void URLdownloadSuc() {
        main.isURLdownload = false;
        fragmenturl.dismiss();
    }
}