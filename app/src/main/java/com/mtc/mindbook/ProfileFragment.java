package com.mtc.mindbook;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {
    TextView message = null;
    Button goToLoginBtn = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        goToLoginBtn = rootView.findViewById(R.id.button_go_to_login);
        message = rootView.findViewById(R.id.profile_message);
        if (savedInstanceState != null) {
            boolean isLogin = savedInstanceState.getBoolean("isLogin");
            if (isLogin) {
                goToLoginBtn.setVisibility(View.INVISIBLE);
            }
        }
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
        if (requestCode == 1){
            if (resultCode != 0) {
                String reply = data.getStringExtra("accessToken");
                Toast.makeText(getActivity(), reply, Toast.LENGTH_SHORT).show();
                message.setText(reply);
                goToLoginBtn.setVisibility(View.INVISIBLE);
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