package com.smart.doorlock.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.smart.doorlock.R;
import com.smart.doorlock.control.NavigateManager;
import com.smart.doorlock.ui.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 15/7/25.
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tv_pwd)
    TextView tvPwd;
    @InjectView(R.id.tv_finger)
    TextView tvFinger;
    @InjectView(R.id.tv_system_params)
    TextView tvSystemParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        initToolBar();
        initListener();
    }

    private void initToolBar() {
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListener() {
        tvFinger.setOnClickListener(this);
        tvPwd.setOnClickListener(this);
        tvSystemParams.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_finger:
                NavigateManager.gotoZwListActivity(this);
                break;
            case R.id.tv_pwd:
                NavigateManager.gotoPwdListActivity(this);
                break;
            case R.id.tv_system_params:
                NavigateManager.gotoOtherSettingsActivity(this);
                break;
        }
    }
}
