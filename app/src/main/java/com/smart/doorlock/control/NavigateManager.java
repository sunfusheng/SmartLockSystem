package com.smart.doorlock.control;

import android.content.Context;
import android.content.Intent;

import com.smart.doorlock.ui.AboutActivity;
import com.smart.doorlock.ui.PwdListActivity;
import com.smart.doorlock.ui.SettingsActivity;

public class NavigateManager {

    public static void gotoSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
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

}
