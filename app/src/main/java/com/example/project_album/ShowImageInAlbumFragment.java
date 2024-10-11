package com.example.project_album;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShowImageInAlbumFragment extends Fragment implements View.OnClickListener {
    private Album album;
    private MainActivity main;
    public ShowImageInAlbumAdapter image_adapter;
    private RecyclerView recyclerView;
    private TextView tv_back_to_album,tv_choose;
    private Button btn_extend;
    private ImageView img_all_info;
    private View v_allInfo;
    private TextView tv_add_to_album ;
    private TextView tv_delete;
    private TextView tv_favorite;
    private TextView tv_copy;
    private TextView tv_name;
    private TextView tv_move;
    private Dialog dialog;
    private ConstraintLayout constraintLayout;
    public ShowImageInAlbumFragment(Album album){
        this.album = album;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = (MainActivity)getActivity();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_in_album, container, false);
        // view for all info dialog
        v_allInfo = main.getLayoutInflater().inflate(R.layout.all_info_album1, null);
        tv_add_to_album = v_allInfo.findViewById(R.id.add_to_album);
        tv_delete = v_allInfo.findViewById(R.id.delete);
        tv_favorite = v_allInfo.findViewById(R.id.favorite);
        tv_copy = v_allInfo.findViewById(R.id.coppy);
        tv_add_to_album.setOnClickListener(this);
        tv_move = v_allInfo.findViewById(R.id.move);
        if (album.getName().equals("Tất cả")){
            tv_move.setEnabled(false);
            tv_move.setTextColor(main.getColor(R.color.grey));
            tv_delete.setEnabled(false);
            tv_delete.setTextColor(main.getColor(R.color.grey));
        }
        if (album.getName().equals("Mục yêu thích")){
            tv_move.setEnabled(false);
            tv_move.setTextColor(main.getColor(R.color.grey));
            tv_favorite.setText("Bỏ ưa thích");
        }
        dialog = new Dialog(main);
        dialog.setContentView(v_allInfo);
        // finish view for all info dialog

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_image_in_album);
        constraintLayout = (ConstraintLayout)view.findViewById(R.id.contrant1);
        DoSthWithOrientation(getResources().getConfiguration().orientation);
        tv_back_to_album = view.findViewById(R.id.tv_back);
        tv_choose = view.findViewById(R.id.tv_choose);
        btn_extend = view.findViewById(R.id.btn_many);
        image_adapter = new ShowImageInAlbumAdapter(main, R.layout.item_image, album.getImages());

        tv_back_to_album.setOnClickListener(this);
        tv_choose.setOnClickListener(this);
        tv_move.setOnClickListener(this);
        tv_favorite.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        tv_copy.setOnClickListener(this);
        tv_name = view.findViewById(R.id.tv_name);
        tv_name.setText(album.getName());
        recyclerView.setAdapter(image_adapter);

        img_all_info = main.bottom_navigation_album.findViewById(R.id.img_all_info);
        img_all_info.setOnClickListener(this);
        setTheme(main.mainColorBackground,main.mainColorText);
        return view;
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int newOrientation = newConfig.orientation;
        DoSthWithOrientation(newOrientation);
        image_adapter.notifyDataSetChanged();
    }

    private void DoSthWithOrientation(int newOrientation) {
        if (newOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),5));
        } else if (newOrientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == tv_back_to_album.getId()){
            FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();
            main.getSupportFragmentManager().popBackStack();
            transaction.remove(this);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.commit();
        }
        else if(view.getId() == tv_choose.getId()){
            if(tv_choose.getText().equals("Chọn")){
                image_adapter.setChooseSelection(true);
                tv_choose.setText("Hủy");
                tv_back_to_album.setVisibility(View.INVISIBLE);
                main.mBottomNavigationView.setVisibility(View.INVISIBLE);
                main.bottom_navigation_album.setVisibility(View.VISIBLE);
            }
            else{
                main.bottom_navigation_album.setVisibility(View.INVISIBLE);
                main.mBottomNavigationView.setVisibility(View.VISIBLE);
                tv_back_to_album.setVisibility(View.VISIBLE);
                image_adapter.setChooseSelection(false);
                image_adapter.resetChooseSelection();
                tv_choose.setText("Chọn");
                image_adapter.notifyDataSetChanged();

            }
        }
        else if(view.getId() == img_all_info.getId()){
            if(image_adapter.image_chosen.size()>0) {
                AllInfoDialog();
            }
            else{
                Toast.makeText(main,"Bạn chưa chọn ảnh",Toast.LENGTH_SHORT).show();
            }
        }
        else if (view.getId() == tv_add_to_album.getId()){
            dialog.cancel();
            addToAlbum();
        }
        else if(view.getId() == tv_move.getId()){
            dialog.cancel();
            moveToAlbum();
        }
        else if (view.getId() == tv_copy.getId()){
            dialog.cancel();
            coppyImage();
        }
        else if (view.getId() == tv_delete.getId()){
            dialog.cancel();
            deleteImAb();
            Toast.makeText(main,"Đã xóa hình ảnh khỏi album",Toast.LENGTH_SHORT).show();
        }
        else if (view.getId() == tv_favorite.getId()){
            dialog.cancel();
            if (album.getName().equals("Mục yêu thích")){
                unFavorite();
                Toast.makeText(main,"Đã bỏ yêu thích",Toast.LENGTH_SHORT).show();
            }
            else{
                updateFavorite(image_adapter.image_chosen);
                Toast.makeText(main,"Đã thêm vào mục yêu thích",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveToAlbum() {
        main.moveImAb = true;
        FragmentManager fragmentmanager = main.getSupportFragmentManager();
        FragmentTransaction ft = fragmentmanager.beginTransaction();
        Fragment fragment = new MyAlbumFragment(this);
        ft.add(R.id.replace_fragment_layout,fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }

    private void AllInfoDialog(){

        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams attribute = window.getAttributes();
        attribute.y = 280;
        attribute.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        dialog.show();
    }
    private void addToAlbum(){
        main.addImAb = true;
        FragmentManager fragmentmanager = main.getSupportFragmentManager();
        FragmentTransaction ft = fragmentmanager.beginTransaction();
        Fragment fragment = new MyAlbumFragment(this);
        ft.add(R.id.replace_fragment_layout,fragment);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }
    public void AddToNewAlbum(long idAlbum,String name){
        if (main.moveImAb) {
            main.moveImAb = false;
            ArrayList<Image> imgs = new ArrayList<>();
            imgs.addAll(image_adapter.image_chosen);
            for (int j = 2; j < AlbumLayout.albums.size(); j++) {
                if (AlbumLayout.albums.get(j).getId() == album.getId()) {
                    for (int i = 0; i < imgs.size(); i++) {
                        MainActivity.dataResource.deleteImageInAlbum(imgs.get(i), album.getId());
                        AlbumLayout.albums.get(j).removeImage(imgs.get(i));
                    }
                    album = AlbumLayout.albums.get(j);
                    MainActivity.dataFirebase.updateAlbum(album);
                    break;
                }
            }

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
        }
        else if(main.addImAb){
            main.addImAb = false;
            ArrayList<Image> imgs = new ArrayList<>();
            imgs.addAll(image_adapter.image_chosen);
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
        }
        originalState();
    }

    private void originalState(){
        main.bottom_navigation_album.setVisibility(View.INVISIBLE);
        main.mBottomNavigationView.setVisibility(View.VISIBLE);
        tv_back_to_album.setVisibility(View.VISIBLE);
        image_adapter.setChooseSelection(false);
        image_adapter.resetChooseSelection();
        tv_choose.setText("Chọn");
        image_adapter.notifyDataSetChanged();
        main.albumLayout.update();
    }

    private void updateFavorite(ArrayList<Image> imgs){
        for(Image image : imgs){
            if(image.getFavorite().equals("F")){
                //main.albumLayout.updateFavorite(imgs.get(i));
                MainActivity.dataResource.likeImage(image.getId(),image.getKey());
                FavoriteLayout.images.add(image);
            }
        }
        originalState();
    }
    private void unFavorite(){
        for(Image image :image_adapter.image_chosen){
            MainActivity.dataResource.unlikeImage(image.getId(),image.getKey());
            FavoriteLayout.images.remove(image);
        }
        originalState();
    }
    private void coppyImage(){
        ArrayList<Image> imgs = new ArrayList<>();
        imgs.addAll(image_adapter.image_chosen);
        for (int j = 0; j < AlbumLayout.albums.size(); j++) {
            if (AlbumLayout.albums.get(j).getId() == album.getId()) {
                int nextk = Integer.parseInt(MainActivity.dataFirebase.findNextkey());
                for (int i = 0; i < imgs.size(); i++) {
                    Image copyImage = new Image(imgs.get(i).getImgBitmap(), main.GenerateName());
                    copyImage.setKey(String.valueOf(nextk+i));
                    if(album.getName().equals("Mục yêu thích")){
                        copyImage.setFavorite("T");
                    }
                    if(!album.getName().equals("Tất cả") && !album.
                            getName().equals("Mục yêu thích")) {
                        MainActivity.dataResource.InsertAlbumImage(album.getId(), copyImage.getId());
                    }
                    MainActivity.dataFirebase.insertImage(copyImage);
                    AlbumLayout.albums.get(j).addImage(copyImage);
                    MainActivity.images.add(copyImage);
                    if(!album.getName().equals("Tất cả")) {
                        AllLayout.images.add(copyImage);
                    }
                }
                album = AlbumLayout.albums.get(j);
                if(!album.getName().equals("Tất cả") && !album.
                        getName().equals("Mục yêu thích")){
                    MainActivity.dataFirebase.updateAlbum(AlbumLayout.albums.get(j));
                }
                break;
            }
        }
        main.bottom_navigation_album.setVisibility(View.INVISIBLE);
        main.mBottomNavigationView.setVisibility(View.VISIBLE);
        tv_back_to_album.setVisibility(View.VISIBLE);
        image_adapter = new ShowImageInAlbumAdapter(main, R.layout.item_image, album.getImages());
        recyclerView.setAdapter(image_adapter);
        recyclerView.scrollToPosition(album.getImages().size());
        tv_choose.setText("Chọn");
        main.albumLayout.update();
    }
    private void deleteImAb(){
        for (Image image : image_adapter.image_chosen) {
            if(album.getName().equals("Mục yêu thích")){
                image.setFavorite("F");
                main.favoriteLayout.updateFavorite(image);
                Log.e("Show",image.getId()+" "+image.getKey());
                MainActivity.dataResource.unlikeImage(image.getId(),image.getKey());
                album.removeImage(image);
            }
            else {
                MainActivity.dataResource.deleteImageInAlbum(image, album.getId());
                album.removeImage(image);
                MainActivity.dataFirebase.updateAlbum(album);
            }
        }
        originalState();
    }
    private void setTheme(int backgroundColor, ColorStateList textColor){
        setThemeBackGround(backgroundColor);
        setThemeText(textColor);
    }

    private void setThemeText(ColorStateList textColor) {
        tv_back_to_album.setTextColor(textColor);
        tv_choose.setTextColor(textColor);
        tv_add_to_album.setTextColor(textColor);
        tv_favorite.setTextColor(textColor);
        tv_copy.setTextColor(textColor);
        tv_name.setTextColor(textColor);
        if (!album.getName().equals("Tất cả") && !album.getName().equals("Mục yêu thích")){
            tv_move.setTextColor(textColor);
        }
        if (!album.getName().equals("Tất cả")){
            tv_delete.setTextColor(textColor);
        }
    }

    private void setThemeBackGround(int backgroundColor) {
        constraintLayout.setBackgroundColor(backgroundColor);
    }
}
