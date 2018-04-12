package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.utils.SPUtils;
import com.gcssloop.encrypt.symmetric.AESUtil;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;

import javax.crypto.Cipher;

import es.dmoral.toasty.Toasty;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private Switch switch_finger;
    private FingerprintIdentify mFingerprintIdentify;
    private LinearLayout ll_settingpassword;
    private final int SETTING_PASSWORD = 1;
    private TextView tv_set_pwd;
    private boolean is_setting_pwd;
    private Context context;

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
        toolbar.setTitle("添加账号");
        //toolbar.setOnMenuItemClickListener(onMenuItemClick);

        //给toolbar的左上角的按钮注册点击监听
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (v.getId() == android.R.id.home)
                //Toast.makeText(getApplicationContext(), "点击了返回箭头", Toast.LENGTH_LONG).show();
                MoreActivity.this.finish();
            }
        });

        switch_finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (!mFingerprintIdentify.isHardwareEnable()) {
                        Toasty.info(MoreActivity.this, "您的设备不支持指纹识别，无法使用此功能").show();
                    } else if (!mFingerprintIdentify.isRegisteredFingerprint()) {
                        Toasty.info(MoreActivity.this, "请在您的设备中录入指纹后再使用此功能").show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                switch_finger.setChecked(false);
                            }
                        },500);

                    } else {
                        //选中的处理
                        Toasty.success(MoreActivity.this, "指纹解锁已启用").show();
                        SPUtils.put(MoreActivity.this, "finger_state", true);
                    }
                } else {
                    //没有选中的处理
                    if((Boolean) SPUtils.get(MoreActivity.this, "finger_state", false)) {
                        Toasty.info(MoreActivity.this, "指纹解锁已关闭").show();
                        SPUtils.put(MoreActivity.this, "finger_state", false);
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
    }

    private void initFBI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        switch_finger = findViewById(R.id.switch_finger);
        ll_settingpassword = findViewById(R.id.ll_settingpassword);
        tv_set_pwd = findViewById(R.id.tv_set_pwd);

    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.ll_settingpassword:
                is_setting_pwd = (boolean) SPUtils.get(context, "isSetPassword", false);
                if (is_setting_pwd) {
                    intent = new Intent(MoreActivity.this, ModifyPasswordActivity.class);
                    startActivity(intent);
                } else {//没有设置过密码则跳转到设置密码页面
                    byte[] bs= new  byte[10000];
                    //String aesStr = AESUtil.aes(bs, "dibage", Cipher.ENCRYPT_MODE);
                    intent = new Intent(MoreActivity.this, SettingPasswordActivity.class);
                    startActivityForResult(intent,SETTING_PASSWORD);
                }
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
