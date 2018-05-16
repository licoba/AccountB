package com.example.dibage.accountb.activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.adapters.CardPhotoAdapter;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.CardDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.dao.PhotoDao;
import com.example.dibage.accountb.entitys.Card;
import com.example.dibage.accountb.entitys.Photo;

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

        CardPhotoAdapter mAdapter = new CardPhotoAdapter(R.layout.item_cardphoto, photoList);
        mAdapter.setContext(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

    }

    private void initData() {
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
    }
}
