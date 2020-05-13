package com.mtc.mindbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {
    Button goToLoginBtn = null;
    Button logoutBtn = null;
    TextView userFullNameTextView = null;
    TextView userEmailTextView = null;
    TextView userNameTextView = null;
    SharedPreferences sharedPrefs = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        goToLoginBtn = rootView.findViewById(R.id.button_go_to_login);
        logoutBtn = rootView.findViewById(R.id.btn_logout);
        userFullNameTextView = rootView.findViewById(R.id.textview_profile_user_full_name);
        userEmailTextView = rootView.findViewById(R.id.textview_profile_email);
        userNameTextView = rootView.findViewById(R.id.textview_profile_username);

        if (savedInstanceState != null) {
            boolean isLogin = savedInstanceState.getBoolean("isLogin");
            if (isLogin) {
                goToLoginBtn.setVisibility(View.INVISIBLE);
            }
        }
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sharedPrefs.getBoolean("isLoggedIn", false)) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                } else {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.commit();
                    logoutBtn.setText("Đăng nhập");
                    userFullNameTextView.setVisibility(View.GONE);
                    userEmailTextView.setVisibility(View.GONE);
                    userNameTextView.setVisibility(View.GONE);
                }
            }
        });
        goToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent,1);
            }
        });
        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();
        sharedPrefs = getActivity().getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);
        Boolean isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false);
        if (isLoggedIn) {
            Log.d("ấ", sharedPrefs.getString("userFullName", ""));
            logoutBtn.setText("Đăng xuất");
            userFullNameTextView.setText(sharedPrefs.getString("userFullName", ""));
            userEmailTextView.setText(sharedPrefs.getString("userEmail", ""));
            userNameTextView.setText(sharedPrefs.getString("userName", ""));
            userFullNameTextView.setVisibility(View.VISIBLE);
            userEmailTextView.setVisibility(View.VISIBLE);
            userNameTextView.setVisibility(View.VISIBLE);
        } else {
            logoutBtn.setText("Đăng nhập");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode != 0) {
                goToLoginBtn.setVisibility(View.INVISIBLE);
                logoutBtn.setText("Đăng xuất");
                userFullNameTextView.setText(sharedPrefs.getString("userFullName", ""));
                userEmailTextView.setText(sharedPrefs.getString("userEmail", ""));
                userNameTextView.setText(sharedPrefs.getString("userName", ""));
                userFullNameTextView.setVisibility(View.VISIBLE);
                userEmailTextView.setVisibility(View.VISIBLE);
                userNameTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (goToLoginBtn.getVisibility() == View.INVISIBLE) {
            outState.putBoolean("isLogin", true);
        }
    }
}
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.add(R.id.profile_main_frame, new LoginFragment());
//                transaction.addToBackStack(null);
//                transaction.commit();