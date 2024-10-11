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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ShowImageInAllAdapter extends RecyclerView.Adapter<ShowImageInAllAdapter.ViewHolder> {
    public ArrayList<Image> images;
    private int idLayout;
    private MainActivity activity;
    public ArrayList<Boolean> ischoose = new ArrayList<>();
    public ArrayList<Image> image_chosen = new ArrayList<>();
    private boolean choose_selection;
    public int count = 0;
    private TextView tv_chose;

    ShowImageInAllAdapter(MainActivity activity,int idLayout,ArrayList<Image>images){
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
        activity.allLayout.setTextInfo(images.get(position).getDate());
        if(activity.typeSquare.equals("square")) {
            holder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else{
            holder.img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        holder.img.setImageBitmap(images.get(position).getImgBitmap());
        debug(String.valueOf(position));
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            debug("ok");
            RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams
                    (MainActivity.Width / activity.NUMCOLUMN - 40,
                            MainActivity.Width / activity.NUMCOLUMN - 40);
            layout.setMargins(0,0,0,10);
            if(checkFirstRow(position)){
                layout.setMargins(0,200,0,10);;
            }
            else if(checkLastRow(position)){
                layout.setMargins(0,0,0,200);
            }
            holder.img.setLayoutParams(layout);

        }
        else {
            RelativeLayout.LayoutParams layout =new RelativeLayout.LayoutParams(
                    MainActivity.Height / (activity.NUMCOLUMN*2) - 30,
                    MainActivity.Height / (activity.NUMCOLUMN*2) - 30);
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
                    ft.replace(R.id.replace_fragment_layout,fragment);
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
        image_chosen = new ArrayList<>();
    }

    private boolean checkFirstRow(int position){
        if(activity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT ){
            if(position<activity.NUMCOLUMN)
                return true;
        }
        else {
            if (position<activity.NUMCOLUMN*2){
                return true;
            }
        }
        return false;
    }
    private boolean checkLastRow(int position){
        if(activity.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT ){
            if(position>= activity.NUMCOLUMN*((images.size()-1)/activity.NUMCOLUMN))
                return true;
        }
        else {
            if (position>=activity.NUMCOLUMN*2*((images.size()-1)/activity.NUMCOLUMN*2)){
                return true;
            }
        }
        return false;
    }
    public void insert(Image img){
        images.add(img);
        ischoose.add(false);
    }
    public void update(ArrayList<Image> imgs){
        this.images = imgs;
    }
    public void updateImagesInShowImageAllAdapter(long id){
        for (int i=0;i<images.size();i++){
            if (images.get(i).getId()==id){
                images.remove(i);
                break;
            }
        }
    }
}
