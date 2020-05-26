package com.mtc.mindbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mtc.mindbook.models.responseObj.LoginResponseObj;
import com.mtc.mindbook.models.responseObj.user.getUser;
import com.mtc.mindbook.remote.APIService;
import com.mtc.mindbook.remote.APIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public Button btnLogin = null;
    public TextView btnRegister = null;
    public EditText fieldName = null;
    public EditText fieldPassword = null;
    public TextInputLayout fieldNameLayout = null;
    public TextInputLayout fieldPasswordLayout = null;
    APIService userService = null;
    Context context = this;

    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private View mChildOfContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.login_button);
        btnRegister = findViewById(R.id.register_button);
        fieldName = findViewById(R.id.name_field);
        fieldPassword = findViewById(R.id.password_field);
        userService = APIUtils.getUserService();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        OnBackPressedCallback backWithoutLogin = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        };
        this.getOnBackPressedDispatcher().addCallback(this, backWithoutLogin);

        FrameLayout content = (FrameLayout) findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();

    }


    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
            } else {
                // keyboard probably just became hidden
                frameLayoutParams.height = usableHeightSansKeyboard-2*getResources().getDimensionPixelSize(
                        getResources().getIdentifier("status_bar_height", "dimen", "android")
                );
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top + getResources().getDimensionPixelSize(getResources().getIdentifier("status_bar_height", "dimen", "android")));
    }

    public void onLoginClicked(View view) {
        try {
            String userName = fieldName.getText().toString();
            String password = fieldPassword.getText().toString();
            if (validateInput(userName, password)) {
                doLogin(userName, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRegisterClicked(View view) {
        Intent intentRegister = new Intent(this, RegisterActivity.class);
        startActivityForResult(intentRegister, 1);
    }

    public boolean validateInput(String userName, String password) {
        if (userName == null || userName.trim().length() == 0) {
            fieldNameLayout.setError("Username không được để trống");
            return false;
        }
        fieldNameLayout.setError(null);
        if (password == null || password.trim().length() == 0) {
            fieldPasswordLayout.setError("Password không được để trống");
            return false;
        }
        fieldPasswordLayout.setError(null);
        return true;
    }

    public void doLogin(String userName, String password) {
        Call<LoginResponseObj> callLogin = userService.login(userName, password);
        callLogin.enqueue(new Callback<LoginResponseObj>() {
            @Override
            public void onResponse(Call<LoginResponseObj> call, Response<LoginResponseObj> response) {
                if (response.message().equals("OK")) {
                    String accessToken = response.body().getAccessToken();
                    Log.d("auth_from_login", "accessToken: " + accessToken);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    SharedPreferences sharedPrefs = getSharedPreferences("userDataPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("accessToken", accessToken);
                    editor.apply();
                    getUser fetchUser = new getUser(context);
                } else {
                    Toast.makeText(LoginActivity.this, "Tài khoản hoặc mật khẩu sai", Toast.LENGTH_SHORT).show();
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
