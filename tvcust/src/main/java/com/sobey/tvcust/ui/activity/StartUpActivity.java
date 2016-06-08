package com.sobey.tvcust.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sobey.common.view.DotView;
import com.sobey.tvcust.R;
import com.sobey.tvcust.ui.fragment.StartUpFragment;

public class StartUpActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private DotView dotView;

    private int[] srcs = new int[]{R.drawable.test,R.drawable.test,R.drawable.test,R.drawable.test};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        
        initView();
        initData();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.startup_viewpager);
        dotView = (DotView) findViewById(R.id.startup_dot);
    }

    private void initData() {
        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        dotView.setViewPager(viewPager);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "title";
        }

        @Override
        public int getCount() {
            return srcs.length;
        }

        @Override
        public Fragment getItem(int position) {
            return StartUpFragment.newInstance(srcs[position], position == srcs.length-1);
        }
    }

    public void onGo(View v){
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
