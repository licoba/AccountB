package com.example.dibage.accountb.activitys;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.dibage.accountb.R;
import com.example.dibage.accountb.utils.SPUtils;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;

import es.dmoral.toasty.Toasty;

public class MoreActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Switch switch_finger;
    private FingerprintIdentify mFingerprintIdentify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        mFingerprintIdentify = new FingerprintIdentify(this);
        toolbar =(Toolbar) findViewById(R.id.toolbar);
        switch_finger = findViewById(R.id.switch_finger);
//        toolbar.setTitle("偏好设置");
//        toolbar.setNavigationIcon(R.mipmap.back);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MoreActivity.this.finish();
//            }
//        });

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
                MoreActivity.this.finish();
            }
        });

        switch_finger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){
                    if(!mFingerprintIdentify.isHardwareEnable()){
                        Toasty.info(MoreActivity.this,"您的设备不支持指纹识别，无法使用此功能").show();
                    }else if(!mFingerprintIdentify.isRegisteredFingerprint()){
                        Toasty.info(MoreActivity.this,"请在您的设备中录入指纹后再使用此功能").show();
                    }else {
                        //选中的处理
                        Toasty.success(MoreActivity.this, "指纹解锁已启用").show();
                        SPUtils.put(MoreActivity.this, "finger_state", 1);
                    }
                }
                else{
                    //没有选中的处理
                    Toasty.info(MoreActivity.this,"指纹解锁已关闭").show();
                    SPUtils.put(MoreActivity.this, "finger_state", 0);
                }

            }
        });
    }


}
