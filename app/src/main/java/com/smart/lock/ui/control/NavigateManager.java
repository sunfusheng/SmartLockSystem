package com.smart.lock.ui.control;

import android.content.Context;
import android.content.Intent;

import com.smart.lock.ui.AboutActivity;
import com.smart.lock.ui.ScretListActivity;
import com.smart.lock.ui.SettingsActivity;

public class NavigateManager {

    public static void gotoSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void gotoAboutActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    public static void gotoScretListActivity(Context context) {
        Intent intent = new Intent(context, ScretListActivity.class);
        context.startActivity(intent);
    }

}
