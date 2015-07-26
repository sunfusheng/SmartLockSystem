package com.smart.doorlock;

import com.orm.SugarApp;
import com.smart.doorlock.ui.BluetoothLeService;

/**
 * Created by sunfusheng on 2015/6/25.
 */
public class SmartLockApplication extends SugarApp {

    public BluetoothLeService ble_service = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public void setBleService(BluetoothLeService service) {
        ble_service = service;
    }

    public BluetoothLeService getBleService() {
        return ble_service;
    }

    public String getResourceString(int res_id){
        return SmartLockApplication.this.getResourceString(res_id);
    }

}
