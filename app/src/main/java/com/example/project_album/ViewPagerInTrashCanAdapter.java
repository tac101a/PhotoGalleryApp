package com.example.project_album;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPagerInTrashCanAdapter extends FragmentStateAdapter {
    ArrayList<Image> images;
    public ViewPagerInTrashCanAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public ViewPagerInTrashCanAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public ViewPagerInTrashCanAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, ArrayList<Image> images) {
        super(fragmentManager, lifecycle);
        this.images=images;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (images==null||images.isEmpty()){
            return null;
        }
        LargeImageFragment myLargeImageFragment=new LargeImageFragment(images.get(position));
//        Log.e("Day la lo1 o adapter","Anh= "+String.valueOf(images.get(position).getId()));
        return  myLargeImageFragment;
    }

    @Override
    public int getItemCount() {
        if (images!=null && !images.isEmpty()){
            return images.size();
        }
        return 0;
    }
    // Tạo một phương thức để cập nhật danh sách ảnh trong adapter
    public void updateImagesList(ArrayList<Image> newImages) {
        this.images = newImages;
        notifyDataSetChanged();
    }
}
