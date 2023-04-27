package com.example.dibage.accountb.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.utils.SPUtils;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;

import es.dmoral.toasty.Toasty;

/**
 * 开发中的新功能.
 * 0.0
 * 
 */

public class DevelopActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Switch switch_finger;
    private FingerprintIdentify mFingerprintIdentify;
    private LinearLayout ll_settingpassword;
    private LinearLayout ll_developing;
    private final int SETTING_PASSWORD = 1;
    private TextView tv_set_pwd;
    private boolean is_setting_pwd;
    private Context context;
    private Button btnShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        initFBI();
        iniData();
        initView();
        initEvent();
        mFingerprintIdentify = new FingerprintIdentify(this);

        setSupportActionBar(toolbar);
        //显示返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //设置返回键为可点击状态
        getSupportActionBar().setHomeButtonEnabled(true);
        //隐藏自带AppTitle
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("功能与设置");
        //toolbar.setOnMenuItemClickListener(onMenuItemClick);

        //给toolbar的左上角的按钮注册点击监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (v.getId() == android.R.id.home)
                //Toast.makeText(getApplicationContext(), "点击了返回箭头", Toast.LENGTH_LONG).show();
                DevelopActivity.this.finish();
            }
        });

        switch_finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!mFingerprintIdentify.isHardwareEnable()) {
                        Toasty.info(DevelopActivity.this, "您的设备不支持指纹识别，无法使用此功能").show();
                    } else if (!mFingerprintIdentify.isRegisteredFingerprint()) {
                        Toasty.info(DevelopActivity.this, "请在您的设备中录入指纹后再使用此功能").show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_finger.setChecked(false);
                            }
                        },500);

                    } else {
                        //选中的处理
                        Toasty.success(DevelopActivity.this, "指纹解锁已启用").show();
                        SPUtils.put(DevelopActivity.this, "finger_state", true);
                    }
                } else {
                    //没有选中的处理
                    if((Boolean) SPUtils.get(DevelopActivity.this, "finger_state", false)) {
                        Toasty.info(DevelopActivity.this, "指纹解锁已关闭").show();
                        SPUtils.put(DevelopActivity.this, "finger_state", false);
                    }
                }

            }
        });
    }

    private void initView() {
        if (is_setting_pwd) {
            tv_set_pwd.setText("修改保护密码");
        }
    }

    private void iniData() {
        context = getApplicationContext();
        is_setting_pwd = (boolean) SPUtils.get(context, "is_setting_pwd", false);
    }

    private void initEvent() {
        ll_settingpassword.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        ll_developing.setOnClickListener(this);

    }

    private void initFBI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch_finger = findViewById(R.id.switch_finger);
        ll_settingpassword = findViewById(R.id.ll_settingpassword);
        tv_set_pwd = findViewById(R.id.tv_set_pwd);
        btnShare = findViewById(R.id.btnShare);
        ll_developing= findViewById(R.id.ll_developing);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_settingpassword:
                is_setting_pwd = (boolean) SPUtils.get(context, "isSetPassword", false);
                if (is_setting_pwd) {
                    intent = new Intent(DevelopActivity.this, ModifyPasswordActivity.class);
                    startActivity(intent);
                } else {//没有设置过密码则跳转到设置密码页面
                    byte[] bs= new  byte[10000];
                    intent = new Intent(DevelopActivity.this, SettingPasswordActivity.class);
                    startActivityForResult(intent,SETTING_PASSWORD);
                }
                break;

            case R.id.btnShare:
                intent=new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,"账号册子：一个在本地记录、保存账号密码的App，增加了添加证件照的功能，保护个人隐私。    " +
                        "https://github.com/SilenceStar/AccountB");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent,"分享账号册子给朋友"));

                break;

            case R.id.ll_developing:
                intent= new Intent(DevelopActivity.this, ModifyPasswordActivity.class);
                startActivity(intent);

                break;


        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_PASSWORD && resultCode == RESULT_OK) {
            tv_set_pwd.setText("修改保护密码");
        }
    }
}
