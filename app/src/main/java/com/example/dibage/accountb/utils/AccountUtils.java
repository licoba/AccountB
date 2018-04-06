package com.example.dibage.accountb.utils;

import com.example.dibage.accountb.entitys.Account;
import com.github.promeg.pinyinhelper.Pinyin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dibage on 2018/3/28.
 */

public class AccountUtils {

    //获取账户名称的首字母
    public static String getFirstString(String str) {
        //思路：首先截取str的第一个字符，然后判断中英文或其它字符，
        //如果是中文，将字符转化成拼音，然后提取首字母返回
        //如果是英文，直接返回第一个字符
        //如果是其它（类似标点符号、日语、德语等等，返回#号
        char c = str.charAt(0);
        if(Pinyin.isChinese(c)){
            return Pinyin.toPinyin(c).substring(0,1);
        }else if(isEnglish(c)){
            return (c+"").toUpperCase();
        }else
            return "#";
    }

    //判断字符是否为中文
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    //判断字符是否为英文
    public static boolean isEnglish(char c) {
        String cString = c+"";
        return cString.matches("^[a-zA-Z]*");

    }


    //获取某个汉字的首字母
    public static String getFirstcharForChinese(char c) {
        return "";

    }

    //从A-Z-#重新排序list
    public static List<Account> orderListAccount(List<Account> accountList){
        List<Account> returnList = null;
        List<Account> TempList = new ArrayList<>();
        int size = accountList.size();
        //迭代器中不允许做增删操作，增删操作必须迭代完之后才可以，所以推荐使用For循环遍历
//        for(Account account:accountList){
//            String firstString = account.getFirstchar();
//            if("#".equals(firstString)){
//                accountList.add(account);
//                accountList.remove(0);
//            }
//
//        }
        //将#号移到最后面
        //使用iterator的remove方法，如果用list的remove方法同样会报上面提到的ConcurrentModificationException错误。
        for(int i = accountList.size() - 1; i >= 0; i--){
            Account account = accountList.get(i);

            if( account.getFirstchar().equals("#")){
                TempList.add(accountList.get(i));
                accountList.remove(i);
            }
        }


        Collections.reverse(TempList);
        accountList.addAll(TempList);
        return  accountList;

    }



}
