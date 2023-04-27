package com.example.dibage.accountb.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.entitys.Account;

import java.util.List;

public class ChangeColorAdapter extends AccountAdapter {
    private String searchString;
    private int resourceId;

    public ChangeColorAdapter(@NonNull Context context, int resource, @NonNull List<Account> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    public void setsearchString(String s) {
        searchString = s;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Account account = getItem(position);
        boolean flag = false;
        if (position < 1) {
            flag = true;
        } else if (position >= 1) {
            if (!getItem(position).getFirstchar().equals(getItem(position - 1).getFirstchar())) {
                flag = true;
            }
        }

        //使用ViewHolder优化 ListView
        ViewHolder viewHolder;
        //view = inflater.inflate(R.layout.item_listview,parent,false);
        if (convertView == null) { //使用convertView重复使用查找加载好的布局
            //使用布局填充器为子项加载我们传入的子布局「」
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            //使用ViewHolder的三个成员变量来保存三个控件，这样就省区了每次都findviewbyid，优化性能
            viewHolder.tv_firstChar = (TextView) convertView.findViewById(R.id.tv1);//查找
            viewHolder.tv_description = (TextView) convertView.findViewById(R.id.tv2);
            viewHolder.tv_username = (TextView) convertView.findViewById(R.id.tv3);

            convertView.setTag(viewHolder);//把ViewHolder储存在View里面
        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        //设置数据
        if (flag) {
            viewHolder.tv_firstChar.setText(account.getFirstchar());
        } else {
            viewHolder.tv_firstChar.setText("");
        }

        if (account.getDescription().contains(searchString)) {//如果描述中包含输入的字符串
            String content = "";
            String str[] = account.getDescription().split(searchString);
            Log.e("tempString::::", str.length + "");
            Log.e("分割后字符串个数::::", str.length + "");
            if (str.length == 0 || str.length == 1) {
                content = "<font color='#009788'>" + searchString + "</font>";
            } else {
                content = str[0] + "<font color='#009788'>" + searchString + "</font>" + str[1];
            }
//        viewHolder.tv_description.setText(account.getDescription());
            viewHolder.tv_description.setText(Html.fromHtml(content));
        }else{
            viewHolder.tv_description.setText(account.getDescription());
        }
        if (account.getUsername().contains(searchString)) {//如果账号中包含输入的字符串
            String content2 = "";
            //int index = tempString.indexOf(searchString);//得到指定字符串的下标
            String str2[] = account.getUsername().split(searchString);
            Log.e("tempString::::", str2.length + "");

            Log.e("分割后字符串个数::::", str2.length + "");
            if (str2.length == 0 || str2.length == 1) {
                content2 = "<font color='#009788'>" + searchString + "</font>";
            } else {
                content2 = str2[0] + "<font color='#009788'>" + searchString + "</font>" + str2[1];
            }
            viewHolder.tv_username.setText(Html.fromHtml(content2));
        }else{
            viewHolder.tv_username.setText(account.getUsername());
        }

        return convertView;
    }
}
