package com.dalo.app.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.dalo.app.Constant;
import com.dalo.app.R;
import com.dalo.app.fragment.TournamenttFragment;

import java.util.ArrayList;
import java.util.List;


public class ContestActivity extends AppCompatActivity {

    Toolbar toolbar;
    public static String tab1 = "Past", tab2 = "Live", tab3 = "Upcoming";
    private String[] tabs = {tab1, tab2, tab3};
    private ViewPager viewPager;
    private TabLayout tabLayout;
    ViewPagerAdapter adapter;
    public TextView cointxt, title;
    public ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contest);
 /*       toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.contest));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);*/
        cointxt = findViewById(R.id.coinstxt);
        back = findViewById(R.id.back);
        title = findViewById(R.id.titletxt);
        title.setText(getString(R.string.contest));

        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        cointxt.setText("" + Constant.TOTAL_COINS);

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TournamenttFragment(), tabs[0]);
        adapter.addFrag(new TournamenttFragment(), tabs[1]);
        adapter.addFrag(new TournamenttFragment(), tabs[2]);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            TournamenttFragment fragment = new TournamenttFragment();

            data.putString("current_page", mFragmentTitleList.get(position));
            fragment.setArguments(data);
            return fragment;
            //return mFragmentList.get(position);
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
