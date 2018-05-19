package com.example.dibage.accountb.activitys;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.dibage.accountb.R;
import com.example.dibage.accountb.adapters.RecycleAdapter;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.AccountDao;
import com.example.dibage.accountb.dao.CardDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.entitys.Account;
import com.example.dibage.accountb.entitys.Card;
import com.example.dibage.accountb.utils.SPUtils;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class CardActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private LinearLayout ll_empty;
    private RecyclerView recyclerView;
    private RecycleAdapter mAdapter;
    private ImageView btn_add;
    DaoSession daoSession ;
    CardDao cardDao;
    QueryBuilder<Card> qb;
    List<Card> cardList = new ArrayList<>();
    public static boolean isUpdate = false;
    private Context context;
    private Dialog dialog;
    private TextView tv_tip;
    private FingerprintIdentify mFingerprintIdentify;
    private boolean finger_state = false;
    private boolean is_setting_pwd = false;

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
        context = this;
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        cardDao = daoSession.getCardDao();
        qb = cardDao.queryBuilder().orderDesc(CardDao.Properties.Id);
        cardList.clear();
        cardList.addAll(qb.list());
        finger_state = (boolean) SPUtils.get(context, "finger_state", false);
        is_setting_pwd = (boolean) SPUtils.get(context, "is_setting_pwd", false);
    }

    private void initEvent() {
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardActivity.this, AddPhotoActivity.class);
                intent.putExtra("fromAty","CardActivity");
                startActivity(intent);
            }
        });

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (finger_state && is_setting_pwd) {
                    //因为证件照更为隐秘，所以需要再次验证指纹。
                    showFingerDialo(position);
                } else {
                    Intent intent = new Intent(CardActivity.this, CardDetailActivity.class);
                    intent.putExtra("card", cardList.get(position));
                    startActivity(intent);
                }


            }
        });

    }

    private void showFingerDialo(int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_finger_tip, null);
        tv_tip = view.findViewById(R.id.finger_tip);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.75); //设置宽度
        dialog.getWindow().setAttributes(lp);

        startValidate(position);
    }

    //开始指纹验证
    private void startValidate(int position) {
        mFingerprintIdentify = new FingerprintIdentify(this);                // 构造对象
        mFingerprintIdentifyListener listener = new mFingerprintIdentifyListener();
        listener.setPosition(position);
        mFingerprintIdentify.startIdentify(3, new mFingerprintIdentifyListener());// 开始验证指纹识别


    }

    public class mFingerprintIdentifyListener implements BaseFingerprint.FingerprintIdentifyListener {

        int position = 0;
        void  setPosition(int position){
            this.position = position;
        }

        @Override
        public void onSucceed() {
            tv_tip.setText("验证成功 √");
            tv_tip.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable(){
                public void run() {
                    Intent intent = new Intent(CardActivity.this, CardDetailActivity.class);
                    intent.putExtra("card", cardList.get(position));
                    startActivity(intent);
                    dialog.dismiss();
                }
            }, 500);



        }

        // 指纹不匹配，并返回可用剩余次数并自动继续验证
        @Override
        public void onNotMatch(int availableTimes) {
            tv_tip.setText("指纹不匹配，还有" + availableTimes + "次机会");
            tv_tip.setVisibility(View.VISIBLE);
        }

        // 错误次数达到上限或者API报错停止了验证，自动结束指纹识别
        // isDeviceLocked 表示指纹硬件是否被暂时锁定
        @Override
        public void onFailed(boolean isDeviceLocked) {
           Toasty.error(context,"指纹不匹配，请确保为本人操作！").show();
            if (isDeviceLocked) {
                Toasty.error(context, "失败次数过多，指纹识别被暂时锁定（30秒）").show();
                mFingerprintIdentify.cancelIdentify();
            }
            dialog.dismiss();
        }

        @Override
        public void onStartFailedByDeviceLocked() {
            Toasty.error(context, "硬件被锁定，30秒无法使用指纹识别").show();
        }
    }//end mFingerprintIdentifyListener


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
        btn_add= findViewById(R.id.btn_add);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isUpdate){
            initData();
            mAdapter.notifyDataSetChanged();
        }
    }
}
