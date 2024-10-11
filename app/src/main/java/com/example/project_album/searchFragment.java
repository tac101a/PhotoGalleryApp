package com.example.project_album;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class searchFragment extends Fragment implements View.OnClickListener {
    private TextView tv_back;
    private EditText edt_search;
    private RecyclerView recyclerView;
    private ShowImageInAllAdapter adapter;
    private ArrayList<Image> images;
    MainActivity main;
    private Context context;
    private TextRecognizer recognizer;
    private ArrayList<String> textHolder = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("OnCreateView", String.valueOf(AllLayout.images.size()));
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        tv_back = view.findViewById(R.id.tv_back);
        edt_search = view.findViewById(R.id.edt_search);
        recyclerView = view.findViewById(R.id.rv_image_in_all);
        adapter = new ShowImageInAllAdapter(main, R.layout.item_image,images);
        Log.e("1231231231231",String.valueOf(adapter.images.size()));

        // onClick
        tv_back.setOnClickListener(this);
        //

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateImage();
                Log.e("Testsssssss",String.valueOf(adapter.images.size()));
            }
        });
        recyclerView.setOnClickListener(this);
        recyclerView.setAdapter(adapter);
        //
        for (int i =0;i<AllLayout.images.size();i++){
            adapter.insert(AllLayout.images.get(i));
        }
        return view;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.e("OnCreate", String.valueOf(AllLayout.images.size()));
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (MainActivity) getActivity();
            images = new ArrayList<>();
            for (int i =0;i<AllLayout.images.size();i++){
                textHolder.add("");
            }
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
                    for (int i =0;i<AllLayout.images.size();i++){
                        recognizeTextFromImage(AllLayout.images.get(i),i);
                    }
                }
            });
            thread.start();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == tv_back.getId()){
            closeFragment();
        }
    }
    void updateImage(){
        images.clear();
        adapter.ischoose.clear();
        if (edt_search.getText().toString().length() == 0){
            for (Image image:AllLayout.images){
                adapter.insert(image);
            }
        } else {
            String inputText = edt_search.getText().toString().toLowerCase();
            for (int i=0;i<AllLayout.images.size();i++){
                if (textHolder.get(i).contains(inputText)){
                    adapter.insert(AllLayout.images.get(i));
                }
            }
        }
        adapter.notifyDataSetChanged();
        Log.e("adasdasdas", String.valueOf(adapter.images.size()));
    }
    private void recognizeTextFromImage(Image img, int i){
        Bitmap bitmap = img.getImgBitmap();
        if (bitmap == null){
            Log.e("TextRecognition", "Bitmap is null.");
            return;
        }
        try {
            InputImage image = InputImage.fromBitmap(bitmap,0);
            Log.e("ssss", "-------------------------");
            Task<Text> textTaskResult = recognizer.process(image)
                    .addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {
                            String recognizedText = text.getText();
                            textHolder.set(i,recognizedText.toLowerCase());
                            Log.e("TextHolder", textHolder.get(i));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("That bai", e.toString());
                            Toast.makeText(main,"" +e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            Toast.makeText(main,"Failed due to:" +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void closeFragment(){
        FragmentTransaction transaction = main.getSupportFragmentManager().beginTransaction();
        main.getSupportFragmentManager().popBackStack();
        transaction.remove(this);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        transaction.commit();
    }
    @Override
    public void onStop(){
        super.onStop();
        main.allLayout.update();
    }
}