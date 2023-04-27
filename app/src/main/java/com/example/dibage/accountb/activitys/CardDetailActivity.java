package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.dibage.accountb.R;
import com.example.dibage.accountb.adapters.CardPhotoAdapter;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.CardDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.dao.PhotoDao;
import com.example.dibage.accountb.entitys.Card;
import com.example.dibage.accountb.entitys.Photo;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import static android.widget.LinearLayout.HORIZONTAL;


/**
 * 证件详情
 */
public class CardDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    Intent intent;
    private Card card;
    private ImageView btn_edit;
    private TextView tv_cardname;
    private TextView tv_username;
    private TextView tv_cardnumber;
    private TextView tv_remark;
    private RecyclerView recyclerView;
    private DaoSession daoSession;
    private CardDao cardDao;
    private List<Photo> photoList = new ArrayList<>();
    private CardPhotoAdapter mAdapter;
    private FrameLayout fl_bigimage;
    private ImageView iv_exit;
    private PhotoView big_image;
    private LinearLayout ll_detail;
    Context context;
    int flag = 0;//用来判断大图是否显示

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_detail);

        initFBI();
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        //点击查看大图
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                flag =1;
                ll_detail.setVisibility(View.GONE);
                fl_bigimage.setVisibility(View.VISIBLE);
                Glide.with(context).load(photoList.get(position).getPhoto_path()).into(big_image);
            }
        });


        big_image.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                flag = 0;
                ll_detail.setVisibility(View.VISIBLE);
                fl_bigimage.setVisibility(View.GONE);
            }
        });
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("证件详情");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardDetailActivity.this.finish();
            }
        });
        tv_cardname.setText(card.getCard_name());
        tv_username.setText(card.getUsername());
        tv_cardnumber.setText(card.getCard_number());
        tv_remark.setText(card.getRemark());

        mAdapter = new CardPhotoAdapter(R.layout.item_cardphoto, photoList);
        mAdapter.setContext(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    private void initData() {
        context = this;
        intent  =getIntent();
        daoSession = ((MyApplication) getApplication()).getDaoSession();
        PhotoDao photoDao = daoSession.getPhotoDao();
        card = (Card) intent.getSerializableExtra("card");//获取传递过来的intent对象
        QueryBuilder qb = photoDao.queryBuilder();
        photoList.clear();
        photoList.addAll(qb.where(PhotoDao.Properties.CardId.eq(card.getId())).list());
        //Log.e("图片张数：",photoList.size()+"");
    }

    private void initFBI() {
        toolbar = findViewById(R.id.toolbar);
        btn_edit = findViewById(R.id.btn_edit);
        tv_cardname = findViewById(R.id.tv_cardname);
        tv_username = findViewById(R.id.tv_username);
        tv_cardnumber = findViewById(R.id.tv_cardnumber);
        tv_remark = findViewById(R.id.tv_remark);
        recyclerView = findViewById(R.id.recyclerView);
        fl_bigimage = findViewById(R.id.fl_bigimage);
        big_image = fl_bigimage.findViewById(R.id.big_image);
        ll_detail = findViewById(R.id.ll_detail);
    }


    //设置图片全屏显示，隐藏状态栏
    public void setAllWindow(){

    }

    @Override
    public void onBackPressed() {
        if(flag==1) {
            ll_detail.setVisibility(View.VISIBLE);
            fl_bigimage.setVisibility(View.GONE);
            flag=0;
        }else{
            super.onBackPressed();
        }
    }
}
