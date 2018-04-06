package com.example.dibage.accountb.utils;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Created by dibage on 2018/3/27.
 */

public class SimpleUtils {

    public static boolean isNotNull(EditText et) {


        if(TextUtils.isEmpty(et.getText().toString().trim()))//如果是空的返回false
            return false;
        else
            return true;

    }

    //获取输入框字符串
    public static String getStrings(EditText et){
        return(et.getText().toString().trim());
    }


}
