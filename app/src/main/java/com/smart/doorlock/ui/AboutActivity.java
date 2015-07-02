package com.smart.doorlock.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.smart.lock.R;
import com.smart.doorlock.ui.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class AboutActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    private boolean isShowGifView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        initActionBar();
    }

    private void initActionBar() {
        mToolbar.setTitle(getString(R.string.about));
        mToolbar.setSubtitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
