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

    public static String DEFAULT_KEY = "dibage";
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

    public static  String getNameByDate(){
        SimpleDateFormat simpleDateFormat;
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String str = simpleDateFormat.format(date);
        return "backup_" + str;// 当前时间
    }


    /**
     *
     * @param length 长度
     * @param big 是否包含大写字母
     * @param small 是否包含小写字母
     * @param special 是否包含特殊字符
     * @return
     */
    public static String getRandomPwd(int length,boolean big,boolean small,boolean special){
        char[] numberChar = {'1','2','3','3','5','6','7','8','9','0'};
        char[] bigChar = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
        char[] smallChar = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
        char[] specialChar = {'~','!','@','#','$','%','^','&','*','(',')','[',']','{','}','?','/','<','>',':',';','|','+','-'};

//        String numberString = numberChar.toString();
//        String bigString = bigChar.toString();
//        String smallString = smallChar.toString();
//        String specialString = specialChar.toString();
//        StringBuffer sBuffer = new StringBuffer(numberString);
        String numberString ="0123456789";
        String bigString = "ABCDEFGHIJKMLNOPQRSTUVWXYZ";
        String smallString = "abcdefghijklmnopqrstuvwxyz";
        String specialString ="~!@#$%^&*()+}{<>?[]=";
        StringBuffer sBuffer = new StringBuffer(numberString);
        if(big&&small&&special){
            sBuffer = sBuffer.append(bigString).append(smallString).append(specialString);
        }else if(!big&&small&&special){
            sBuffer = sBuffer.append(smallString).append(specialString);
        }else if(big&!small&&special){
            sBuffer = sBuffer.append(bigString).append(specialString);
        }else if(big&small&&!special){
            sBuffer = sBuffer.append(bigString).append(smallString);
        }else if(big&!small&&!special){
            sBuffer = sBuffer.append(bigString);
        }else if(!big&small&&!special){
            sBuffer = sBuffer.append(smallString);
        }else if(!big&!small&&special){
            sBuffer = sBuffer.append(specialString);
        }else {
            sBuffer = sBuffer;
        }

        return getRandomByString(length,sBuffer.toString());
    }

    private static String getRandomByString(int length, String str) {
        StringBuffer sb = new StringBuffer("");
        //random()方法产生的随机数在0.0和1.0之间
        for (int i = 0; i < length; i++) {
            sb.append(str.charAt((int)Math.round(Math.random() * (str.length() - 1))));
        }
            return sb.toString();
    }


}
