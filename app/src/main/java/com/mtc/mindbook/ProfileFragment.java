package com.mtc.mindbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {
    Button goToBookShelf = null;
    TextView userFullNameTextView = null;
    TextView userEmailTextView = null;
    SharedPreferences sharedPrefs = null;
    ImageView logout = null;
    LinearLayout userInfoWrapper = null;
    LinearLayout enableLight = null;
    LinearLayout enableDark = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        rootView.findViewById(R.id.title_bar).setPaddingRelative(0, getResources().getDimensionPixelSize(
                getResources().getIdentifier("status_bar_height", "dimen", "android")
        ), 0, 0);

        goToBookShelf = rootView.findViewById(R.id.btn_bookshelf);
        userFullNameTextView = rootView.findViewById(R.id.textview_profile_user_full_name);
        userEmailTextView = rootView.findViewById(R.id.textview_profile_email);
        logout = rootView.findViewById(R.id.log_out);
        userInfoWrapper = rootView.findViewById(R.id.user_info_wrapper);
        enableDark = rootView.findViewById(R.id.enable_night_theme);
        enableLight = rootView.findViewById(R.id.enable_light_theme);
        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sharedPrefs.getBoolean("isLoggedIn", false)) {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("isLoggedIn", false);
                    editor.commit();
                    userInfoWrapper.setGravity(Gravity.CENTER);
                    userFullNameTextView.setText("Đăng nhập");
                    userEmailTextView.setVisibility(View.GONE);
                    logout.setVisibility(View.GONE);
                    userInfoWrapper.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!sharedPrefs.getBoolean("isLoggedIn", false)) {
                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                startActivityForResult(intent, 1);
                            }
                        }
                    });
                }
            }
        });
        goToBookShelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BookShelfActivity.class);
                startActivity(intent);
            }
        });
        enableLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
        enableDark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_UNSPECIFIED) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
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
            userFullNameTextView.setText(sharedPrefs.getString("userFullName", ""));
            userEmailTextView.setText(sharedPrefs.getString("userEmail", ""));
            userFullNameTextView.setVisibility(View.VISIBLE);
            userEmailTextView.setVisibility(View.VISIBLE);
            userInfoWrapper.setGravity(Gravity.LEFT);
            logout.setVisibility(View.VISIBLE);
        } else {
            userInfoWrapper.setGravity(Gravity.CENTER);
            userFullNameTextView.setText("Đăng nhập");
            userEmailTextView.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
            userInfoWrapper.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!sharedPrefs.getBoolean("isLoggedIn", false)) {
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1){
            if (resultCode != 0) {
                userFullNameTextView.setText(sharedPrefs.getString("userFullName", ""));
                userEmailTextView.setText(sharedPrefs.getString("userEmail", ""));
                userFullNameTextView.setVisibility(View.VISIBLE);
                userEmailTextView.setVisibility(View.VISIBLE);
            }
        }
    }



}
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.add(R.id.profile_main_frame, new LoginFragment());
//                transaction.addToBackStack(null);
//                transaction.commit();