package com.smart.doorlock.ui;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.smart.doorlock.control.NavigateManager;
import com.smart.doorlock.ui.base.BaseActivity;
import com.smart.doorlock.widget.ActionSheet;
import com.smart.lock.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class MainActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.gv_coding)
    GifView gvCoding;
    @InjectView(R.id.ll_company_info)
    RelativeLayout llCompanyInfo;
    @InjectView(R.id.tv_pwd_setting)
    TextView tvPwdSetting;
    @InjectView(R.id.tv_finger_setting)
    TextView tvFingerSetting;
    @InjectView(R.id.tv_other_setting)
    TextView tvOtherSetting;
    @InjectView(R.id.tv_version_right)
    LinearLayout tvVersionRight;
    @InjectView(R.id.drawer_view)
    RelativeLayout drawerView;
    @InjectView(R.id.drawer)
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initData();
        initView();
        initListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                aboutSoftwareDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void aboutSoftwareDialog() {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(getString(R.string.cancel))
                .setOtherButtonTitles(getString(R.string.about), getString(R.string.exit))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0:
                                NavigateManager.gotoAboutActivity(MainActivity.this);
                                break;
                            case 1:
                                finish();
                                break;
                        }
                    }
                }).show();
    }

    private void initData() {
        initActionBar();
    }

    private void initActionBar() {
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        mDrawerToggle.syncState();
        drawer.setDrawerListener(mDrawerToggle);
    }

    private void initView() {
        gvCoding.setGifImage(R.drawable.gif_robot_walk);
    }

    private void initListener() {
        drawerView.setOnTouchListener(this);
        tvPwdSetting.setOnClickListener(this);
        tvFingerSetting.setOnClickListener(this);
        tvOtherSetting.setOnClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_pwd_setting:
                NavigateManager.gotoPwdListActivity(this);
                break;
            case R.id.tv_finger_setting:
                NavigateManager.gotoPwdListActivity(this);
                break;
            case R.id.tv_other_setting:
                NavigateManager.gotoPwdListActivity(this);
                break;
        }
    }

}
