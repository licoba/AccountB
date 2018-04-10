package com.example.dibage.accountb.utils;

import android.text.TextUtils;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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

    //获取一个完全随机  并且唯一的文件名
    public static  String getRandomFileName(){
        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        Random random = new Random();

        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数

        return rannum + str;// 当前时间
    }


}
