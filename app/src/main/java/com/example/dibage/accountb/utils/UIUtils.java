package com.example.dibage.accountb.utils;


import android.Manifest;
import android.app.Activity;
import android.content.Context;

import android.content.pm.PackageManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.dibage.accountb.activitys.MoreActivity;


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

    //bgcolor：0为黑，1为无改变

    public static void darkenBackgroud(Activity context,Float bgcolor) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgcolor;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }


    /**
     * 设置toolbar的标题，以及返回事件的监听
     * @param aty 用来显示toolbar的Activity
     * @param toolbar toolbar对象
     * @param title toolbar的标题
     */
    public static  void setToolbar(final AppCompatActivity aty, Toolbar toolbar, String title){
        aty.setSupportActionBar(toolbar);
        aty.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        aty.getSupportActionBar().setHomeButtonEnabled(true);
        aty.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aty.finish();
            }
        });
    }



}
