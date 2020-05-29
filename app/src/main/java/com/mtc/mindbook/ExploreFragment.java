package com.mtc.mindbook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mtc.mindbook.explore.NearFragment;
import com.mtc.mindbook.models.explore.SectionPageAdapter;
import com.mtc.mindbook.explore.ShareFragment;


public class ExploreFragment extends Fragment {
    SharedPreferences sharedPrefs = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        sharedPrefs = getActivity().getSharedPreferences("userDataPrefs", Context.MODE_PRIVATE);
        Boolean isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false);

        View rootView = null;
        if (isLoggedIn) {
            rootView = inflater.inflate(R.layout.activity_explore, container, false);
            update(rootView);

            AppBarLayout appbar = rootView.findViewById(R.id.appbar);
            rootView.findViewById(R.id.title_bar).setPaddingRelative(0, getResources().getDimensionPixelSize(
                    getResources().getIdentifier("status_bar_height", "dimen", "android")
            ), 0, 0);
            appbar.setOutlineProvider(null);
        } else {
            rootView = inflater.inflate(R.layout.activity_explore_guess, container, false);
            Button openLoginBtn = rootView.findViewById(R.id.open_login);
            openLoginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), LoginActivity.class);
                    startActivityForResult(intent, 1);
                }
            });
        }
        return rootView;
    }

    private void update(View v) {
        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager());
        adapter.addFragment(new ShareFragment(), getString(R.string.community));
        adapter.addFragment(new NearFragment(), getString(R.string.nearby));
        viewPager.setAdapter(adapter);
    }

}
