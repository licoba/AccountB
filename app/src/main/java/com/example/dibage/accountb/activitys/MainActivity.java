package com.example.dibage.accountb.activitys;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.adapters.AccountAdapter;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.AccountDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.entitys.Account;
import com.example.dibage.accountb.utils.AccountUtils;
import com.example.dibage.accountb.utils.UIUtils;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.gjiazhe.wavesidebar.WaveSideBar;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    final String TAG = "MainActivity";
    Context context = MainActivity.this;
    static boolean isPause = false;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton fabAddAccount;
    private FloatingActionButton fabAddIdCard;
    private WaveSideBar sideBar;
    private ListView listView;
    private Toolbar toolbar;
    private PopupWindow mPopWindow;
    private LinearLayout ll_empty;
    List<Account> accountsList = new ArrayList<>();
    QueryBuilder<Account> qb;
    AccountAdapter accountAdapter ;
    DaoSession daoSession ;
    AccountDao mAccountDao;

    private float alpha = 1.0f;//初始值设为1，为不变暗
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFBI();
        initData();
        initView();
        initEvent();
    }

    private void initEvent() {
        //监听Item的点击事件
        listView.setOnItemClickListener(new myItemClickListener());
        //监听ListView的触摸事件
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (floatingActionMenu.isOpened()) {
                    floatingActionMenu.close(true);
                    return true;
                } else return false;
            }
        });

        listView.setOnItemLongClickListener(new myItemLongClickListener());

        fabAddAccount.setOnClickListener(FablickListener);
        fabAddIdCard.setOnClickListener(FablickListener);

    }

    private void initData() {

        daoSession = ((MyApplication) getApplication()).getDaoSession();
        mAccountDao = daoSession.getAccountDao();
        //获取queryBuilder，通过queryBuilder来实现查询功能
        //mAccountDao.queryBuilder()表示查询所有，
        // orderAsc(AccountDao.Properties.Firstchar)表示按照首字母升序排序（#比A大，所以需要用函数重新排序）
        qb = mAccountDao.queryBuilder().orderAsc(AccountDao.Properties.Firstchar, AccountDao.Properties.Username);
        accountsList = qb.list();
        accountsList = AccountUtils.orderListAccount(accountsList);
        accountAdapter = new AccountAdapter(context, R.layout.item_listview, accountsList);
        listView.setAdapter(accountAdapter);
    }

    private void initFBI() {
        floatingActionMenu = findViewById(R.id.fabMenu);
        fabAddAccount = findViewById(R.id.fabAddAcount);
        fabAddIdCard = findViewById(R.id.fabAddIdCard);
        sideBar = findViewById(R.id.side_bar);
        listView = findViewById(R.id.listview);
        ll_empty = findViewById(R.id.ll_empty);
        toolbar = findViewById(R.id.toolbar);
    }


    public void initView() {
        if (accountsList.size()>0){
            ll_empty.setVisibility(View.GONE);
            sideBar.setVisibility(View.VISIBLE);
        }else {
            sideBar.setVisibility(View.INVISIBLE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("账号册子");
        toolbar.setOnMenuItemClickListener((android.support.v7.widget.Toolbar.OnMenuItemClickListener) onMenuItemClick);
        //自定义侧边索引
        sideBar.setIndexItems("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
                "W", "X", "Y", "Z", "#");
        sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {

            @Override
            public void onSelectIndexItem(String index) {
                Log.d("WaveSideBar", index);
                int location = 0;
                boolean flag = false;//如果没有匹配到值，那么界面就不动
                //
                for (int i = 0; i < accountsList.size(); i++) {
                    if (accountsList.get(i).getFirstchar().equals(index)) {
                        location = i;
                        flag = true;
                        break;
                    }
                }
                Log.d("Location:", location + "");
                if (flag)
                    listView.setSelection(location);
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        isPause = true; //记录页面已经被暂停
        if (floatingActionMenu.isOpened())
            floatingActionMenu.close(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) { //判断是否暂停
            isPause = false;
            initData();
            //accountAdapter.notifyDataSetChanged();不起作用，不知道为什么
            accountAdapter = new AccountAdapter(context, R.layout.item_listview, accountsList);
            listView.setAdapter(accountAdapter);
            if (accountsList.size()>0){
                ll_empty.setVisibility(View.GONE);
                sideBar.setVisibility(View.VISIBLE);
            }else {
                sideBar.setVisibility(View.INVISIBLE);
            }
        }
    }

    public class myItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Account account = (Account) adapterView.getItemAtPosition(i);
                showPopupWindow(account);
        }
    }

    public class myItemLongClickListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            showPopupMenu(accountsList.get(i));
            return true;
        }
    }


    private void showPopupWindow(final Account account) {
        TextView tv1;
        TextView tv2;
        TextView tv3;
        LayoutInflater inflater = getLayoutInflater();
        View contentView = inflater.from(MainActivity.this).inflate(R.layout.pop_detail, null);
        tv1 = contentView.findViewById(R.id.tv_description);
        tv2 = contentView.findViewById(R.id.tv_username);
        tv3 = contentView.findViewById(R.id.tv_password);

        tv1.setText(account.getDescription());
        tv2.setText(account.getUsername());
        tv3.setText(account.getPassword());
        mPopWindow = new PopupWindow(contentView,
                getWindowManager().getDefaultDisplay().getWidth() - 200, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);

        //显示popupWindow
        View rootview = LayoutInflater.from(MainActivity.this). inflate(R.layout.activity_main, null);
        mPopWindow.setAnimationStyle(R.style.Popupwindow);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
        //UIUtils.darkenBackgroud(MainActivity.this, 0.5f);

        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        //backgroundAlpha((float)msg.obj);
                        UIUtils.darkenBackgroud(MainActivity.this, (float)msg.obj);
                        break;
                }
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(alpha>0.5f){
                    try {
                        Thread.sleep(4);//每0.004s变暗0.01
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    alpha-=0.01f;
                    msg.obj =alpha ;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();



        LinearLayout layout1 = contentView.findViewById(R.id.layout1);
        LinearLayout layout2 = contentView.findViewById(R.id.layout2);

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cmb = (ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(account.getUsername());
                Toasty.success(context, "账号已复制", Toast.LENGTH_SHORT, true).show();
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager cmb = (ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(account.getPassword());
                Toasty.success(context, "密码已复制", Toast.LENGTH_SHORT, true).show();
            }
        });

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(alpha<1.0f){
                            try {
                                Thread.sleep(3);//每0.004s变暗0.01
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Message msg = mHandler.obtainMessage();
                            msg.what = 1;
                            alpha+=0.01f;
                            msg.obj =alpha ;
                            mHandler.sendMessage(msg);
                        }
                    }
                }).start();
            }
        });
    }

    private void showPopupMenu(final Account account) {

        LayoutInflater inflater = getLayoutInflater();
        View contentView = inflater.from(MainActivity.this).inflate(R.layout.pop_menu, null);

        mPopWindow = new PopupWindow(contentView,
                getWindowManager().getDefaultDisplay().getWidth() - 220, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);

        //显示popupWindow
        View rootview = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        mPopWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
        UIUtils.darkenBackgroud(MainActivity.this, 0.5f);

        LinearLayout layout1 = contentView.findViewById(R.id.layout1);
        LinearLayout layout2 = contentView.findViewById(R.id.layout2);
        LinearLayout layout3 = contentView.findViewById(R.id.layout3);

        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,EditAccountActivity.class);
                startActivity(intent);
                mPopWindow.dismiss();
            }
        });

        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.toast(context,"点击了layout2");
            }
        });

        layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UIUtils.toast(context,"点击了layout3");
            }
        });

        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                UIUtils.darkenBackgroud(MainActivity.this, 1f);
            }
        });
    }


    //浮动添加按钮的监听器
    private View.OnClickListener FablickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent;
            switch (v.getId()) {
                case R.id.fabAddAcount:
                    intent = new Intent(context, AddAccountActivity.class);
                    startActivity(intent);
                    floatingActionMenu.close(true);
                    break;
                case R.id.fabAddIdCard:
                    intent = new Intent(context, AddPhotoActivity.class);
                    startActivity(intent);
                    floatingActionMenu.close(true);
                    break;

            }
        }
    };

    private android.support.v7.widget.Toolbar.OnMenuItemClickListener onMenuItemClick = new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            String msg = "";
            Intent intent ;
            switch (menuItem.getItemId()) {
                case R.id.action_search:
                    intent = new Intent(context,SearchActivity.class);
                    startActivity(intent);

                    break;
                case R.id.action_setting:
                    intent = new Intent(context, MoreActivity.class);
                    startActivity(intent);
                    break;
                case R.id.action_card:
                    intent = new Intent(context, CardActivity.class);
                    startActivity(intent);
                    break;
            }

            if (!msg.equals("")) {
                Toasty.success(context, msg, Toast.LENGTH_SHORT, true).show();
            }
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }


}
