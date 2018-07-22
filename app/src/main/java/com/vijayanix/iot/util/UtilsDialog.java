package com.vijayanix.iot.util;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

public class UtilsDialog {

    public static MaterialDialog showTitleContent(Context context, String title, String content,String positiveText,String negativeText ) {
        MaterialDialog.Builder materialBuilder = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(positiveText)
                .negativeText(negativeText)
                .cancelable(true);
        return materialBuilder.show();
    }

    public static MaterialDialog showTitleContent2(Context context, String title, String content,MaterialDialog.SingleButtonCallback positiveCallback,MaterialDialog.SingleButtonCallback negativeCallback ) {
        MaterialDialog.Builder materialBuilder = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .positiveText(context.getResources().getString(android.R.string.ok))
                .negativeText(context.getResources().getString(android.R.string.cancel))
		        .onPositive(positiveCallback)
		        .onNegative(negativeCallback)
                .cancelable(true);
        return materialBuilder.show();

    }

}
