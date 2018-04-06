package com.example.dibage.accountb.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dibage.accountb.R;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;
import com.wei.android.lib.fingerprintidentify.base.BaseFingerprint;

import es.dmoral.toasty.Toasty;

public class ValidateActivity extends AppCompatActivity {
    private Context  context;
    private TextView tv_tip;
    private ImageView imageView;
    private FingerprintIdentify mFingerprintIdentify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate);
        context = this;
        tv_tip = findViewById(R.id.tv_tip);
        imageView = findViewById(R.id.iv_block);
        tv_tip.setText("请验证指纹");

        mFingerprintIdentify = new FingerprintIdentify(this);                // 构造对象
        mFingerprintIdentify.startIdentify(3, new mFingerprintIdentifyListener());// 开始验证指纹识别
    }

   public class mFingerprintIdentifyListener implements BaseFingerprint.FingerprintIdentifyListener {

       @Override
       public void onSucceed() {
           Toasty.success(context,"验证成功").show();
           Intent intent = new Intent(ValidateActivity.this,MainActivity.class);
           startActivity(intent);
           finish();

       }

       // 指纹不匹配，并返回可用剩余次数并自动继续验证
       @Override
       public void onNotMatch(int availableTimes) {
           tv_tip.setText("指纹不匹配，还有"+availableTimes+"次机会");
       }

       // 错误次数达到上限或者API报错停止了验证，自动结束指纹识别
       // isDeviceLocked 表示指纹硬件是否被暂时锁定
       @Override
       public void onFailed(boolean isDeviceLocked) {
           tv_tip.setText("指纹解锁失败，请输入密码解锁");
           if(isDeviceLocked) {
               Toasty.error(context, "失败次数过多，指纹识别模块被暂时锁定（30秒）").show();
               mFingerprintIdentify.cancelIdentify();
           }
       }

       @Override
       public void onStartFailedByDeviceLocked() {
           Toasty.error(context, "硬件被锁定，30秒无法使用指纹识别").show();
       }
   }
}
