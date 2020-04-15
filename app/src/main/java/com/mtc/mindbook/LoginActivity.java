package com.mtc.mindbook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mtc.mindbook.model.responseObj.LoginResponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public MaterialButton btnLogin=null;
    public TextInputEditText fieldName=null;
    public TextInputEditText fieldPassword=null;
    APIService userService = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.login_button);
        fieldName = findViewById(R.id.name_field);
        fieldPassword = findViewById(R.id.password_field);
        userService =APIUtils.getUserService();
    }

    public void onLoginClicked(View view) {
        try{
            String userName = fieldName.getText().toString();
            String password = fieldPassword.getText().toString();
            if (validateInput(userName,password)){
                doLogin(userName,password);
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }
    public boolean validateInput(String userName, String password){
        if(userName == null || userName.trim().length() == 0){
            Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password == null || password.trim().length() == 0){
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    public void doLogin(String userName, String password){
        Call<LoginResponseObj> callLogin = userService.login(userName,password);
        callLogin.enqueue(new Callback<LoginResponseObj>() {
            @Override
            public void onResponse(Call<LoginResponseObj> call, Response<LoginResponseObj> response) {
                if (response.message().equals("OK")){
                    String accessToken = response.body().getAccessToken();
                    Log.d("auth", "accessToken: " + accessToken);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("accessToken", accessToken);
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khâu sai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseObj> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Hãy kiểm tra kết nối mạng của bạn", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
