package com.example.dibage.accountb.adapters;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dibage.accountb.R;
import com.example.dibage.accountb.entitys.Card;

import java.util.List;

/**
 * 第一个泛型Status是数据实体类型，第二个BaseViewHolder是ViewHolder其目的是为了支持扩展ViewHolder。
 */
public class RecycleAdapter extends BaseQuickAdapter<Card, BaseViewHolder>  {


    public RecycleAdapter(int layoutResId, List<Card> data) {
        super(layoutResId,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Card card) {

        String cardnum = card.getCard_number();
        if(card.getCard_number().length()>4){   //显示：*号加后四位，例如：*451X
            cardnum = cardnum.substring(cardnum.length()-4,card.getCard_number().length());
            cardnum = "＊"+cardnum;
        }

        helper.setText(R.id.card_name,card.getCard_name())
                .setText(R.id.card_number,cardnum)
                .setText(R.id.username,card.getUsername());

    }
}
