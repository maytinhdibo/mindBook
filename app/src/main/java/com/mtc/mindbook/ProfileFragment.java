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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {
    Button goToLoginBtn = null;
    Button logoutBtn = null;
    SharedPreferences sharedPrefs = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        goToLoginBtn = rootView.findViewById(R.id.button_go_to_login);
        logoutBtn = rootView.findViewById(R.id.btn_logout);
        sharedPrefs = getActivity().getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);
        if (sharedPrefs.getBoolean("isLoggedIn", false)) {
            logoutBtn.setText("Đăng xuất");
            Log.d("token", sharedPrefs.getString("accessToken", ""));
        } else {
            logoutBtn.setText("Đăng nhập");
        }
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("dd", "onActivityResult: ");
        if (requestCode == 1){
            if (resultCode != 0) {
                goToLoginBtn.setVisibility(View.INVISIBLE);
                logoutBtn.setText("Đăng xuất");
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("dd", "onCreateView:");
        if (goToLoginBtn.getVisibility() == View.INVISIBLE) {
            outState.putBoolean("isLogin", true);
        }
    }
}
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.add(R.id.profile_main_frame, new LoginFragment());
//                transaction.addToBackStack(null);
//                transaction.commit();