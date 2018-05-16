package com.example.dibage.accountb.activitys;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.applications.MyApplication;
import com.example.dibage.accountb.commonView.PopWindowTip;
import com.example.dibage.accountb.dao.AccountDao;
import com.example.dibage.accountb.dao.DaoSession;
import com.example.dibage.accountb.entitys.Account;
import com.example.dibage.accountb.utils.AccountUtils;
import com.example.dibage.accountb.utils.FileUtils;
import com.example.dibage.accountb.utils.SPUtils;
import com.example.dibage.accountb.utils.SimpleUtils;
import com.example.dibage.accountb.utils.UIUtils;
import com.gcssloop.encrypt.symmetric.AESUtil;
import com.google.gson.Gson;
import com.nanchen.compresshelper.FileUtil;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;

import es.dmoral.toasty.Toasty;

public class MoreActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MoreActivity";
    public static final int RECORVRY_DATA = 3;//导入账号
    private Toolbar toolbar;
    private Switch switch_finger;
    private FingerprintIdentify mFingerprintIdentify;
    private LinearLayout ll_settingpassword;
    private LinearLayout ll_developing;
    private LinearLayout ll_create_backup;
    private final int SETTING_PASSWORD = 1;//设置密码
    private final int MODIFY_PASSWORD = 2;//修改密码
    private TextView tv_set_pwd;
    private boolean is_setting_pwd;
    private boolean is_setting_finger;
    private Context context;
    private Button btnShare;
    private boolean permission_state = false;
    DaoSession daoSession;
    AccountDao mAccountDao;
    List<Account> accountsList = new ArrayList<>();
    QueryBuilder<Account> qb;
    PopWindowTip popWindowTip;
    PopWindowTip popWindowTip2;
    private LinearLayout ll_recovery;
    private LinearLayout ll_course;
    private LinearLayout ll_support;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        initFBI();
        iniData();
        initView();
        initEvent();

    }

    public class MyCheckListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (!mFingerprintIdentify.isHardwareEnable()) {
                    Toasty.info(MoreActivity.this, "您的设备不支持指纹识别，无法使用此功能").show();
                } else if (!mFingerprintIdentify.isRegisteredFingerprint()) {
                    Toasty.info(MoreActivity.this, "请在您的设备中录入指纹后再使用此功能").show();
                } else {
                    Toasty.success(MoreActivity.this, "指纹解锁已启用").show();
                    SPUtils.put(MoreActivity.this, "finger_state", true);
                }
            } else if (!isChecked) {
                popWindowTip = new PopWindowTip(MoreActivity.this) {
                    @Override
                    protected void clickCancel() {
                        switch_finger.setOnCheckedChangeListener(null);
                        switch_finger.setChecked(true);
                        switch_finger.setOnCheckedChangeListener(new MyCheckListener());

                    }

                    @Override
                    protected void dismissTodo() {
                        switch_finger.setOnCheckedChangeListener(null);
                        switch_finger.setChecked(true);
                        switch_finger.setOnCheckedChangeListener(new MyCheckListener());
                    }

                    @Override
                    public void clickConfirm() {
                        if ((Boolean) SPUtils.get(MoreActivity.this, "finger_state", false)) {
                            Toasty.info(MoreActivity.this, "指纹解锁已关闭").show();
                            SPUtils.put(MoreActivity.this, "finger_state", false);
                        }
                    }
                };
                popWindowTip.setTitleAndContent("指纹解锁", "确定关闭指纹解锁功能？");
                popWindowTip.setOutside(false);
                popWindowTip.update();

            }
        }

    }


    private void initView() {
        UIUtils.setToolbar(MoreActivity.this, toolbar, "功能与设置");
        if (is_setting_pwd) {
            tv_set_pwd.setText("修改保护密码");
        }
        if (is_setting_finger)
            switch_finger.setChecked(true);
        else
            switch_finger.setChecked(false);
    }

    private void iniData() {
        context = getApplicationContext();
        is_setting_pwd = (boolean) SPUtils.get(context, "is_setting_pwd", false);
        is_setting_finger = (boolean) SPUtils.get(context, "finger_state", false);
        daoSession = ((MyApplication) getApplication()).getDaoSession();
        mAccountDao = daoSession.getAccountDao();
        qb = mAccountDao.queryBuilder().orderAsc(AccountDao.Properties.Firstchar, AccountDao.Properties.Username);
        accountsList = qb.list();
        accountsList = AccountUtils.orderListAccount(accountsList);
    }

    private void initEvent() {
        ll_settingpassword.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        ll_developing.setOnClickListener(this);
        ll_create_backup.setOnClickListener(this);
        ll_recovery.setOnClickListener(this);
        mFingerprintIdentify = new FingerprintIdentify(this);
        switch_finger.setOnCheckedChangeListener(new MyCheckListener());
        ll_course.setOnClickListener(this);
        ll_support.setOnClickListener(this);
    }

    private void initFBI() {
        toolbar = findViewById(R.id.toolbar);
        switch_finger = findViewById(R.id.switch_finger);
        ll_settingpassword = findViewById(R.id.ll_settingpassword);
        tv_set_pwd = findViewById(R.id.tv_set_pwd);
        btnShare = findViewById(R.id.btnShare);
        ll_developing = findViewById(R.id.ll_developing);
        ll_create_backup = findViewById(R.id.ll_create_backup);
        ll_recovery = findViewById(R.id.ll_recovery);
        ll_course = findViewById(R.id.ll_course);
        ll_support = findViewById(R.id.ll_support);


    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //设置、修改保护密码
            case R.id.ll_settingpassword:
                is_setting_pwd = (boolean) SPUtils.get(context, "is_setting_pwd", false);
                if (is_setting_pwd) {//设置过密码则跳转到修改密码界面
                    intent = new Intent(MoreActivity.this, ModifyPasswordActivity.class);
                    startActivity(intent);
                } else {//没有设置过密码则跳转到设置密码页面
                    intent = new Intent(MoreActivity.this, SettingPasswordActivity.class);
                    startActivityForResult(intent, SETTING_PASSWORD);
                }
                break;

            case R.id.btnShare:
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "账号册子：一个在本地记录、保存账号密码的App，增加了添加证件照的功能，保护个人隐私。  " +
                        "https://github.com/SilenceStar/AccountB");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "分享账号册子给朋友"));
                break;

            case R.id.ll_developing:
//                intent = new Intent(MoreActivity.this, ModifyPasswordActivity.class);
//                startActivity(intent);
                Toasty.success(context,"后续会增加文件导出txt,文件导出excel等功能，敬请期待。",Toast.LENGTH_SHORT,false).show();
                break;
            case R.id.ll_course:
                Toasty.success(context,"教程还没写。",Toast.LENGTH_SHORT,false).show();
                break;
            case R.id.ll_support:
                Toasty.success(context,"谢谢你的支持。",Toast.LENGTH_SHORT,false).show();
                break;


            case R.id.ll_create_backup: //生成备份文件
                //首先，检查权限，然后检查是否 设置保护密码和是否有数据
                //全部通过的话再弹出PopWindow
                checkPermissions();
                if (permission_state) {
                    if (checkPwdData()) {
                        popWindowTip = new PopWindowTip(MoreActivity.this) {
                            @Override
                            protected void clickCancel() {
                            }

                            @Override
                            protected void dismissTodo() {

                            }

                            @Override
                            public void clickConfirm() {
                                generateBackup();
                            }
                        };
                        popWindowTip.setTitleAndContent(getString(R.string.generate_backup), getString(R.string.generate_backup_tip), "开始备份");
                    }
                }

                break;

            case R.id.ll_recovery: //备份文件恢复
                checkPermissions();
                if (permission_state) {
                    intent = new Intent(MoreActivity.this, RecorveyActivity.class);
                    startActivityForResult(intent, RECORVRY_DATA);
                }
                break;
        }
    }

    //从备份文件中恢复数据
    private void recorveyData() {

    }

    //检查是否 设置保护密码 和 是否有数据
    public boolean checkPwdData() {
        if (!is_setting_pwd) {
            Toasty.warning(context, "请先设置保护密码").show();
            return false;
        } else if (accountsList.size() <= 0) {
            Toasty.warning(context, "没有数据可以导出哦").show();
            return false;
        } else
            return true;
    }


    //检查是否拥有文件读写权限
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MoreActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            permission_state = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission_state = true;
                } else {//用户点击拒绝
                    permission_state = false;
                    Toasty.warning(context, "需要访问文件权限才能进行操作").show();
                }
                break;
        }
    }

    //生成备份文件
    private void generateBackup() {
        Log.e(TAG, accountsList.toString());
        Gson gson = new Gson();
        String jsonString = gson.toJson(accountsList);
//        Log.e(TAG,"json格式的List："+jsonString);
        String pwd = (String) SPUtils.get(context, "pwd_encrypt", "diabge");
        String key = AESUtil.aes(pwd, SimpleUtils.DEFAULT_KEY, Cipher.DECRYPT_MODE);
//        Log.e(TAG,"加密用的key："+key);
        String backupString = AESUtil.aes(jsonString, key, Cipher.ENCRYPT_MODE);
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/账号册子/";
        String path = dir + SimpleUtils.getNameByDate() + ".bkp";
        Log.e(TAG, "目录：" + dir);
        if (FileUtils.saveData(path, backupString)) {
            //Toasty.success(MoreActivity.this, "存入成功").show();
            popWindowTip2 = new PopWindowTip(MoreActivity.this) {
                @Override
                protected void clickCancel() {
                }

                @Override
                protected void dismissTodo() {

                }

                @Override
                public void clickConfirm() {
                    ;
                }
            };
            popWindowTip2.setTitleAndContent("备份成功", "文件存放目录为：\n" + path);
        } else
            Toasty.warning(MoreActivity.this, "存入失败").show();


//        Log.e(TAG,"备份文件内容：（AES加密）"+backupString);
//        String backupString1 = AESUtil.aes(backupString,key, Cipher.DECRYPT_MODE);
//        Log.e(TAG,"解密出来的内容："+backupString1);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SETTING_PASSWORD:
                    is_setting_pwd = true;
                    tv_set_pwd.setText("修改保护密码");
                break;
                case RECORVRY_DATA://备份文件导入完成
                    this.finish();
                    break;
            }
        }
    }
}
