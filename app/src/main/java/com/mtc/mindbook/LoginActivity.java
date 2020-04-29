package com.mtc.mindbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mtc.mindbook.models.responseObj.LoginResponseObj;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public MaterialButton btnLogin=null;
    public MaterialButton btnRegister = null;
    public TextInputEditText fieldName=null;
    public TextInputEditText fieldPassword=null;
    public TextInputLayout fieldNameLayout = null;
    public TextInputLayout fieldPasswordLayout = null;
    APIService userService = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.login_button);
        btnRegister = findViewById(R.id.register_button);
        fieldName = findViewById(R.id.name_field);
        fieldNameLayout = findViewById(R.id.name_fieldL);
        fieldPassword = findViewById(R.id.password_field);
        fieldPasswordLayout = findViewById(R.id.password_fieldL);
        userService =APIUtils.getUserService();
        OnBackPressedCallback backWithoutLogin = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, backWithoutLogin);

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

    public void onRegisterClicked(View view) {
        Intent intentRegister = new Intent(this, RegisterActivity.class);
        startActivityForResult(intentRegister, 1);
    }
    public boolean validateInput(String userName, String password){
        if(userName == null || userName.trim().length() == 0){
            fieldNameLayout.setError("Username không được để trống");
            return false;
        }
        fieldNameLayout.setError(null);
        if(password == null || password.trim().length() == 0){
            fieldPasswordLayout.setError("Password không được để trống");
            return false;
        }
        fieldPasswordLayout.setError(null);
        return true;
    }
    public void doLogin(String userName, String password){
        Call<LoginResponseObj> callLogin = userService.login(userName,password);
        callLogin.enqueue(new Callback<LoginResponseObj>() {
            @Override
            public void onResponse(Call<LoginResponseObj> call, Response<LoginResponseObj> response) {
                if (response.message().equals("OK")){
                    String accessToken = response.body().getAccessToken();
                    Log.d("auth_from_login", "accessToken: " + accessToken);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != 0) {
            String reply = data.getStringExtra("accessToken");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("accessToken", reply);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
