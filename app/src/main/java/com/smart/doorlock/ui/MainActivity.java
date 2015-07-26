package com.smart.doorlock.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.smart.doorlock.R;
import com.smart.doorlock.SmartLockApplication;
import com.smart.doorlock.ble.BleProto;
import com.smart.doorlock.control.NavigateManager;
import com.smart.doorlock.db.LogSqliteDB;
import com.smart.doorlock.entity.LogEntity;
import com.smart.doorlock.ui.base.BaseActivity;
import com.smart.doorlock.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by sunfusheng on 2015/6/25.
 */
@SuppressLint("NewApi")
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final String TAG = BleProto.DEBUG_TAG;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String READ_UUID = "0000caa2-0000-1000-8000-00805f9b34fb";
    private final static String WRITE_UUID = "0000caa2-0000-1000-8000-00805f9b34fb";
    public static String CAPSENSE_SLIDER = "0000cab2-0000-1000-8000-00805f9b34fb";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.ll_connect_device)
    LinearLayout llConnectDevice;
    @InjectView(R.id.ll_open_log)
    LinearLayout llOpenLog;
    @InjectView(R.id.ll_settings)
    LinearLayout llSettings;
    @InjectView(R.id.ll_first_row)
    LinearLayout llFirstRow;
    @InjectView(R.id.ll_help)
    LinearLayout llHelp;
    @InjectView(R.id.ll_second_row)
    LinearLayout llSecondRow;

    private Handler mHandler;
    private BluetoothGattCharacteristic readGatt, writeGatt;
    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private SmartLockApplication application;
    private ProgressDialog connect_dialog;
    private boolean HANDLER_FLAG = true;
    private LogSqliteDB logDB;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                Toast.makeText(MainActivity.this, "Unable to initialize Bluetooth", Toast.LENGTH_SHORT).show();
                finish();
            } else
                Toast.makeText(MainActivity.this, "Ble server start succ!!", Toast.LENGTH_SHORT).show();

            application.setBleService(mBluetoothLeService);

            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            HANDLER_FLAG = false;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device. This can be a
    // result of read
    // or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.state_connected);
                invalidateOptionsMenu();

            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                if (connect_dialog != null && connect_dialog.isShowing()) {
                    connect_dialog.dismiss();
                }
                mConnected = false;
                updateConnectionState(R.string.state_disconnected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                filterGattServices(mBluetoothLeService
                        .getSupportedGattServices());
            } else if (BleProto.LINK_PROTO_RSP_ACTION.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                byte[] rcv_data = intent.getByteArrayExtra(BleProto.PROTO_RSP_DATA);
                if (rcv_data != null) {
                    switch (rcv_data[BleProto.PROTO_CMD_POS] & 0xff) {
                        case BleProto.PROTO_DEVICE_CHECK:
                            Toast.makeText(MainActivity.this,
                                    R.string.ble_sync_time_succ,
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case BleProto.PROTO_UPLOAD_OPENDOOR_LOG:
                            saveOpenDoorLog(rcv_data);
                            break;
                        default:
                            break;
                    }

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        application = ((SmartLockApplication) getApplication());
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        mHandler = new Handler();

        initView();
        initListener();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    private void connectDevice() {
        connect_dialog = new ProgressDialog(MainActivity.this);
        connect_dialog.setTitle(getResources().getString(
                R.string.alert_message_connect_title));
        connect_dialog.setMessage(getResources().getString(
                R.string.alert_message_connect)
                + "\n"
                + mDeviceName
                + "\n"
                + mDeviceAddress
                + "\n"
                + getResources().getString(R.string.alert_message_wait));
        connect_dialog.show();

        // Get the connection status of the device
        if (!mConnected) {
            // Disconnected,so connect
            mBluetoothLeService.connect(mDeviceAddress);
        } else {
            // Not disconnected,so disconnect and then connect
            mBluetoothLeService.disconnect();
            mConnected = false;
            mBluetoothLeService.connect(mDeviceAddress);
        }
        HANDLER_FLAG = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (HANDLER_FLAG) {
                    connect_dialog.dismiss();
                    if (mBluetoothLeService != null)
                        mBluetoothLeService.disconnect();
                    try {
                        Toast.makeText(MainActivity.this,
                                R.string.connect_delay_message,
                                Toast.LENGTH_SHORT).show();

                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 5000);
    }

    private void initView() {
        initActionBar();
    }

    private void initActionBar() {
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toolbar.setSubtitle("未连接");
    }

    private void initListener() {
        llConnectDevice.setOnClickListener(this);
        llOpenLog.setOnClickListener(this);
        llSettings.setOnClickListener(this);
        llHelp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_connect_device:
                NavigateManager.gotoBleScanActivity(this);
                break;
            case R.id.ll_open_log:
                NavigateManager.gotoLogListActivity(this);
                break;
            case R.id.ll_settings:
                NavigateManager.gotoSettingsActivity(this);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService.disconnect();
        mBluetoothLeService = null;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mConnected) {
                    toolbar.setSubtitle("已连接"+mDeviceName);
                } else {
                    toolbar.setSubtitle("已断开");
                }
            }
        });
    }

    private void filterGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String uuid = null;
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            uuid = gattService.getUuid().toString();

            List<BluetoothGattCharacteristic> gattCharacteristics = gattService
                    .getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                //charas.add(gattCharacteristic);

                if (gattCharacteristic.getUuid().toString().equals(READ_UUID.toLowerCase(Locale.CHINA))) {

                    readGatt = gattCharacteristic;
                    mBluetoothLeService.setReadGatt(gattCharacteristic);

                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            mBluetoothLeService.setCharacteristicNotification(
                                    readGatt, true);
                            byte[] time_byte = TimeUtil.getDateTimeStr();
                            mBluetoothLeService.bleSendData(BleProto.FormatDownLinkData(BleProto.PROTO_DEVICE_CHECK, time_byte));        //send device test cmd
                        }
                    }
                            , 1000);
                }
                if (gattCharacteristic.getUuid().toString()
                        .equals(WRITE_UUID.toLowerCase(Locale.CHINA))) {
                    System.out.println("WRITE_UUID");
                    writeGatt = gattCharacteristic;
                    mBluetoothLeService.setWriteGatt(gattCharacteristic);
                }
            }
        }
        if (connect_dialog != null && connect_dialog.isShowing()) {
            connect_dialog.dismiss();
        }
        /**
         * Setting the handler flag to false. adding new fragment
         * ProfileControlFragment to the view
         */
        HANDLER_FLAG = false;
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleProto.LINK_PROTO_RSP_ACTION);
        return intentFilter;
    }

    private void saveOpenDoorLog(byte[] list_data) {

        int length = (list_data[BleProto.PROTO_RSP_LENGTH_POS] << 8) + list_data[BleProto.PROTO_RSP_LENGTH_POS + 1] - 1;
        int ptr = BleProto.PROTO_RSP_VALID_DATA_POS;
        logDB = new LogSqliteDB(this);

        for (int i = 0; i < length; i += BleProto.PROTO_LOG_LEN) {
            int index = (list_data[ptr] << 8) + list_data[ptr + 1];
            ptr += 2;
            String time = TimeUtil.formatBcdTime(list_data, ptr);
            ptr += 7;

            int type = list_data[ptr];
            ptr += 1;
            LogEntity log = new LogEntity(type, index, time);
            logDB.insertOneLog(log);
        }
        logDB.close();
        byte[] ret_code = new byte[1];
        ret_code[0] = 0;
        mBluetoothLeService.bleSendData(BleProto.FormatDownLinkData(BleProto.PROTO_UPLOAD_OPENDOOR_LOG, ret_code));        //send device test cmd
    }
}
