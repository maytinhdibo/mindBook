package com.mtc.mindbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.mtc.mindbook.models.responseObj.LoginResponseObj;
import com.mtc.mindbook.models.responseObj.user.getUser;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    public Button btnRegister = null;
    public EditText usernameField = null;
    public EditText firstnameField = null;
    public EditText lastnameField = null;
    public EditText emailField = null;
    public EditText passwordField = null;
    public EditText repasswordField = null;
    APIService userService = null;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setStatusBarColor(ContextCompat.getColor(this ,R.color.colorPrimary));


        btnRegister = findViewById(R.id.confirm_register_button);
        usernameField = findViewById(R.id.username_field_register);
        firstnameField = findViewById(R.id.firstname_field);
        lastnameField = findViewById(R.id.lastname_field);
        emailField = findViewById(R.id.email_field);
        passwordField = findViewById(R.id.password_field_register);
        repasswordField = findViewById(R.id.repassword_field_register);

        userService = APIUtils.getUserService();
    }

    public void onConfirmRegisterClicked(View view) {
        try {
            String username = usernameField.getText().toString();
            String firstname = firstnameField.getText().toString();
            String lasttname = lastnameField.getText().toString();
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();
            String repassword = repasswordField.getText().toString();
            if (validateRegister(username, firstname, lasttname, email, password, repassword)) {
                doRegister(username, firstname, lasttname, email, password);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean validateRegister(String username, String firstname, String lastname, String email, String password, String repassword) {
        if (username == null || username.trim().length() == 0) {
            Toast.makeText(this, "Username không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (firstname == null || firstname.trim().length() == 0) {
            Toast.makeText(this, "First name không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (lastname == null || lastname.trim().length() == 0) {
            Toast.makeText(this, "Last name không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!email.matches("[^@]+@[^\\.]+\\..+")) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password == null || password.trim().length() == 0) {
            Toast.makeText(this, "Password không được để trống", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (repassword == null || !repassword.equals(password)) {
            Toast.makeText(this, "Xác nhận mật khẩu không trùng khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    void doRegister(String username, String firstname, String lastname, String email, String password) {
        Call<LoginResponseObj> callRegister = userService.register(username, firstname, lastname, email, password);
        callRegister.enqueue(new Callback<LoginResponseObj>() {
            @Override
            public void onResponse(Call<LoginResponseObj> call, Response<LoginResponseObj> response) {
                if (response.message().equals("OK")) {
                    String accessToken = response.body().getAccessToken();
                    Log.d("auth_from_register", "accessToken: " + accessToken);
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("accessToken", accessToken);
                    SharedPreferences sharedPrefs = getSharedPreferences("userDataPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("accessToken", accessToken);
                    editor.apply();
                    getUser fetchUser = new getUser(context);
                } else {
                    Toast.makeText(RegisterActivity.this, "Username hoặc Email đã tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponseObj> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Hãy kiểm tra kết nối mạng của bạn", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
