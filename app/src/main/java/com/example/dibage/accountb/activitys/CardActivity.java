package com.example.dibage.accountb.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.adapters.RecycleAdapter;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.AccountDao;
import com.example.dibage.accountb.dao.CardDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.entitys.Account;
import com.example.dibage.accountb.entitys.Card;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class CardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout ll_empty;
    private RecyclerView recyclerView;
    private RecycleAdapter mAdapter;
    DaoSession daoSession ;
    CardDao cardDao;
    QueryBuilder<Card> qb;
    List<Card> cardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        initFBI();
        initData();
        initView();
        initEvent();
    }

    private void initData() {
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        cardDao = daoSession.getCardDao();
        qb = cardDao.queryBuilder();
        cardList = qb.list();
    }

    private void initEvent() {

    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("证件夹");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardActivity.this.finish();
            }
        });

        //创建布局管理（这个是为什么？？）
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new RecycleAdapter(R.layout.item_card,cardList);
        recyclerView.setAdapter(mAdapter);

        if(!cardList.isEmpty()){
            ll_empty.setVisibility(View.INVISIBLE);
        }


    }

    private void initFBI() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
        ll_empty = findViewById(R.id.ll_empty);

    }
}
