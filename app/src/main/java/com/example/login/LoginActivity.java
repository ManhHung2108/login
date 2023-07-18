package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    EditText loginUserName, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Ánh xạ view
        loginUserName = findViewById(R.id.login_userName);
        loginPassword = findViewById(R.id.login_passWord);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginButton = findViewById(R.id.login_button);

        //sự kiện click login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Nếu 1 trong 2 sai
                if(!validateUserName() | !validatePassWord()){

                }
                else {
                    checkUser();
                }
            }
        });

        //sự kiện chuyển sang trang đăng ký
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateUserName(){
        String val = loginUserName.getText().toString().trim();
        if(val.isEmpty()){
            loginUserName.setError("Tài khoản không được để trống");
            return  false;
        }
        else  {
            loginUserName.setError(null);
            return  true;
        }
    }

    public Boolean validatePassWord(){
        String val = loginPassword.getText().toString().trim();
        if(val.isEmpty()){
            loginPassword.setError("Mật khẩu không được để trống");
            return  false;
        }
        else  {
            loginPassword.setError(null);
            return  true;
        }
    }

    public  void checkUser(){
        String useUserName = loginUserName.getText().toString().trim();
        String usePassWord = loginPassword.getText().toString().trim();

        //Tìm kiếm trong danh sách trường có giá trị là userName nhập vào
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(useUserName);

        //Hàm lắng nghe sự kiện thay đổi dữ liệu từ cơ sở dữ liệu FireBase
        //- Khác với addValueEventListener (được sử dụng để lắng nghe sự kiện thay đổi liên tục),
        //addListenerForSingleValueEvent chỉ lắng nghe sự kiện một lần duy nhất và sau đó ngừng lắng nghe.
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Xử lý dữ liệu ở đây khi dữ liệu đã được tải về thành công từ Firebase
                // Ví dụ: lấy giá trị của các nút con, phân tích dữ liệu, hiển thị lên giao diện người dùng, vv.

                if(snapshot.exists()){
                    loginUserName.setError(null);
                    //nếu tài khoản tồn tại lấy ra mk để check
                    String passwordFromDB = snapshot.child(useUserName).child("password").getValue(String.class);

                    if(passwordFromDB.equals(usePassWord)){
                        loginUserName.setError(null);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        loginPassword.setError("Mật khẩu chưa chính xác");
                        loginPassword.requestFocus();
                    }
                }
                else {
                    loginUserName.setError("Tài khoản không tồn tại");
                    loginUserName.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}