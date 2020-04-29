package com.mtc.mindbook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity {
    public MaterialButton btnRegister = null;
    public TextInputLayout usernameFieldLayout = null;
    public TextInputEditText usernameField = null;
    public TextInputLayout firstnameFieldLayout = null;
    public TextInputEditText firstnameField = null;
    public TextInputLayout lastnameFieldLayout = null;
    public TextInputEditText lastnameField = null;
    public TextInputLayout passwordFieldLayout = null;
    public TextInputEditText emailField = null;
    public TextInputEditText passwordField = null;
    public TextInputLayout repasswordFieldLayout = null;
    public TextInputEditText repasswordField = null;
    APIService userService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
                    editor.commit();
                    setResult(RESULT_OK, intent);
                    finish();
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
