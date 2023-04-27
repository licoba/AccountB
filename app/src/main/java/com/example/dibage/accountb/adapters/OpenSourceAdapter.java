package com.example.dibage.accountb.adapters;


import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.dibage.accountb.R;
import com.example.dibage.accountb.entitys.OpenSource;

import java.util.List;

public class OpenSourceAdapter extends BaseQuickAdapter<OpenSource, BaseViewHolder> {
    public OpenSourceAdapter(int layoutResId, @Nullable List<OpenSource> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OpenSource item) {
        helper.setText(R.id.tv_name,item.getName());
        helper.setText(R.id.tv_description,item.getDescribe());
        //ll_open
        //helper.addOnClickListener(R.id.ll_open);
    }
}
