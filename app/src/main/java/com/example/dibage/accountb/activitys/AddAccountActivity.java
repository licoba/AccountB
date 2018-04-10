package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class AddAccountActivity extends AppCompatActivity {



    EditText et_description;
    EditText et_username;
    EditText et_password;
    EditText et_remarks;
    Button btn_Submit;
    ListView listView;

    Toolbar toolbar;

    //获取dao实例
    DaoSession daoSession ;
    AccountDao mAccountDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);


        initView();

        daoSession = ((MyApplication)getApplication()).getDaoSession();
        mAccountDao = daoSession.getAccountDao();


        //替代ActionBar
        setSupportActionBar(toolbar);
        //显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置返回键为可点击状态
        getSupportActionBar().setHomeButtonEnabled(true);
        //隐藏自带AppTitle
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("添加账号");
        //toolbar.setOnMenuItemClickListener(onMenuItemClick);

        //给toolbar的左上角的按钮注册点击监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (v.getId() == android.R.id.home)
                //Toast.makeText(getApplicationContext(), "点击了返回箭头", Toast.LENGTH_LONG).show();
                AddAccountActivity.this.finish();
            }
        });

//        toolbar.setOnMenuItemClickListener(onMenuClick);

//        btn_Submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                UIUtils.toast(AddAccountActivity.this, "提交按钮");
//                Toasty.info(AddAccountActivity.this, "Here is some info for you.", Toast.LENGTH_SHORT, true).show();
//            }
//        });
        btn_Submit.setOnClickListener(clickListener);
        btn_Submit.setOnClickListener(clickListener);

    }

    private void initView() {
        et_description = findViewById(R.id.etDescription);
        et_password = findViewById(R.id.etPassword);
        et_username = findViewById(R.id.etUsername);
        et_remarks = findViewById(R.id.etRemark);
        btn_Submit = findViewById(R.id.btnSubmit);

        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listview);
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
                        Toasty.error(AddAccountActivity.this, msg, Toast.LENGTH_SHORT, true).show();
                    break;
                default:
                    Toasty.info(AddAccountActivity.this, msg, Toast.LENGTH_SHORT, true).show();

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



    //
//    private android.support.v7.widget.Toolbar.OnMenuItemClickListener onMenuClick = new android.support.v7.widget.Toolbar.OnMenuItemClickListener() {
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem) {
//
//            UIUtils.toast(AddAccountActivity.this, "有响应");
//
//            String msg = "";
//            switch (menuItem.getItemId()) {
//                case R.id.action_submit:
//                    msg += "点击了提交按钮";
//                    break;
//            }
//
//            if (!msg.equals("")) {
//                UIUtils.toast(AddAccountActivity.this, msg);
//            }
//            return true;
//        }
//    };


    //显示Menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.tb_submit, menu);
//        return true;
//    }
}
