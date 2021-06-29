package com.dalo.app.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.ImageLoader;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.dalo.app.R;

import com.dalo.app.fragment.LoginActivityFragment;

import com.dalo.app.fragment.SignUpFragment;
import com.dalo.app.helper.AppController;
import com.dalo.app.helper.CircleImageView;
import com.dalo.app.helper.Session;

import java.util.ArrayList;
import java.util.List;


public class LoginTabActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private TabLayout tabLayout;
    Toolbar toolbar;
    public static TextView coins;
    static Button redeem;
    public static TextView score, coin, tvName;
    public static int txtcoin = 0;
    private CircleImageView imgProfile;
    public static ViewPagerAdapter adapter;
    private String[] tabs;
    Boolean firstTime = null;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    public static FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tablogin);
        // transparentStatusAndNavigation(Wallet.this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tabs = new String[]{getString(R.string.act_login), getString(R.string.new_act)};
        viewPager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (Session.isLogin(LoginTabActivity.this)) {
            Intent intent = new Intent(LoginTabActivity.this, MainActivity.class);
            intent.putExtra("type", "default");
            startActivity(intent);
            finish();

        }
        /*tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);*/
        tabLayout.setupWithViewPager(viewPager);
        mAuth = FirebaseAuth.getInstance();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //setUpCustomTabs();

/*        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View selected = tab.getCustomView();
                TextView iv_text = (TextView) selected.findViewById(R.id.custom_tab_label);
                iv_text.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                iv_text.setBackground(getResources().getDrawable(R.drawable.loginbtn));
                iv_text.setPadding(15, 20, 15, 20);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View selected = tab.getCustomView();
                TextView iv_text = (TextView) selected.findViewById(R.id.custom_tab_label);
                iv_text.setTextColor(getResources().getColor(R.color.white));
                iv_text.setBackground(getResources().getDrawable(R.drawable.loginunselectbtn));
                iv_text.setPadding(15, 20, 15, 20);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });*/


    }


    private void setUpCustomTabs() {
        for (int i = 0; i < tabs.length; i++) {
            TextView customTab = (TextView) LayoutInflater.from(this).inflate(R.layout.custome_logintab, null);//get custom view

            customTab.setText(tabs[i]);//set text over view
            if (i == 0) {
                //   customTab.setBackground(getResources().getDrawable(R.drawable.loginbtn));


                customTab.setTextSize(16);
                customTab.setTypeface(customTab.getTypeface(), Typeface.BOLD);
                customTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                customTab.setPadding(15, 20, 15, 20);

            } else {
                /*customTab.setBackground(getResources().getDrawable(R.drawable.loginunselectbtn));*/

                customTab.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                customTab.setPadding(15, 20, 15, 20);
            }
            TabLayout.Tab tab = tabLayout.getTabAt(i);//get tab via position
            if (tab != null)
                tab.setCustomView(customTab);//set custom view
        }
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new LoginActivityFragment(), tabs[0]);
        adapter.addFrag(new SignUpFragment(), tabs[1]);
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
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
