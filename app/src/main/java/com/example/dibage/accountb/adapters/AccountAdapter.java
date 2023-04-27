package com.example.dibage.accountb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.entitys.Account;

import java.util.List;

public class AccountAdapter extends ArrayAdapter<Account> {

    private int resourceId;


    public AccountAdapter(@NonNull Context context, int resource, @NonNull List<Account> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Account account = getItem(position);
        boolean flag = false;
        if(position<1){
            flag = true;
        }else if(position>=1){
            if(!getItem(position).getFirstchar().equals(getItem(position-1).getFirstchar())){
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
        viewHolder.tv_description.setText(account.getDescription());
        viewHolder.tv_username.setText(account.getUsername());

        return convertView;
    }

    public class ViewHolder {//使用ViewHolder来优化ListView-

        TextView tv_firstChar;
        TextView tv_description;
        TextView tv_username;
    }

}

