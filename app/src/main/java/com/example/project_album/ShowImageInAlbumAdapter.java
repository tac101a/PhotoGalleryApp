package com.example.project_album;

import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShowImageInAlbumAdapter extends RecyclerView.Adapter<ShowImageInAlbumAdapter.ViewHolder> {
    private ArrayList<Image> images;
    private int idLayout;
    private MainActivity activity;
    private ArrayList<Boolean> ischoose = new ArrayList<>();
    public ArrayList<Image> image_chosen = new ArrayList<>();
    private boolean choose_selection;
    public int count = 0;
    private TextView tv_chose;

    ShowImageInAlbumAdapter(MainActivity activity,int idLayout,ArrayList<Image>images){
        this.activity = activity;
        this.idLayout = idLayout;
        this.images = images;
        for(int i = 0;i<images.size();i++){
            ischoose.add(false);
        }
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = activity.getLayoutInflater().inflate(idLayout,parent,false);
        tv_chose = activity.findViewById(R.id.tv1);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.img.setImageBitmap(images.get(position).getImgBitmap());
        debug(String.valueOf(position));
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            debug("ok");
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams
                    (MainActivity.Width / 3 - 40, MainActivity.Width / 3 - 40);
            layout.setMargins(0,0,0,10);
            if(checkFirstRow(position)){
                layout.setMargins(0,300,0,10);
            }
            else if(checkLastRow(position)){
                layout.setMargins(0,0,0,500);
            }
            holder.img.setLayoutParams(layout);

        }
        else {
            RelativeLayout.LayoutParams layout =new RelativeLayout.LayoutParams(
                    MainActivity.Height / 5 - 30, MainActivity.Height / 5 - 30);
            layout.setMargins(0,0,0,10);
            holder.img.setLayoutParams(layout);
        }

        if(!ischoose.get(position)){

            holder.cb.setChecked(false);
            holder.cb.setVisibility(View.INVISIBLE);
        }
        else{

            holder.cb.setChecked(true);
            holder.cb.setVisibility(View.VISIBLE);
        }
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(choose_selection){
                    if(ischoose.get(position)){
                        ischoose.set(position,false);
                        holder.cb.setChecked(false);
                        holder.cb.setVisibility(View.INVISIBLE);
                        count --;
                        image_chosen.remove(images.get(position));
                    }
                    else{
                        image_chosen.add(images.get(position));
                        ischoose.set(position,true);
                        holder.cb.setChecked(true);
                        holder.cb.setVisibility(View.VISIBLE);
                        count++;
                    }
                    if(count>0) {
                        tv_chose.setText("Đã chọn " + String.valueOf(count) + " ảnh");
                    }
                    else{
                        tv_chose.setText("Chọn mục");
                    }
                }
                else{
                    FragmentManager fragmentmanager = activity.getSupportFragmentManager();
                    FragmentTransaction ft = fragmentmanager.beginTransaction();
                    Fragment fragment = new ViewPagerAllLayoutFragment(images,position);
                    ft.add(R.id.replace_fragment_layout,fragment);
                    ft.addToBackStack(fragment.getClass().getSimpleName());
                    ft.commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private CheckBox cb;

        ViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            cb = itemView.findViewById(R.id.check_box);
            cb.setVisibility(View.INVISIBLE);
        }
    }

    private void debug(String s){
        Log.e("Show image in album adapter: ",s);
    }
    public void setChooseSelection(boolean check){
        choose_selection = check;
        if(!check){
            image_chosen.clear();
        }
    }
    public void resetChooseSelection(){
        count = 0;
        if(tv_chose!=null)
            tv_chose.setText("Chọn mục");
        for(int i=0;i<images.size();i++){
            ischoose.set(i,false);
        }
        image_chosen.clear();
    }

    private boolean checkFirstRow(int position){
        if(activity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT ){
            if(position<3)
                return true;
        }
        else {
            if (position<5){
                debug(String.valueOf(position));
                return true;
            }
        }
        return false;
    }
    private boolean checkLastRow(int position){
        if(activity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT ){
            if(position>= 3*(images.size()/3))
                return true;
        }
        else {
            if (position>=5*(images.size()/5)){
                return true;
            }
        }
        return false;
    }
}
