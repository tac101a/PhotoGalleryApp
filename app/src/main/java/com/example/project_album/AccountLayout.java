package com.example.project_album;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AccountLayout extends Fragment {
    MainActivity main;
    Context context = null;
    EditText editNickname;
    EditText editPassword;
    EditText editNewPassword;
    EditText editRetypeNewPassword;
    Button btnSaveChange;
    Button btnLogout;
    ScrollView mainLayout;
    TextView labelNewPassword, labelRetypeNewPassword, labelPassword, labelNickname;
    ArrayList<String> accountInfo;
    public AccountLayout() {
    }

    public static AccountLayout newInstance(String strArg) {
        AccountLayout fragment = new AccountLayout();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_layout, container, false);
        mainLayout = view.findViewById(R.id.accountLayout);
        editNickname = view.findViewById(R.id.editAccountNickname);
        editPassword = view.findViewById(R.id.editAccountPassword);
        editNewPassword = view.findViewById(R.id.editNewPassword);
        editRetypeNewPassword = view.findViewById(R.id.editRetypeNewPassword);
        btnSaveChange = view.findViewById(R.id.btnEditAccount);
        btnLogout = view.findViewById(R.id.btnLogout);

        labelNewPassword = view.findViewById(R.id.labelNewPassword);
        labelRetypeNewPassword = view.findViewById(R.id.labelRetypeNewPassword);
        labelPassword = view.findViewById(R.id.labelPassword);
        labelNickname = view.findViewById(R.id.labelNickname);

        setTheme(main.mainColorBackground, main.mainColorText);

        editNickname.setText(main.username);
        editNickname.setEnabled(false);
        editPassword.setText("");
        editNewPassword.setText("");
        editRetypeNewPassword.setText("");

        btnSaveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPassword.getText().toString().compareTo(main.password)!=0) {
                    Toast.makeText(main, "Wrong password!", Toast.LENGTH_SHORT).show();
                }
                else if (editNewPassword.getText().toString().compareTo(editRetypeNewPassword.getText().toString())!=0) {
                    Toast.makeText(main, "New password and retyped new password are not matched!", Toast.LENGTH_SHORT).show();
                }
                else if(editNewPassword.getText().toString().equals("")){
                    Toast.makeText(main, "password không được để trống", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(main);
                    alertDialogBuilder.setMessage("Are you sure you want to save the changes?");
                    alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newPassword = editNewPassword.getText().toString();
                            Map<String,Object> map = new HashMap<>();
                            map.put("password",newPassword);
                            FirebaseFirestore fbf = FirebaseFirestore.getInstance();
                            fbf.collection("user").document(main.userkey).update(map);
                            editPassword.setText("");
                            editNewPassword.setText("");
                            editRetypeNewPassword.setText("");
                            Toast.makeText(main, "Update successfully!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main.albumLayout.isInit = false;
                MainActivity.images.clear();
                AlbumLayout.albums.clear();
                MainActivity.dataResource.clearTable();
                SharedPreferences myPrefContainer = main.getSharedPreferences(
                        "user", Activity.MODE_PRIVATE);
                myPrefContainer.edit().clear().commit();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                main.finish();
            }
        });

        return view;
    }

    private void setTheme(int backgroundColor, ColorStateList textColor) {
        setThemeBackGround(backgroundColor);
        setThemeText(textColor);
    }

    private void setThemeBackGround(int backgroundColor) {
        mainLayout.setBackgroundColor(backgroundColor);
    }

    private void setThemeText(ColorStateList textColor) {
        labelNewPassword.setTextColor(textColor);
        labelRetypeNewPassword.setTextColor(textColor);
        labelPassword.setTextColor(textColor);
        labelNickname.setTextColor(textColor);
    }
}