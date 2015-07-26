package com.smart.doorlock.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.smart.doorlock.R;
import com.smart.doorlock.SmartLockApplication;
import com.smart.doorlock.ble.BleProto;
import com.smart.doorlock.ui.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class OtherSettingsActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = BleProto.DEBUG_TAG;

    private BluetoothLeService mBluetoothLeService = null;
    private ProgressDialog process_dialog = null;
    private boolean HANDLER_FLAG = true;

    int open_door_type = 1;
    int voice_stime = 22;
    int voice_etime = 6;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.type_pwd)
    RadioButton mButtonrPwd;
    @InjectView(R.id.type_zw)
    RadioButton mButtonrZw;
    @InjectView(R.id.type_zw_pwd)
    RadioButton mButtonrZwPwd;
    //@InjectView(R.id.clear_setting)
    //Button mButtonClearSet;
    @InjectView(R.id.voice_start_time)
    EditText mEditStart;
    @InjectView(R.id.voice_stop_time)
    EditText mEditStop;

    @InjectView(R.id.save_setting)
    Button mButtonSaveSet;


    @InjectView(R.id.open_door_type)
    RadioGroup mRadioGroupType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_settings);
        ButterKnife.inject(this);

        mBluetoothLeService = ((SmartLockApplication)getApplication()).getBleService();

        initData();
        initListener();

        registerReceiver(mSetDataReceiver, makeDataUpdateFilter());

        bleGetSystemSet();
        requestSetting(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSetDataReceiver);
    }

    private void initData() {
        initActionBar();
        mRadioGroupType.clearCheck();
    }

    private void initActionBar() {
        mToolbar.setTitle(getString(R.string.action_settings));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListener() {
        //mButtonClearSet.setOnClickListener(this);
        mButtonSaveSet.setOnClickListener(this);
        RadioGroup.OnCheckedChangeListener typelistener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                //Log.d(TAG, "onCheckedChanged  checkedId = " + checkedId);
                if (group != null && checkedId > -1) {

                    //Log.d(TAG, "onCheckedChanged11");
                    if (checkedId == mButtonrPwd.getId()) {
                        open_door_type = 1;
                        mButtonrZw.setChecked(false);
                        mButtonrZwPwd.setChecked(false);
                    } else if (checkedId == mButtonrZw.getId()) {
                        open_door_type = 2;
                        mButtonrZwPwd.setChecked(false);
                        mButtonrPwd.setChecked(false);
                    } else if (checkedId == mButtonrZwPwd.getId()) {
                        open_door_type = 3;
                        mButtonrZw.setChecked(false);
                        mButtonrPwd.setChecked(false);
                    }
                }
            }
        };
        mRadioGroupType.setOnCheckedChangeListener(typelistener);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            //case R.id.clear_setting:
            //    break;
            case R.id.save_setting:
                String stime = mEditStart.getText().toString();
                String etime = mEditStop.getText().toString();
                voice_stime = Integer.parseInt(stime);
                voice_etime = Integer.parseInt(etime);
                bleSetSystemSet();
                requestSetting(true);
                break;
            default:
                break;
        }
    }

    private void bleGetSystemSet(){
        mBluetoothLeService.bleSendData(BleProto.FormatDownLinkData(BleProto.PROTO_GET_SYSTEM_SETTING, null));
    }

    private void bleSetSystemSet(){
        byte[] set_data = new byte[3];
        set_data[0] = (byte)open_door_type;
        set_data[1] = (byte)voice_stime;
        set_data[2] = (byte)voice_etime;
        mBluetoothLeService.bleSendData(BleProto.FormatDownLinkData(BleProto.PROTO_SAVE_SYSTEM_SETTING, set_data));
    }

    private void requestSetting(final boolean is_write)
    {
        int process_res_id = R.string.reading_seting_set;

        if (is_write){
            process_res_id = R.string.modifying_seting_set;
        }
        process_dialog = new ProgressDialog(OtherSettingsActivity.this);
        process_dialog.setTitle(getResources().getString(
                R.string.basic_set_title));
        process_dialog.setMessage(
                "\n"
                        + getResources().getString(process_res_id)
                        + "\n"
                        + "\n"
                        + getResources().getString(R.string.alert_message_wait));
        process_dialog.show();
        HANDLER_FLAG = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HANDLER_FLAG) {
                    HANDLER_FLAG = false;
                    process_dialog.dismiss();
                    try {
                        Toast.makeText(OtherSettingsActivity.this,
                                is_write ? R.string.process_fail : R.string.reading_set_overtime,
                                Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        }, 3000);
    }

    private static IntentFilter makeDataUpdateFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BleProto.SET_PROTO_RSP_ACTION);
        return intentFilter;
    }

    private void updateProcessState(boolean is_read) {

        if (process_dialog != null && process_dialog.isShowing()) {
            process_dialog.dismiss();
        }
        HANDLER_FLAG = false;

        Toast.makeText(OtherSettingsActivity.this,
                is_read?R.string.reading_set_succ:R.string.process_succ,
                Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver mSetDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.e(TAG, "mSetDataReceiver  action = " + action);
            if (BleProto.SET_PROTO_RSP_ACTION.equals(action)) {

                byte[] rcv_data = intent.getByteArrayExtra(BleProto.PROTO_RSP_DATA);
                if (rcv_data != null) {
                    switch(rcv_data[BleProto.PROTO_CMD_POS]&0xff)
                    {
                        case BleProto.PROTO_CLEAR_SETTINGS:
                            updateProcessState(false);
                            break;
                        case BleProto.PROTO_GET_SYSTEM_SETTING:
                            open_door_type = rcv_data[BleProto.PROTO_RSP_VALID_DATA_POS];
                            voice_stime = rcv_data[BleProto.PROTO_RSP_VALID_DATA_POS+1];
                            voice_etime = rcv_data[BleProto.PROTO_RSP_VALID_DATA_POS+2];
                            if ((open_door_type == 0) || (open_door_type > 3))
                                open_door_type = 2;
                            if ((voice_stime > 23) || (voice_stime < 0))
                                voice_stime = 22;
                            if ((voice_etime > 23) || (voice_etime < 0))
                                voice_etime = 6;
                            if (open_door_type == 1)
                                mButtonrPwd.setChecked(true);
                            else if (open_door_type == 2)
                                mButtonrZw.setChecked(true);
                            else
                                mButtonrZwPwd.setChecked(true);

                            mEditStart.setText(Integer.toString(voice_stime));
                            mEditStart.setSelection(Integer.toString(voice_stime).length());
                            mEditStop.setText(Integer.toString(voice_etime));
                            mEditStop.setSelection(Integer.toString(voice_etime).length());
                            updateProcessState(true);
                            break;
                        case BleProto.PROTO_SAVE_SYSTEM_SETTING:
                            updateProcessState(false);
                            finish();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    };
}
