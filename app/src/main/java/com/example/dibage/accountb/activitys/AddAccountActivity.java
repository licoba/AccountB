package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.dao.AccountDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.entitys.Account;
import com.example.dibage.accountb.utils.AccountUtils;
import com.example.dibage.accountb.utils.SimpleUtils;
import com.example.dibage.accountb.utils.UIUtils;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class AddAccountActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton btn_clear1;
    private ImageButton btn_clear2;
    private ImageButton btn_clear3;
    private ImageButton btn_clear4;

    EditText et_description;
    EditText et_username;
    EditText et_password;
    EditText et_remarks;
    Button btn_Submit;
    Button btn_getRandom;
    ListView listView;

    Toolbar toolbar;
    DaoSession daoSession ;
    AccountDao mAccountDao;
    private PopupWindow mPopupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);

        initView();
        initData();
        initEvent();

    }

    private void initData() {
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        mAccountDao = daoSession.getAccountDao();
    }

    private void initEvent() {
        btn_clear1.setOnClickListener(this);
        btn_clear2.setOnClickListener(this);
        btn_clear3.setOnClickListener(this);
        btn_clear4.setOnClickListener(this);
        btn_Submit.setOnClickListener(clickListener);
        btn_getRandom.setOnClickListener(this);

    }

    private void initView() {
        et_description = findViewById(R.id.etDescription);
        et_password = findViewById(R.id.etPassword);
        et_username = findViewById(R.id.etUsername);
        et_remarks = findViewById(R.id.etRemark);
        btn_Submit = findViewById(R.id.btnSubmit);
        btn_clear1 = findViewById(R.id.btn_clear1);
        btn_clear2 = findViewById(R.id.btn_clear2);
        btn_clear3 = findViewById(R.id.btn_clear3);
        btn_clear4 = findViewById(R.id.btn_clear4);

        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listview);
        btn_getRandom = findViewById(R.id.btn_getRandom);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("添加账号");
        //给toolbar的左上角的按钮注册点击监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAccountActivity.this.finish();
            }
        });
;

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        String msg;
        boolean VertifyState = false ;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSubmit:
                    if (SimpleUtils.isNotNull(et_description)){
                        if (SimpleUtils.isNotNull(et_username)){
                            if(SimpleUtils.isNotNull(et_password)) {
                                msg = "保存成功";
                                VertifyState = true;

                                AddRecord(et_description,et_username,et_password,et_remarks);
                                AddAccountActivity.this.finish();
                            }
                            else
                                msg="密码不能为空";
                        }else
                            msg = "账号不能为空";
                    }
                    else
                        msg="名称不能为空";
                    if (VertifyState)
                    Toasty.success(AddAccountActivity.this, msg, Toast.LENGTH_SHORT, true).show();
                    else
                        Toasty.warning(AddAccountActivity.this, msg, Toast.LENGTH_SHORT, true).show();
                    break;
                default:
                    Toasty.warning(AddAccountActivity.this, msg, Toast.LENGTH_SHORT, true).show();

            }
        }
    };

    //向数据库添加一条记录
    private void AddRecord(EditText et_description, EditText et_username, EditText et_password, EditText et_remarks) {

        String description = SimpleUtils.getStrings(et_description);
        String username = SimpleUtils.getStrings(et_username);
        String password = SimpleUtils.getStrings(et_password);
        String remarks = SimpleUtils.getStrings(et_remarks);
        String firstChar = AccountUtils.getFirstString(description);
        Account account = new Account(description,username,password,remarks,firstChar);
        mAccountDao.insert(account);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_clear1:
                et_description.setText("");
                break;
            case R.id.btn_clear2:
                et_username.setText("");
                break;
            case R.id.btn_clear3:
                et_password.setText("");
                break;
            case R.id.btn_clear4:
                et_remarks.setText("");
                break;
            case R.id.btn_getRandom:
                showPopRandom();
                break;
        }
    }

    private void showPopRandom() {
        mPopupWindow = new PopupWindow();
        LayoutInflater inflater = getLayoutInflater();
        View contentView = inflater.from(AddAccountActivity.this).inflate(R.layout.pop_random, null);
        View rootview = inflater.from(AddAccountActivity.this). inflate(R.layout.activity_add_account, null);
        mPopupWindow = new PopupWindow(contentView,
                getWindowManager().getDefaultDisplay().getWidth() - 200, WindowManager.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.showAtLocation(rootview, Gravity.CENTER, 0, 0);
    }
}
