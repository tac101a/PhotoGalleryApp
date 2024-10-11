package com.example.project_album;


import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AlbumLayout extends Fragment {
    MainActivity main;

    public static ArrayList<Album> albums = new ArrayList<Album>();
    ShowImageInAlbumFragment showimage;
    private Bundle saveInstanceState;
    private GridView girdViewAlbum;
    private GridViewAlbumAdapter adapter_album;
    private ViewGroup view_album_top;
    private ViewGroup view_album_bottom;
    private TextView tv_album_small;
    private TextView tv_album_big;
    private TextView tv_all_my_album;
    private View mainView;
    private ViewGroup container;
    private static AlbumLayout instance;
    private FloatingActionButton btnAddAlbum;
    private ScrollView sv;
    private RelativeLayout layout_icon;
    public boolean isInit = false;
    private EditText txtTitle;
    private Button btnSave;
    private Button btnCancel;
    private TextView tv_heading;

    private MyAlbumFragment myAlbumFragment;

    //view dialog
    private Dialog dialog;

    //Hiden
    private String myFilePasswordForHide="user";
    String key = "hidepass";
    String passGetFromSharePre="";
    TextView tvHiden;
    TextView tvNumberHiden;
    private LinearLayout ln_all;
    private TextView tv_delete;
    private TextView tv_numdelete;


    private AlbumLayout(){
        debug("constructor");
        albums = MainActivity.dataResource.getAllAlbum();
        myAlbumFragment = new MyAlbumFragment();
    }
    public static AlbumLayout newInstance(String strArg){
//        if (instance == null){
//            instance =  new AlbumLayout();
//            Bundle args = new Bundle();
//            args.putString("strArg1", strArg);
//            instance.setArguments(args);
//        }
//        return instance;
        AlbumLayout fragment = new AlbumLayout();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity)getActivity() ;
        debug("onCreate "+String.valueOf(albums.size()));
        InitAlbums();
        for(int i = 1;i<albums.size();i++){
            int size = albums.get(i).getImages().size();
            if(size!=0 && albums.get(i).get_image(size-1).getImgBitmap() == null)
                albums.get(i).get_image(size-1).setImgBitmap(main.getImageFromPath
                        (albums.get(i).get_image(size-1).getPath()));
        }
        isInit = true;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_album_layout, container, false);
        this.container = container;
        this.saveInstanceState = saveInstanceState;

        //view for dialog
        createDialogView();

        //finish view for dialog

        view_album_top = (ViewGroup)mainView.findViewById(R.id.ln_image_view_top);
        view_album_bottom = (ViewGroup)mainView.findViewById(R.id.ln_image_view_bottom);

        ln_all = mainView.findViewById(R.id.view_for_tab_album);
        tv_album_big = mainView.findViewById(R.id.tv_album_big);
        tv_album_small = mainView.findViewById(R.id.tv_album_small);
        tv_all_my_album = mainView.findViewById(R.id.tv_all_my_album);
        btnAddAlbum = mainView.findViewById(R.id.btn_add_album);
        layout_icon = (RelativeLayout)mainView.findViewById(R.id.layout_icon);
        sv = (ScrollView)mainView.findViewById(R.id.sv_parent);
        tv_delete = mainView.findViewById(R.id.tv_delete);
        tv_numdelete = mainView.findViewById(R.id.tv_num_delete);
        tv_numdelete.setText(String.valueOf(TrashCanLayout.images.size()));
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTrashcanFragment();
            }
        });
        //Hidden
        tvHiden=mainView.findViewById(R.id.tv_hide);
        tvNumberHiden=mainView.findViewById(R.id.tv_num_hiden);
        tvNumberHiden.setText(String.valueOf(HideInAlbumLayoutFragment.images.size()));
        tvHiden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFragmentPictureHidden();
            }
        });
        //Hiden
        sv.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view,int i, int i1, int i2, int i3) {
                if(tv_album_big.getGlobalVisibleRect(new Rect()) == false){
                    tv_album_small.setTextSize(20);
                    layout_icon.setBackgroundColor(main.getColor(R.color.black_1));
                }
                else{
                    tv_album_small.setTextSize(0);
                    layout_icon.setBackgroundColor(main.mainColorBackground);
                }
            }
        });

        btnAddAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddAlbumForm();
            }
        });

        UpdateConfiguration(getResources().getConfiguration());

        tv_all_my_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllMyAlbumClick();
            }
        });
        setTheme(main.mainColorBackground,main.mainColorText);
        return mainView;
    }

    private void createDialogView(){
        dialog = new Dialog(main);
        dialog.setCancelable(false);


        View view  = getActivity().getLayoutInflater().inflate(R.layout.custom_add_album, null);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        if(window == null){
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attribute = window.getAttributes();
        attribute.gravity =Gravity.CENTER;
        txtTitle = view.findViewById(R.id.txt_title);
        btnSave = view.findViewById(R.id.btn_save);
        btnCancel = view.findViewById(R.id.btn_cancel);
        tv_heading = view.findViewById(R.id.tv_heading);
        txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(txtTitle.length() != 0){
                    btnSave.setTextColor(Color.BLUE);
                    btnSave.setBackgroundResource(R.drawable.custtom_button);
                    btnSave.setClickable(true);

                }
                else{
                    btnSave.setTextColor(Color.GRAY);
                    btnSave.setBackgroundResource(R.color.black);
                    btnSave.setClickable(false);
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        UpdateConfiguration(newConfig);
    }

    private void UpdateConfiguration(Configuration newConfig){
        int newOrientation = newConfig.orientation;

        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            tv_album_big.setTextSize(0);
            tv_album_small.setTextSize(30);
            setViewAlbumOnLandscapeOritation();
        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {
            tv_album_big.setTextSize(40);
            tv_album_small.setTextSize(0);
            setViewAlbumOnPortraitOritation();
        }
    }
    private void setViewAlbumOnPortraitOritation() {
        view_album_top.removeAllViews();
        view_album_bottom.removeAllViews();
        for(int i = 0;i<albums.size();i+=2){
            View viewchild = createViewChildForAlbum(i);
            //viewchild.setPadding(0,0,130,0);
            view_album_top.addView(viewchild);
        }
        for(int i = 1;i<albums.size();i+=2){
            View viewchild = createViewChildForAlbum(i);
            view_album_bottom.addView(viewchild);

        }
    }

    private void setViewAlbumOnLandscapeOritation(){
        view_album_top.removeAllViews();
        view_album_bottom.removeAllViews();
        for(int i = 0;i<albums.size();i++){
            View viewchild = createViewChildForAlbum(i);
            ImageView img = viewchild.findViewById(R.id.image);
            //img.setLayoutParams(new LinearLayout.LayoutParams(MainActivity.Width/3 - 50,MainActivity.Width/3 -50));
            view_album_top.addView(viewchild);
        }
    }

    private View createViewChildForAlbum(int position){
        View viewchild = getActivity().getLayoutInflater().inflate(R.layout.item_album,null);
        LinearLayout ln = viewchild.findViewById(R.id.ln_parent);
        ln.setBackgroundColor(main.mainColorBackground);
        viewchild.setId(position);
        ImageView img = viewchild.findViewById(R.id.image);
        TextView tvname = viewchild.findViewById(R.id.tv_album_name);
        TextView tvlength = viewchild.findViewById(R.id.tv_album_length);
        if(albums.get(position).length() != 0 ){
            img.setImageBitmap(albums.get(position).get_image(
                    albums.get(position).length() -1 ).getImgBitmap());
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else{
            img.setScaleType(ImageView.ScaleType.CENTER);
        }
        CardView cv = (CardView) viewchild.findViewById(R.id.cv_album);
        cv.setLayoutParams(new LinearLayout.LayoutParams(
                MainActivity.Width/2-150,MainActivity.Width/2-150));
        tvname.setText(albums.get(position).getName());
        tvlength.setText(String.valueOf(albums.get(position).length()));
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentmanager.beginTransaction();
                Fragment fragment = new ShowImageInAlbumFragment(albums.get(position));
                showimage = (ShowImageInAlbumFragment) fragment;
                ft.add(R.id.replace_fragment_layout,fragment);
                ft.addToBackStack(fragment.getClass().getSimpleName());
                ft.commit();
                //talk();
            }
        });
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(position!=0 && position!=1) {
                    View v = getActivity().getLayoutInflater().inflate(R.layout.custtom_edit_album, null);
                    Dialog dialog1 = new Dialog(getActivity());
                    dialog1.setContentView(v);
                    Window window = dialog1.getWindow();
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    WindowManager.LayoutParams attribute = window.getAttributes();
                    attribute.gravity = Gravity.CENTER;
                    ImageView img = v.findViewById(R.id.image);
                    try {
                        img.setImageBitmap(albums.get(position).get_image(
                                albums.get(position).length() - 1).getImgBitmap());
                    } catch (Exception e) {
                        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    }


                    TextView tv_name = v.findViewById(R.id.tv_album_name);
                    tv_name.setText(albums.get(position).getName());
                    TextView tv_length = v.findViewById(R.id.tv_album_length);
                    tv_length.setText(String.valueOf(albums.get(position).length()));
//                LinearLayout ln = (LinearLayout) v.findViewById(R.id.ln_parent);
//                ln.setBackgroundColor(getResources().getColor(R.color.black_n));

                    TextView tv_rename = v.findViewById(R.id.tv_rename);
                    TextView tv_delete = v.findViewById(R.id.tv_delete);
                    TextView tv_slider = v.findViewById(R.id.tv_slider);

                    tv_rename.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog1.dismiss();
                            FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fragmentmanager.beginTransaction();
                            ft.add(R.id.replace_fragment_layout, myAlbumFragment);
                            ft.addToBackStack(myAlbumFragment.getClass().getSimpleName());
                            ft.commit();
                            tv_heading.setText("Đặt tên Album");
                            dialog.show();
                            btnSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String name = txtTitle.getText().toString();
                                    update();
                                    dialog.cancel();
                                    MainActivity.dataResource.updateName(albums.get(position).getName(),
                                            name);
                                    albums.get(position).setName(name);
                                    MainActivity.dataFirebase.updateAlbum(albums.get(position));
                                    UpdateConfiguration(getResources().getConfiguration());
                                }
                            });
                        }
                    });

                    dialog1.show();
                    tv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MainActivity.dataResource.deleteAlbum(albums.get(position).getId(),
                                    albums.get(position).getKey());
                            albums.remove(position);
                            dialog1.cancel();
                            UpdateConfiguration(getResources().getConfiguration());
                        }
                    });
                    tv_slider.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
                            FragmentTransaction ft = fragmentmanager.beginTransaction();
                            Fragment fragment = new ViewPagerAllLayoutFragment(albums.get(position).getImages());
                            ft.add(R.id.replace_fragment_layout, fragment);
                            ft.addToBackStack(fragment.getClass().getSimpleName());
                            ft.commit();
                            dialog1.dismiss();
                        }
                    });

                }
                return true;
            }
        });
        return viewchild;
    }
    public void InitAlbums() {
        if(!isInit) {
            ArrayList<Image> images3;
            images3 = FavoriteLayout.images;
            Album a2 = new Album(-1,"Mục yêu thích", images3);

            ArrayList<Image> images4;
            images4 = AllLayout.images;
            Album a1 = new Album(-2,"Tất cả", images4);
            albums.add(0, a1);
            albums.add(1, a2);
            ConvertAlbum();
        }
        else{
//            albums.get(1).setImages(FavoriteLayout.images);
//            albums.get(0).setImages(AllLayout.images);
            for(int i =2;i<albums.size();i++){
                ArrayList<Image> temp = albums.get(i).getImages();
                for(int j =0;j<temp.size();j++){
                    if (temp.get(j).getDeleted().equals("T") ||
                    temp.get(j).getHide().equals("T")){
                        temp.remove(temp.get(j));
                    }
                }
            }
        }

    }
    private void ConvertAlbum(){
        for(int i = 2;i<albums.size();i++){
            debug(albums.get(i).getName()+" "+albums.get(i).getImages().size());
            for(int j = 0;j<albums.get(i).getImages().size();j++){
                debug(String.valueOf(i));
                Image img = getImage(albums.get(i).getImages().get(j).getId());
                if(img == null){
                    albums.get(i).removeImage(j);
                    j--;
                }
                else {
                    albums.get(i).getImages().set(j, img);
                }
            }
        }

    }
    private Image getImage(long id){
        for(int i = 0;i<MainActivity.images.size();i++){
            if(id == MainActivity.images.get(i).getId() && MainActivity.images.get(i).getDeleted().equals("F")){
                return MainActivity.images.get(i);
            }
        }
        return null;
    }
    private void AllMyAlbumClick(){
        FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentmanager.beginTransaction();
        ft.add(R.id.replace_fragment_layout,myAlbumFragment);
        ft.addToBackStack(myAlbumFragment.getClass().getSimpleName());
        ft.commit();
    }

    private void AddAlbumForm(){
        FragmentManager fragmentmanager = getActivity().getSupportFragmentManager();
        FragmentTransaction ft = fragmentmanager.beginTransaction();
        ft.add(R.id.replace_fragment_layout,myAlbumFragment);
        ft.addToBackStack(myAlbumFragment.getClass().getSimpleName());
        ft.commit();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.isAddAlbum = true;
                MainActivity.dataFirebase.insertAlbum(txtTitle.getText().toString());
                //MyAlbumFragment.gridViewAlbumAdapter.notifyDataSetChanged();
            }
        });

        dialog.show();
    }

    private void talk(){
        Toast.makeText(getActivity(), "click",Toast.LENGTH_SHORT).show();
    }
    private void debug(String k){
        Log.e("Album Layout",k);
    }
    public void update(){
        try{
            InitAlbums();
            UpdateConfiguration(getResources().getConfiguration());
            showimage.image_adapter.notifyDataSetChanged();
            debug("oke");
        }
        catch (Exception e){

        }
        myAlbumFragment.update();
    }

    //=========================Ẩn==================
    public  void goToFragmentPictureHidden(){

        //Lấy mật khẩu từ SharePreferences
        SharedPreferences myPrefContainer = main.getSharedPreferences(myFilePasswordForHide, MainActivity.MODE_PRIVATE);
        if (( myPrefContainer != null ) && myPrefContainer.contains(key)){
            passGetFromSharePre = myPrefContainer.getString(key, "");
        }
        // Hiện diaglog nhập mật khẩu
        Dialog dialogEnterIntoHide = new Dialog(main);
        dialogEnterIntoHide.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEnterIntoHide.setContentView(R.layout.custom_enter_password_hide);
        Window window = dialogEnterIntoHide.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams attribute = window.getAttributes();
        attribute.gravity = Gravity.CENTER;
        EditText edtPassIntoHide=dialogEnterIntoHide.findViewById(R.id.edt_pass_into_hide);
        TextView txtForgotPass=dialogEnterIntoHide.findViewById(R.id.txt_forgot_pass);
        TextView txtResetPass=dialogEnterIntoHide.findViewById(R.id.txt_reset_pass);
        Button btnIntoHide=dialogEnterIntoHide.findViewById(R.id.btn_enter_hide);
        Button btnCancelIntoHide=dialogEnterIntoHide.findViewById(R.id.btn_cancel_enter_hide);
        btnIntoHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtPassIntoHide.getText().toString().equals(passGetFromSharePre)){
                    Toast.makeText(main, "Mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                }else{
                    FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
                    HideInAlbumLayoutFragment hideFragment=new HideInAlbumLayoutFragment();
                    ft.replace(R.id.replace_fragment_layout, hideFragment);
                    ft.commit();
                    dialogEnterIntoHide.dismiss();
                }
            }
        });
        btnCancelIntoHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEnterIntoHide.dismiss();
            }
        });
        dialogEnterIntoHide.show();

        //Nếu quên mật khẩu
        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEnterIntoHide.dismiss();
                //Hiển thị dialog mới
                displayDialogForgotPass();

            }
        });
        //Nếu muốn set lại mật khẩu
        txtResetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEnterIntoHide.dismiss();
                //Hiển thị dialog mới
                displayDialogResetPass(passGetFromSharePre);

            }
        });

    }
    public void setTextNumberHide(int numberHide){
        tvNumberHiden.setText(String.valueOf(numberHide));
    }
    public void displayDialogForgotPass(){
        Dialog dialogForgot = new Dialog(main);
        dialogForgot.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogForgot.setContentView(R.layout.custom_forgot_pass_for_hide);
        Window window = dialogForgot.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams attribute = window.getAttributes();
        attribute.gravity = Gravity.CENTER;
        TextView txtTitleForgot=dialogForgot.findViewById(R.id.title_forgot_pass_hide);
        TextView txtFirstText=dialogForgot.findViewById(R.id.txt_first_text);
        EditText edtOldPass=dialogForgot.findViewById(R.id.edt_old_pass);
        EditText edtNewPass=dialogForgot.findViewById(R.id.edt_new_pass);
        EditText edtVerifyPass=dialogForgot.findViewById(R.id.edt_verify_pass);
        Button btnCancel=dialogForgot.findViewById(R.id.btn_cancel_forgot);
        Button btnChangePass=dialogForgot.findViewById(R.id.btn_change_pass);
        txtTitleForgot.setText("Quên mật khẩu?");
        txtFirstText.setText("Mật khẩu login");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForgot.dismiss();
            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtOldPass.getText().toString().equals(main.password)
                        ||edtNewPass.getText().toString().equals("")
                        || !edtVerifyPass.getText().toString().equals(edtNewPass.getText().toString())){
                    Toast.makeText(main, "Nhập sai, hãy nhập lại", Toast.LENGTH_SHORT).show();
                }else{
                    //Cập nhật pass mới và quay lại goToFragmentPictureHidden
                    SharedPreferences myPrefContainer = main.getSharedPreferences(myFilePasswordForHide, MainActivity.MODE_PRIVATE);
                    SharedPreferences.Editor myPrefEditor = myPrefContainer.edit();
                    myPrefEditor.putString(key, edtNewPass.getText().toString());
                    main.dataFirebase.setHidePassIntoFirebase(edtNewPass.getText().toString());
                    main.hidepass=edtNewPass.getText().toString();
                    myPrefEditor.commit();
                    goToFragmentPictureHidden();
                    dialogForgot.dismiss();
                }
            }
        });
        dialogForgot.show();

    }
    public void displayDialogResetPass(String txtPassFromPre){
        Dialog dialogReset = new Dialog(main);
        dialogReset.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogReset.setContentView(R.layout.custom_forgot_pass_for_hide);
        Window window = dialogReset.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams attribute = window.getAttributes();
        attribute.gravity = Gravity.CENTER;
        TextView txtTitleForgot=dialogReset.findViewById(R.id.title_forgot_pass_hide);
        TextView txtFirstText=dialogReset.findViewById(R.id.txt_first_text);
        EditText edtOldPass=dialogReset.findViewById(R.id.edt_old_pass);
        EditText edtNewPass=dialogReset.findViewById(R.id.edt_new_pass);
        EditText edtVerifyPass=dialogReset.findViewById(R.id.edt_verify_pass);
        Button btnCancel=dialogReset.findViewById(R.id.btn_cancel_forgot);
        Button btnChangePass=dialogReset.findViewById(R.id.btn_change_pass);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogReset.dismiss();
            }
        });
        btnChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtOldPass.getText().toString().equals(txtPassFromPre)
                        ||edtNewPass.getText().toString().equals("")
                        || !edtVerifyPass.getText().toString().equals(edtNewPass.getText().toString())){
                    Toast.makeText(main, "Nhập sai, hãy nhập lại", Toast.LENGTH_SHORT).show();
                }else{
                    //Cập nhật pass mới và quay lại goToFragmentPictureHidden
                    SharedPreferences myPrefContainer = main.getSharedPreferences(myFilePasswordForHide, MainActivity.MODE_PRIVATE);
                    SharedPreferences.Editor myPrefEditor = myPrefContainer.edit();
                    myPrefEditor.putString(key, edtNewPass.getText().toString());
                    main.dataFirebase.setHidePassIntoFirebase(edtNewPass.getText().toString());
                    main.hidepass=edtNewPass.getText().toString();
                    myPrefEditor.commit();
                    goToFragmentPictureHidden();
                    dialogReset.dismiss();
                }

            }
        });
        dialogReset.show();

    }
    //=========================Ẩn==================

    private void goToTrashcanFragment(){
        main.mBottomNavigationView.setSelectedItemId(R.id.action_bin);
        FragmentTransaction ft = main.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.replace_fragment_layout, main.trashCanLayout);
        ft.commit();
    }
    private void setTheme(int backgroundColor, ColorStateList textColor){
        setThemeBackGround(backgroundColor);
        setThemeText(textColor);
    }

    private void setThemeText(ColorStateList textColor) {
        tv_all_my_album.setTextColor(textColor);
        tvHiden.setTextColor(textColor);
        tv_delete.setTextColor(textColor);
        btnAddAlbum.setImageTintList(textColor);
    }

    private void setThemeBackGround(int backgroundColor) {
        ln_all.setBackgroundColor(backgroundColor);
        btnAddAlbum.setBackgroundColor(backgroundColor);
        layout_icon.setBackgroundColor(backgroundColor);
    }
    public void addToDatabase(String key,String name){
        ArrayList<Image> image = new ArrayList<Image>();
        Album album = new Album(-3,name,image);
        album.setKey(key);
        albums.add(album);
        long id = MainActivity.dataResource.InsertAlbum(name,key);
        album.setId(id);
        dialog.dismiss();
        update();
    }
    public void deleteImAbDatabase(Image image){
        for (Album album : albums){
            MainActivity.dataResource.deleteImageInAlbum(image,album.getId());
            album.removeImage(image);
            MainActivity.dataFirebase.updateAlbum(album);
        }
    }
    public void updateAlbumFirebase(){
        InitAlbums();
        for(int i =2;i<albums.size();i++){
            MainActivity.dataFirebase.updateAlbum(albums.get(i));
        }
    }
}