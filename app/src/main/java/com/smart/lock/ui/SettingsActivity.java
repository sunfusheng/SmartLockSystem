package com.smart.lock.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.smart.lock.R;
import com.smart.lock.ui.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class SettingsActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        initData();
        initListener();
    }

    private void initData() {
        initActionBar();
    }

    private void initActionBar() {
        mToolbar.setTitle(getString(R.string.action_settings));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListener() {

    }

}
