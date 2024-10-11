package com.example.project_album;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;


public class SignUpFragment extends Fragment {

    LoginActivity main;
    Context context = null;
    String username,password,phone,email;

    EditText editUserName, editPhone,editPass,editPass2, editEmail;

    Button btnSignUp;
    public SignUpFragment() {
        // Required empty public constructor
    }


    public static SignUpFragment newInstance(String param1) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        args.putString("strArg1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main = (LoginActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_sign_up, container, false);
        editEmail=view.findViewById(R.id.editEmail);
        editPass=view.findViewById(R.id.editPassword);
        editPass2=view.findViewById(R.id.editPassword2  );
        editUserName=view.findViewById(R.id.editUserName);
        editPhone=view.findViewById(R.id.editPhone);
        btnSignUp=view.findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username =editUserName.getText().toString();
                password = editPass.getText().toString();
                phone = editPhone.getText().toString();
                email= editEmail.getText().toString();
                if (email.equals("") || username.equals("")||password.equals("") ||
                editPass2.getText().toString().equals("")|| phone.equals("")){
                    Toast.makeText(main,"Bạn chưa nhập đầy đủ thông tin",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.compareTo(editPass2.getText().toString())!=0){
                    Toast.makeText(main, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                }
                else{
                    if (!editUserName.getText().toString().matches("^[a-zA-Z][a-zA-Z0-9_]{7,}$")) {
                        Toast.makeText(main, "Username có ít nhất 8 kí tự và bắt đầu bằng chữ", Toast.LENGTH_SHORT).show();
                    } else if (editPass.getText().toString().length() < 8 || !editPass.getText().toString().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")) {
                        Toast.makeText(main, "Mật khẩu phải có ít nhất 8 ký tự, bao gồm ít nhất một chữ hoa, một chữ thường và một ký tự đặc biệt", Toast.LENGTH_SHORT).show();
                    } else if (!editPass.getText().toString().equals(editPass2.getText().toString())) {
                        Toast.makeText(main, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show();
                    } else if (!editEmail.getText().toString().matches("^[a-zA-Z0-9]+@gmail\\.com$")) {
                        Toast.makeText(main, "Email phải có định dạng đuôi @gmail.com", Toast.LENGTH_SHORT).show();
                    } else if (!editPhone.getText().toString().matches("^[0-9]+$")) {
                        // Phone validation failed
                        Toast.makeText(main, "Số điện thoại chỉ được chứa số", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        User user = DataFirebase.checkUser(editUserName.getText().toString(),
                                editPass.getText().toString());
                        if (user == null) {
                            DataFirebase.UploadUser newupload = new DataFirebase.UploadUser(
                                    username, password, email, phone);
                            // khong ton tai user trong database =>dc phep dang ky
                            FirebaseFirestore fbf = FirebaseFirestore.getInstance();

                            //tim kiem next key cho user moi
                            fbf.collection("user").get().addOnCompleteListener(
                            new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                String newkey = "0";
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    newkey = document.getId();
                                }
                                newkey = String.valueOf(Integer.parseInt(newkey) + 1);
                                newupload.setKey(newkey);
                                fbf.collection("user").document(newkey).set(newupload);
                                Toast.makeText(main, "Tài khoản đã được tạo thành công",
                                        Toast.LENGTH_SHORT).show();
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                ft.replace(R.id.login_container, new LoginFragment());
                                ft.commit();
                            }
                            });
                        } else {
                            Toast.makeText(main, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
        return view;
    }
}