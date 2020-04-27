package com.mtc.mindbook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.mtc.mindbook.explore.NearFragment;
import com.mtc.mindbook.models.explore.SectionPageAdapter;
import com.mtc.mindbook.explore.ShareFragment;


public class LibFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_explore, container, false);
        update(rootView);

        AppBarLayout appbar = rootView.findViewById(R.id.appbar);
        appbar.setOutlineProvider(null);
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
        adapter.addFragment(new ShareFragment(), getString(R.string.community));
        adapter.addFragment(new NearFragment(), getString(R.string.nearby));
        viewPager.setAdapter(adapter);
    }

}
