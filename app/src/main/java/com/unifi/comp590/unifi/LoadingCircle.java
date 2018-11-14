package com.unifi.comp590.unifi;

import android.app.Activity;
import android.support.v7.app.AlertDialog;

public class LoadingCircle {
    private static AlertDialog.Builder builder;
    public static AlertDialog Circle(Activity activity) {
        builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_bar);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
