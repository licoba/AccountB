package com.example.dibage.accountb.adapters;


import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.dibage.accountb.R;
import com.example.dibage.accountb.entitys.Card;
import com.example.dibage.accountb.entitys.Record;

import java.util.List;


/**
 * 恢复数据页面的适配器
 */
public class RecordAdapter extends BaseQuickAdapter<Record, BaseViewHolder> {


    public RecordAdapter(int layoutResId, @Nullable List<Record> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Record item) {
        helper.setText(R.id.name,item.getName());
        helper.setText(R.id.location,item.getLocation());
        helper.setText(R.id.number,item.getNumber()+"条记录");
    }
}
