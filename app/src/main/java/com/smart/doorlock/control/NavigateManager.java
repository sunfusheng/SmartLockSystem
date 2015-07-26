package com.smart.doorlock.control;

import android.content.Context;
import android.content.Intent;

import com.smart.doorlock.ui.AboutActivity;
import com.smart.doorlock.ui.BleScanActivity;
import com.smart.doorlock.ui.LogListActivity;
import com.smart.doorlock.ui.OtherSettingsActivity;
import com.smart.doorlock.ui.PwdListActivity;
import com.smart.doorlock.ui.SettingsActivity;
import com.smart.doorlock.ui.ZwListActivity;

public class NavigateManager {

    public static void gotoSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void gotoOtherSettingsActivity(Context context) {
        Intent intent = new Intent(context, OtherSettingsActivity.class);
        context.startActivity(intent);
    }

    public static void gotoBleScanActivity(Context context) {
        Intent intent = new Intent(context, BleScanActivity.class);
        context.startActivity(intent);
    }

    public static void gotoAboutActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    public static void gotoPwdListActivity(Context context) {
        Intent intent = new Intent(context, PwdListActivity.class);
        context.startActivity(intent);
    }

	public static void gotoZwListActivity(Context context) {
        Intent intent = new Intent(context, ZwListActivity.class);
        context.startActivity(intent);
    }

    public static void gotoLogListActivity(Context context) {
        Intent intent = new Intent(context, LogListActivity.class);
        context.startActivity(intent);
    }
}
