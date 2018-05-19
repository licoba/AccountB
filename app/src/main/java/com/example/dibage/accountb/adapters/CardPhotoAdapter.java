package com.example.dibage.accountb.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.dibage.accountb.R;
import com.example.dibage.accountb.entitys.Card;
import com.example.dibage.accountb.entitys.Photo;
import com.example.dibage.accountb.utils.PhotoUtils;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.List;


/**
 * 传入的数据应该是Photo对象，而不是Card对象
 */
public class CardPhotoAdapter extends BaseQuickAdapter<Photo, BaseViewHolder> {
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public CardPhotoAdapter(int layoutResId, @Nullable List<Photo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Photo item) {
        ImageView imageView = (ImageView) helper.getView(R.id.img_card);
        File file = new File(item.getPhoto_path());
        Glide.with(context).load(file).into(imageView);
    }
}
