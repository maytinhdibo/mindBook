package com.mtc.mindbook;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.mtc.mindbook.explore.NearFragment;
import com.mtc.mindbook.models.explore.SectionPageAdapter;
import com.mtc.mindbook.explore.ShareFragment;


public class LibFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_lib, container, false);
        update(rootView);
        return rootView;
    }

    private void update(View v)
    {
        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager());
        adapter.addFragment(new ShareFragment(), "Share");
        adapter.addFragment(new NearFragment(), "Near");
        viewPager.setAdapter(adapter);
    }

}
