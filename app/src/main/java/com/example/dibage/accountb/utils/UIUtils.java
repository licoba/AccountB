package com.example.dibage.accountb.utils;


import android.app.Activity;
import android.content.Context;

import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;




public class UIUtils {

    //打印方法
    public static void print(String str) {

        System.out.println("dibage...." + str);

    }

    private static Toast mToast = null;

    //吐司提示
    public static void toast(Context context, String str) {
        if (context != null) {
            if (mToast == null) {
                mToast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(str);
                mToast.setDuration(Toast.LENGTH_SHORT);
            }
            mToast.show();
        }
    }

    //这样写效率不高，代码重复。优化之后：

    public static void darkenBackgroud(Activity context,Float bgcolor) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgcolor;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }


}
