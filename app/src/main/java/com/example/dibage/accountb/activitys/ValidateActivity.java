package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.utils.SPUtils;
import com.gcssloop.encrypt.symmetric.AESUtil;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import javax.crypto.Cipher;

import es.dmoral.toasty.Toasty;

public class ValidateActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private TextView tv_tip;
    private ImageView imageView;
    private FingerprintIdentify mFingerprintIdentify;
    private boolean finger_state = false;
    private boolean is_setting_pwd = false;

    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_0;
    private ImageButton btn_delete;
    private Button btn_exit;
    private TextView tv_number;
    StringBuffer sBuffer = new StringBuffer("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);

        init();
        initEvent();
        if (finger_state && is_setting_pwd) {
            tv_tip.setText("请验证指纹或输入密码");
            mFingerprintIdentify = new FingerprintIdentify(this);                // 构造对象
            mFingerprintIdentify.startIdentify(3, new mFingerprintIdentifyListener());// 开始验证指纹识别
        } else if ((!finger_state) && is_setting_pwd) {
            tv_tip.setText("请输入密码解锁");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (finger_state)
            mFingerprintIdentify.cancelIdentify();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (finger_state)
            mFingerprintIdentify.startIdentify(3, new mFingerprintIdentifyListener());// 开始验证指纹识别
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            default:
                sBuffer.append("");
                break;
            case R.id.btn_0:
                sBuffer.append("0");
                break;
            case R.id.btn_1:
                sBuffer.append("1");
                break;
            case R.id.btn_2:
                sBuffer.append("2");
                break;
            case R.id.btn_3:
                sBuffer.append("3");
                break;
            case R.id.btn_4:
                sBuffer.append("4");
                break;
            case R.id.btn_5:
                sBuffer.append("5");
                break;
            case R.id.btn_6:
                sBuffer.append("6");
                break;
            case R.id.btn_7:
                sBuffer.append("7");
                break;
            case R.id.btn_8:
                sBuffer.append("8");
                break;
            case R.id.btn_9:
                sBuffer.append("9");
                break;
            case R.id.btn_exit:
                //System.exit(0);  //体验不友好
                onBackPressed();
                break;
            case R.id.btn_delete:
                if (sBuffer.length() > 0)
                    sBuffer.deleteCharAt(sBuffer.length() - 1);
                break;
        }
        String s = "";
        if (sBuffer.length() > 8)
            sBuffer.deleteCharAt(sBuffer.length() - 1);
        for (int i = 1; i <= sBuffer.length(); i++)
            s += "●";
        tv_number.setText(s);

        boolean is_setting_pwd = (boolean) SPUtils.get(context, "is_setting_pwd", false);
        if (is_setting_pwd) {
            String pwd_from_sp = "";
            pwd_from_sp = (String) SPUtils.get(context, "pwd_encrypt", "默认值");
            //Toasty.info(context,"解密后的数据："+AESUtil.aes(pwd_from_sp,"dibage", Cipher.DECRYPT_MODE)).show();
            if (sBuffer.toString().equals(AESUtil.aes(pwd_from_sp, "dibage", Cipher.DECRYPT_MODE))) {
                Toasty.success(context, "验证成功").show();
                Intent intent = new Intent(ValidateActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }


    public class mFingerprintIdentifyListener implements BaseFingerprint.FingerprintIdentifyListener {

        @Override
        public void onSucceed() {
            Toasty.success(context, "验证成功").show();
            Intent intent = new Intent(ValidateActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

        // 指纹不匹配，并返回可用剩余次数并自动继续验证
        @Override
        public void onNotMatch(int availableTimes) {
            tv_tip.setText("指纹不匹配，还有" + availableTimes + "次机会");
        }

        // 错误次数达到上限或者API报错停止了验证，自动结束指纹识别
        // isDeviceLocked 表示指纹硬件是否被暂时锁定
        @Override
        public void onFailed(boolean isDeviceLocked) {
            tv_tip.setText("指纹解锁失败，请输入密码解锁");
            if (isDeviceLocked) {
                Toasty.error(context, "失败次数过多，指纹识别模块被暂时锁定（30秒）").show();
                mFingerprintIdentify.cancelIdentify();
            }
        }

        @Override
        public void onStartFailedByDeviceLocked() {
            Toasty.error(context, "硬件被锁定，30秒无法使用指纹识别").show();
        }
    }//end mFingerprintIdentifyListener


    private void init() {
        context = this;
        tv_tip = findViewById(R.id.tv_tip);
        imageView = findViewById(R.id.iv_block);
        tv_number = findViewById(R.id.tv_number);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);
        btn_4 = findViewById(R.id.btn_4);
        btn_5 = findViewById(R.id.btn_5);
        btn_6 = findViewById(R.id.btn_6);
        btn_7 = findViewById(R.id.btn_7);
        btn_8 = findViewById(R.id.btn_8);
        btn_9 = findViewById(R.id.btn_9);
        btn_exit = findViewById(R.id.btn_exit);
        btn_delete = findViewById(R.id.btn_delete);
        tv_number.setText("");
        finger_state = (boolean) SPUtils.get(context, "finger_state", false);
        is_setting_pwd = (boolean) SPUtils.get(context, "is_setting_pwd", false);
    }

    private void initEvent() {
        btn_0.setOnClickListener(this);
        btn_1.setOnClickListener(this);
        btn_2.setOnClickListener(this);
        btn_3.setOnClickListener(this);
        btn_4.setOnClickListener(this);
        btn_5.setOnClickListener(this);
        btn_6.setOnClickListener(this);
        btn_7.setOnClickListener(this);
        btn_8.setOnClickListener(this);
        btn_9.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_delete.setOnClickListener(this);
    }
}
