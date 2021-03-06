package com.mohkamfer.taskapp;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.mohkamfer.taskapp.ui.ReceiveFragment;
import com.mohkamfer.taskapp.ui.SendFragment;
import com.mohkamfer.taskapp.util.ConnectionManager;

@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    private ConnectionManager mManager;

    private SendFragment mSendFragment;
    private ReceiveFragment mReceiveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mSendFragment = SendFragment.newInstance();
        mReceiveFragment = ReceiveFragment.newInstance();

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mManager = new ConnectionManager(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mManager.onDestroy();
    }

    public void publishMessage(String topic, String message) {
        if (mManager.isConnected())
            mManager.publish(topic, message);
    }

    public void updateSendStatus() {
        mSendFragment.updateStatus();
    }

    public void appendToBody(String message) {
        mReceiveFragment.append(message);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return mSendFragment;
                case 1:
                    return mReceiveFragment;
                default:
                    throw new UnsupportedOperationException("Error position " + position);
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
